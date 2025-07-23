package gabs.tecnologias.application.service;

import dto.CapacidadTecnologiaResponse;
import gabs.tecnologias.application.port.CapacidadTecnologiaUseCases;
import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.domain.port.CapacidadTecnologiaRepositoryPort;
import gabs.tecnologias.domain.port.TecnologiaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacidadTecnologiaService implements CapacidadTecnologiaUseCases {

    private final CapacidadTecnologiaRepositoryPort repository;
    private final TecnologiaRepositoryPort tecnologiaRepository;


    @Override
    public Flux<CapacidadTecnologiaResponse> getTechnologiesListByCapacidad(Long id) {
        return repository.findByCapacidadId(id)
                .flatMap(tecnologiaCapacidad ->
                        tecnologiaRepository.findById(tecnologiaCapacidad.getTecnologiaId())
                                .map(tecnologia -> {
                                    CapacidadTecnologiaResponse response = new CapacidadTecnologiaResponse();
                                    response.setId(tecnologia.getId());
                                    response.setNombre(tecnologia.getNombre());
                                    return response;
                                })
                );
    }

    @Override
    public Flux<CapacidadTecnologia> register(Long capacidadId, List<Long> tecnologiaIds) {
        return Flux.fromIterable(tecnologiaIds)
                .map(tecnologiaId -> {
                    CapacidadTecnologia entity = new CapacidadTecnologia();
                    entity.setCapacidadId(capacidadId);
                    entity.setTecnologiaId(tecnologiaId);
                    return entity;
                })
                .flatMap(repository::save);
    }

    @Override
    public Mono<Void> deleteTecnologiasByCapacidadId(Long capacidadId) {
        return repository.findExclusiveTechsOfCapacidad(capacidadId)
                .flatMap(tecnologiaRepository::deleteById)
                .then();
    }

}
