package gabs.tecnologias.application.service;

import gabs.tecnologias.application.port.CapacidadTecnologiaUseCases;
import gabs.tecnologias.domain.exception.CapacidadTecnologiaNotFoundException;
import gabs.tecnologias.domain.exception.TecnologiaNotFoundException;
import gabs.tecnologias.domain.exception.ValidationException;
import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.domain.port.CapacidadTecnologiaRepositoryPort;
import gabs.tecnologias.domain.port.TecnologiaRepositoryPort;
import gabs.tecnologias.dto.CapacidadTecnologiaResponse;
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
        if (id == null) {
            return Flux.error(new ValidationException("id", "El ID de capacidad no puede ser nulo"));
        }
        
        return repository.findByCapacidadId(id)
                .switchIfEmpty(Flux.error(new CapacidadTecnologiaNotFoundException(id)))
                .flatMap(tecnologiaCapacidad ->
                        tecnologiaRepository.findById(tecnologiaCapacidad.getTecnologiaId())
                                .switchIfEmpty(Mono.error(new TecnologiaNotFoundException(tecnologiaCapacidad.getTecnologiaId())))
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
        if (capacidadId == null) {
            return Flux.error(new ValidationException("capacidadId", "El ID de capacidad no puede ser nulo"));
        }
        if (tecnologiaIds == null || tecnologiaIds.isEmpty()) {
            return Flux.error(new ValidationException("tecnologiaIds", "La lista de IDs de tecnologías no puede estar vacía"));
        }
        
        return Flux.fromIterable(tecnologiaIds)
                .flatMap(tecnologiaId -> 
                    tecnologiaRepository.findById(tecnologiaId)
                        .switchIfEmpty(Mono.error(new TecnologiaNotFoundException(tecnologiaId)))
                        .map(tecnologia -> {
                            CapacidadTecnologia entity = new CapacidadTecnologia();
                            entity.setCapacidadId(capacidadId);
                            entity.setTecnologiaId(tecnologiaId);
                            return entity;
                        })
                )
                .flatMap(repository::save);
    }

    @Override
    public Mono<Void> deleteCapacidadesByCapacidadesIds(List<Long> capacidadesIds) {
        if (capacidadesIds == null || capacidadesIds.isEmpty()) {
            return Mono.error(new ValidationException("capacidadesIds", "La lista de IDs de capacidades no puede estar vacía"));
        }
        
        return repository.findExclusiveTechsByCapacidadesIds(capacidadesIds)
                .collectList()
                .flatMap(tecnologiaRepository::deleteAllById)
                .then(repository.deleteByCapacidadesIds(capacidadesIds))
                .then();
    }

}
