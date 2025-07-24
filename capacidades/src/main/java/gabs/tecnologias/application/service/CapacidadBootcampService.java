package gabs.tecnologias.application.service;

import gabs.tecnologias.application.port.CapacidadBootcampUseCases;
import gabs.tecnologias.domain.model.CapacidadBootcamp;
import gabs.tecnologias.domain.port.CapacidadBootcampRepositoryPort;
import gabs.tecnologias.domain.port.CapacidadRepositoryPort;
import gabs.tecnologias.dto.CapacidadBootcampResponse;
import gabs.tecnologias.infraestructure.adapter.in.TecnologiaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                .flatMap(capacidadBootcamp ->
                        capacidadRepository.findById(capacidadBootcamp.getCapacidadId())
                                .flatMap(capacidad ->
                                        tecnologiaClient.getTecnologiasByCapacidadId(capacidad.getId())
                                                .collectList()
                                                .map(tecnologias -> {
                                                    System.out.println("Tecnologias" + tecnologias);
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

        return Flux.fromIterable(capacidadesList)
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
        return repository.findExclusiveCapacidadesOfBootcamp(bootcampId)
                .collectList()
                .flatMap(ids->
                        tecnologiaClient.deleteTechnologiasByCapacidadDeleted(ids)
                                .thenMany(Flux.fromIterable(ids)
                                        .flatMap(capacidadRepository::deleteById)
                                )
                        .then(repository.deleteByBootcampId(bootcampId))
                                .then()
                );
    }
}
