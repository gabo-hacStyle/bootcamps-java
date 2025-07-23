package gabs.bootcamps.application.service;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.model.Bootcamp;

import gabs.bootcamps.domain.port.BootcampRepositoryPort;

import gabs.bootcamps.dto.BootcampRequest;
import gabs.bootcamps.dto.CapacidadDTO;

import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.PageAndQuery;
import gabs.bootcamps.infraestructure.adapter.in.CapacidadesClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
               .flatMap(bootcamp ->
                       capacidadesClient.getById(bootcamp.getId())
                               .collectList()
                               .map(capacidades -> {
                                   BootcampResponse response = new BootcampResponse();
                                   response.setNombre(bootcamp.getNombre());
                                   response.setDescripcion(bootcamp.getDescripcion());
                                   response.setId(bootcamp.getId());
                                   response.setDuracion(bootcamp.getDuracion());
                                   response.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
                                   response.setCapacidades(capacidades);
                                   return response;
                               })

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
                    bootcamp.setFechaLanzamiento(LocalDate.now());
                    bootcamp.setDuracion(request.getDuracion());

                    return repository.save(bootcamp)
                            .flatMap(saved ->
                                    capacidadesClient.postCapacidadesByBootcampId(saved.getId(), capsIds)
                                            .then(Mono.just(saved))
                            );
                });




    }


    @Override
    public Mono<Void> delete(Long id) {
        return capacidadesClient.delete(id)
                .then(repository.deleteById(id));
    }


    private Mono<List<Long>> validateCapsExist(List<Long> capacidades) {


        return Flux.fromIterable(capacidades)
                .flatMap(id ->
                        capacidadesClient.existsCapsById(id)
                                .map(exists -> new AbstractMap.SimpleEntry<>(id, exists))
                )
                .collectList()
                .flatMap(results -> {
                    List<Long> invalidIds = results.stream()
                            .filter(entry -> !entry.getValue())
                            .map(Map.Entry::getKey)
                            .toList();

                    if (!invalidIds.isEmpty()) {
                        String msg = "Las siguientes capacidades no existen: " + invalidIds;
                        return Mono.error(new IllegalArgumentException(msg));
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
            return Mono.error(new IllegalArgumentException("Debe asociar entre 1 y 4 capacidades."));
        }

        return Mono.empty();
    }

    private Mono<Void> validateDoubleCapacities(List<Long> capacidades) {
        HashSet<Long> set = new HashSet<>(capacidades);
        if (set.size() != capacidades.size()) {
            return Mono.error(new IllegalArgumentException("No se permiten tecnologías repetidas."));
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
