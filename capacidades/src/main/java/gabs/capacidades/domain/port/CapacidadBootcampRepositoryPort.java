package gabs.capacidades.domain.port;

import gabs.capacidades.domain.model.CapacidadBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadBootcampRepositoryPort {
    Flux<CapacidadBootcamp> findByBootcampId(Long bootcampId);
    Mono<CapacidadBootcamp> save(CapacidadBootcamp capacidadBootcamp);
    Flux<Long> findExclusiveCapacidadesOfBootcamp(Long bootcampId);
    Mono<Void> deleteByBootcampId(Long bootcampId);
}
