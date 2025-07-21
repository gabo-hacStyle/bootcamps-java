package gabs.tecnologias.domain.port;

import gabs.tecnologias.domain.model.CapacidadBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadBootcampRepositoryPort {
    Flux<CapacidadBootcamp> findByBootcampId(Long bootcampId);
    Mono<CapacidadBootcamp> save(CapacidadBootcamp capacidadBootcamp);
}
