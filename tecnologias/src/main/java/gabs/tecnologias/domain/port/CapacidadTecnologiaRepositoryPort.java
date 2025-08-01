package gabs.tecnologias.domain.port;

import gabs.tecnologias.domain.model.CapacidadTecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadTecnologiaRepositoryPort {
    Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId);
    Mono<CapacidadTecnologia> save(CapacidadTecnologia capacidadTecnologia);
    Flux<Long> findExclusiveTechsByCapacidadesIds(List<Long> capacidadesId);
    Mono<Void> deleteByCapacidadesIds(List<Long> capacidadesIds);
}
