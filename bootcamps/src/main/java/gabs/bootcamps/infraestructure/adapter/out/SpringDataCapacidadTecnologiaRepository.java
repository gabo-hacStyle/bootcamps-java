package gabs.bootcamps.infraestructure.adapter.out;

import gabs.bootcamps.domain.model.CapacidadTecnologia;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataCapacidadTecnologiaRepository extends ReactiveCrudRepository<CapacidadTecnologia, Long> {
    Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId);
    Mono<Void> deleteByCapacidadId(Long capacidadId);
}
