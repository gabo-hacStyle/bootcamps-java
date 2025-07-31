package gabs.capacidades.application.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import gabs.capacidades.application.port.CapacidadUseCases;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.domain.port.CapacidadRepositoryPort;
import gabs.capacidades.dto.CapacidadRequest;
import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.CapacidadTecnologiasRequest;
import gabs.capacidades.dto.PageAndQuery;
import gabs.capacidades.infraestructure.adapter.in.TecnologiaClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacidadService implements CapacidadUseCases {

    private final CapacidadRepositoryPort repository;
    private final TecnologiaClient tecnologiaClient;

    @Override
    public Flux<CapacidadResponse> findAll(PageAndQuery consult) {
        int offset = consult.getSize() * consult.getPage();

        Flux<Capacidad> capacidades;
        if ("nombre".equalsIgnoreCase(consult.getSortBy())) {
            capacidades = "desc".equalsIgnoreCase(consult.getDirection())
                    ? repository.findPagedByNombreDesc(consult.getSize(), offset)
                    : repository.findPagedByNombreAsc(consult.getSize(), offset);
        } else {
            // Si el sortBy es cantidad, primero traemos capacidades paginadas por nombre (puedes elegir), luego ordenamos en memoria
            capacidades = repository.findPagedByNombreAsc(consult.getSize(), offset);
        }

        Flux<CapacidadOrderByTechsQuantityDto> responses = capacidades.concatMap(capacidad ->
                tecnologiaClient.getTecnologiasByCapacidadId(capacidad.getId())
                        .collectList()
                        .map(tecnologias -> {
                            CapacidadResponse r = new CapacidadResponse();
                            r.setNombre(capacidad.getNombre());
                            r.setDescripcion(capacidad.getDescripcion());
                            r.setTecnologiasList(tecnologias);
                            r.setId(capacidad.getId());
                            return new CapacidadOrderByTechsQuantityDto(r, tecnologias.size());
                        })
        );

        if ("cantidad".equalsIgnoreCase(consult.getSortBy())) {
            return responses
                    .collectList()
                    .flatMapMany(list -> {
                        Comparator<CapacidadOrderByTechsQuantityDto> comparator = Comparator.comparingInt(CapacidadOrderByTechsQuantityDto::getCantidadTecnologias);
                        if ("desc".equalsIgnoreCase(consult.getDirection())) comparator = comparator.reversed();
                        list.sort(comparator);
                        // Solo retorna el response, no el DTO auxiliar
                        return Flux.fromIterable(list).map(CapacidadOrderByTechsQuantityDto::getResponse);
                    });
        } else {
            return responses.map(CapacidadOrderByTechsQuantityDto::getResponse);
        }


    }

    @Override
    public Mono<CapacidadResponse> findById(Long id) {

        // Paso 1: Busca la capacidad
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CapacidadNotFoundException(id)))
                .flatMap(capacidad ->
                        tecnologiaClient.getTecnologiasByCapacidadId(capacidad.getId())
                                .collectList()
                                .map(tecnologias -> {
                                    CapacidadResponse response = new CapacidadResponse();
                                    response.setNombre(capacidad.getNombre());
                                    response.setDescripcion(capacidad.getDescripcion());
                                    response.setId(capacidad.getId());
                                    response.setTecnologiasList(tecnologias);
                                    return response;
                                })
                );
    }
    @Override
    public Mono<Capacidad> register(CapacidadRequest request) {
        return validateTechQuantity(request)
                .then(validateDoubleTechs(request))
                .then(validateTechsExist(request))
                .flatMap(validIds -> {
                    Capacidad capacidad = new Capacidad();
                    capacidad.setNombre(request.getNombre());
                    capacidad.setDescripcion(request.getDescripcion());

                    return repository.save(capacidad)
                            .flatMap(saved ->
                                    tecnologiaClient.postTecnologiasByCapacidadId(saved.getId(), new CapacidadTecnologiasRequest(validIds))
                                            .then(Mono.just(saved))
                            );
                });
    }
    @Override
    public Mono<Capacidad> updateParcial(Long id, CapacidadRequest changes) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CapacidadNotFoundException(id)))
                .flatMap(original -> {
                    if (changes.getNombre() != null) {
                        original.setNombre(changes.getNombre());
                    }
                    if (changes.getDescripcion() != null) {
                        original.setDescripcion(changes.getDescripcion());
                    }

                    return repository.save(original);
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id){
        return repository.existsById(id);
    }

    @Override
    public Mono<Void> delete(Long id) { 
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CapacidadNotFoundException(id)))
                .then(repository.deleteById(id));
    }


    private Mono<Void> validateTechQuantity(CapacidadRequest request) {
        if (request.getTecnologias() == null ||
                request.getTecnologias().size() < 3 ||
                request.getTecnologias().size() > 20) {
            return Mono.error(new ValidationException("La capacidad debe tener entre 3 y 20 tecnologías."));
        }
        return Mono.empty();
    }

    private Mono<Void> validateDoubleTechs(CapacidadRequest request) {
        HashSet<Long> set = new HashSet<>(request.getTecnologias());
        if (set.size() != request.getTecnologias().size()) {
            return Mono.error(new ValidationException("No se permiten tecnologías repetidas."));
        }
        return Mono.empty();
    }

    private Mono<List<Long>> validateTechsExist(CapacidadRequest request) {
        List<Long> requestedIds = request.getTecnologias();

        return Flux.fromIterable(requestedIds)
                .flatMap(id ->
                        tecnologiaClient.existsTechById(id)
                                .map(exists -> new AbstractMap.SimpleEntry<>(id, exists))
                )
                .collectList()
                .flatMap(results -> {
                    List<Long> invalidIds = results.stream()
                            .filter(entry -> !entry.getValue())
                            .map(Map.Entry::getKey)
                            .toList();

                    if (!invalidIds.isEmpty()) {
                        String msg = "Las siguientes tecnologías no existen: " + invalidIds;
                        return Mono.error(new ValidationException(msg));
                    }

                    // Solo devuelve los ids válidos (o todos, si todos existen)
                    List<Long> validIds = results.stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    return Mono.just(validIds);
                });
    }

    @Data
    @AllArgsConstructor
    private class CapacidadOrderByTechsQuantityDto {
        private CapacidadResponse response;
        private int cantidadTecnologias;
    }
}
