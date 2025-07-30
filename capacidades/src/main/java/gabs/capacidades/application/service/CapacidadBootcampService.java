package gabs.capacidades.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import gabs.capacidades.application.port.CapacidadBootcampUseCases;
import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.CapacidadBootcamp;
import gabs.capacidades.domain.port.CapacidadBootcampRepositoryPort;
import gabs.capacidades.domain.port.CapacidadRepositoryPort;
import gabs.capacidades.dto.CapacidadBootcampResponse;
import gabs.capacidades.infraestructure.adapter.in.TecnologiaClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacidadBootcampService implements CapacidadBootcampUseCases {

    private final CapacidadBootcampRepositoryPort repository;
    private final CapacidadRepositoryPort capacidadRepository;
    private final TecnologiaClient tecnologiaClient;

    @Override
    public Flux<CapacidadBootcampResponse> getAllByBootcamp(Long bootcampId) {
        return repository.findByBootcampId(bootcampId)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(bootcampId)))
                .flatMap(capacidadBootcamp ->
                        capacidadRepository.findById(capacidadBootcamp.getCapacidadId())
                                .switchIfEmpty(Mono.error(new CapacidadNotFoundException(capacidadBootcamp.getCapacidadId())))
                                .flatMap(capacidad ->
                                        tecnologiaClient.getTecnologiasByCapacidadId(capacidad.getId())
                                                .collectList()
                                                .map(tecnologias -> {
                                                    CapacidadBootcampResponse response = new CapacidadBootcampResponse();
                                                    response.setId(capacidad.getId());
                                                    response.setNombre(capacidad.getNombre());
                                                    response.setTecnologias(tecnologias);
                                                    return response;
                                                })
                                )
                );
    }

    @Override
    public Flux<CapacidadBootcamp> saveCapacidadBootcamp(Long bootcampId, List<Long> capacidadesList) {
        if (capacidadesList == null || capacidadesList.isEmpty()) {
            return Flux.error(new ValidationException("La lista de capacidades no puede estar vacía"));
        }

        return Flux.fromIterable(capacidadesList)
                .flatMap(capacidadId -> capacidadRepository.findById(capacidadId)
                        .switchIfEmpty(Mono.error(new CapacidadNotFoundException(capacidadId)))
                        .then(Mono.just(capacidadId)))
                .map(capacidadId -> {
                    CapacidadBootcamp entity = new CapacidadBootcamp();
                    entity.setBootcampId(bootcampId);
                    entity.setCapacidadId(capacidadId);
                    return entity;
                })
                .flatMap(repository::save);
    }

    @Override
    public Mono<Void> deleteCapacidadesByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId)
                .collectList()
                .flatMap(capacidades -> {
                    if (capacidades.isEmpty()) {
                        return Mono.error(new BootcampNotFoundException(bootcampId));
                    }
                    return repository.findExclusiveCapacidadesOfBootcamp(bootcampId)
                            .collectList()
                            .flatMap(ids ->
                                    tecnologiaClient.deleteTechnologiasByCapacidadDeleted(ids)
                                            .thenMany(Flux.fromIterable(ids)
                                                    .flatMap(capacidadRepository::deleteById)
                                            )
                                            .then(repository.deleteByBootcampId(bootcampId))
                                            .then()
                            );
                });
    }
}
