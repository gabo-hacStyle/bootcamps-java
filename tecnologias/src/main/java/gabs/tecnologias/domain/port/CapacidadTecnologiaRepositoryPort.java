package gabs.tecnologias.domain.port;

import gabs.tecnologias.domain.model.CapacidadTecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadTecnologiaRepositoryPort {
    Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId);
    Mono<CapacidadTecnologia> save(CapacidadTecnologia capacidadTecnologia);
    Mono<Void> deleteByCapacidadId(Long capacidadId);
}
