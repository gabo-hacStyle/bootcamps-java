package gabs.bootcamps.application.service;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.event.BootcampCreatedEvent;
import gabs.bootcamps.domain.exception.BootcampNotFoundException;
import gabs.bootcamps.domain.exception.BootcampValidationException;
import gabs.bootcamps.domain.exception.ExternalServiceException;
import gabs.bootcamps.domain.exception.BootcampException;
import gabs.bootcamps.domain.model.Bootcamp;

import gabs.bootcamps.domain.port.BootcampRepositoryPort;

import gabs.bootcamps.dto.*;
import gabs.bootcamps.infraestructure.adapter.out.CapacidadesClient;
import gabs.bootcamps.infraestructure.adapter.out.ReportsClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BootcampService implements BootcampUseCases {

    private final BootcampRepositoryPort repository;
    private final CapacidadesClient capacidadesClient;
    private final ReportsClient reportsClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Flux<BootcampResponse> findAll(PageAndQuery consult) {
        int offset = consult.getSize() * consult.getPage();

        Flux<Bootcamp> bootcamps;

        if("nombre".equalsIgnoreCase(consult.getSortBy())){
            bootcamps = "desc".equalsIgnoreCase(consult.getDirection())
                    ? repository.findPagedByNombreDesc(consult.getSize(), offset)
                    : repository.findPagedByNombreAsc(consult.getSize(), offset);
        } else {
            bootcamps = repository.findPagedByNombreAsc(consult.getSize(), offset);
        }

        Flux<BootcampOrderByCapsQuantityDto> response = bootcamps.concatMap(btcmp ->
                capacidadesClient.getById(btcmp.getId())
                        .collectList()
                        .map(caps -> {
                            BootcampResponse b = new BootcampResponse();
                            b.setNombre(btcmp.getNombre());
                            b.setDescripcion(btcmp.getDescripcion());
                            b.setDuracion(btcmp.getDuracion());
                            b.setFechaLanzamiento(btcmp.getFechaLanzamiento());
                            b.setCapacidades(caps);
                            b.setId(btcmp.getId());
                            b.setFechaFinalizacion(btcmp.getFechaFinalizacion());
                            return new BootcampOrderByCapsQuantityDto(b, caps.size());
                        })
                );

        if ("cantidad".equalsIgnoreCase(consult.getSortBy())) {
            return response
                    .collectList()
                    .flatMapMany(list -> {
                        Comparator<BootcampOrderByCapsQuantityDto> comparator = Comparator.comparingInt(BootcampOrderByCapsQuantityDto::getCantidadTecnologias);
                        if ("desc".equalsIgnoreCase(consult.getDirection())) comparator = comparator.reversed();
                        list.sort(comparator);
                        // Solo retorna el response, no el DTO auxiliar
                        return Flux.fromIterable(list).map(BootcampOrderByCapsQuantityDto::getResponse);
                    });
        } else {
            return response.map(BootcampOrderByCapsQuantityDto::getResponse);
        }


    }

    @Override
    public Mono<BootcampResponse> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)))
                .flatMap(bootcamp ->
                        capacidadesClient.getById(bootcamp.getId())
                                .collectList()
                                .onErrorMap(throwable -> ExternalServiceException.capacidadesServiceError(throwable.getMessage()))
                                .map(capacidades -> {
                                    BootcampResponse response = new BootcampResponse();
                                    response.setNombre(bootcamp.getNombre());
                                    response.setDescripcion(bootcamp.getDescripcion());
                                    response.setId(bootcamp.getId());
                                    response.setDuracion(bootcamp.getDuracion());
                                    response.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
                                    response.setFechaFinalizacion(
                                            bootcamp.getFechaFinalizacion()
                                    );
                                    response.setCapacidades(capacidades);
                                    return response;
                                })
                );
    }

    @Override
    public Flux<BootcampSimpleResponse> findByIdSimpleResponse(List<Long> ids) {
        return repository.findByIds(ids)
                .flatMap(bootcamp -> {
                        BootcampSimpleResponse b = new BootcampSimpleResponse();
                        b.setDuracion(bootcamp.getDuracion());
                        b.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
                        b.setFechaFinalizacion(bootcamp.getFechaFinalizacion());
                        b.setNombre(bootcamp.getNombre());
                        b.setId(bootcamp.getId());
                        return Mono.just(b);
                }

                );
    }

    @Override
    public Mono<Bootcamp> register(BootcampRequest request) {
        return validateDoubleCapacities(request.getCapacidades())
                .then(validateCapsQuantity(request.getCapacidades()))
                .then(validateCapsExist(request.getCapacidades()))
                .flatMap(capsIds -> {
                    Bootcamp bootcamp = new Bootcamp();
                    bootcamp.setNombre(request.getNombre());
                    bootcamp.setDescripcion(request.getDescripcion());
                    bootcamp.setFechaLanzamiento(request.getFechaLanzamiento());
                    bootcamp.setDuracion(request.getDuracion());
                    bootcamp.setFechaFinalizacion(request.getFechaLanzamiento().plusDays(request.getDuracion()));

                    return repository.save(bootcamp)
                            .flatMap(saved ->
                                    capacidadesClient.postCapacidadesByBootcampId(saved.getId(), capsIds)
                                            .then(Mono.just(saved))
                            );
                })
                .doOnSuccess(savedBootcamp -> {
                    if (savedBootcamp != null) {
                        applicationEventPublisher.publishEvent(new BootcampCreatedEvent(savedBootcamp.getId()));
                    }
                });
    }


    @Override
    public Mono<Void> delete(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)))
                .flatMap(bootcamp -> 
                    capacidadesClient.delete(id)
                        .onErrorMap(throwable -> ExternalServiceException.capacidadesServiceError(throwable.getMessage()))
                        .then(repository.deleteById(id))
                        .onErrorMap(throwable -> new BootcampException("Error al eliminar el bootcamp", HttpStatus.INTERNAL_SERVER_ERROR))
                );
    }


    private Mono<List<Long>> validateCapsExist(List<Long> capacidades) {
        return Flux.fromIterable(capacidades)
                .flatMap(id ->
                        capacidadesClient.existsCapsById(id)
                                .onErrorMap(throwable -> ExternalServiceException.capacidadesServiceError(throwable.getMessage()))
                                .map(exists -> new AbstractMap.SimpleEntry<>(id, exists))
                )
                .collectList()
                .flatMap(results -> {
                    List<Long> invalidIds = results.stream()
                            .filter(entry -> !entry.getValue())
                            .map(Map.Entry::getKey)
                            .toList();

                    if (!invalidIds.isEmpty()) {
                        return Mono.error(BootcampValidationException.capacidadesNotFound(invalidIds.toString()));
                    }

                    // Solo devuelve los ids válidos (o todos, si todos existen)
                    List<Long> validIds = results.stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    return Mono.just(validIds);
                });
    }


    private Mono<Void> validateCapsQuantity(List<Long> capacidades) {
        if (capacidades == null || capacidades.size() < 1 || capacidades.size() > 4) {
            return Mono.error(BootcampValidationException.invalidCapacidadesQuantity());
        }

        return Mono.empty();
    }

    private Mono<Void> validateDoubleCapacities(List<Long> capacidades) {
        HashSet<Long> set = new HashSet<>(capacidades);
        if (set.size() != capacidades.size()) {
            return Mono.error(BootcampValidationException.duplicateCapacidades());
        }
        return Mono.empty();
    }



    @Data
    @AllArgsConstructor
    private class BootcampOrderByCapsQuantityDto {
        private BootcampResponse response;
        private int cantidadTecnologias;
    }




}
