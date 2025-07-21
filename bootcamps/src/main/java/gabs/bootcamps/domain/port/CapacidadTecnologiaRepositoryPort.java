package gabs.bootcamps.domain.port;

import gabs.bootcamps.domain.model.CapacidadTecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadTecnologiaRepositoryPort {

    Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId);
    Mono<CapacidadTecnologia> save(CapacidadTecnologia capacidadTecnologia);
    Mono<Void> deleteByCapacidadId(Long capacidadId);
}
