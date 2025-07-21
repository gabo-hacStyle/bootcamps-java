package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.CapacidadBootcamp;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SpringDataCapacidadBootcampRepository extends ReactiveCrudRepository<CapacidadBootcamp, Long> {
    Flux<CapacidadBootcamp> findByBootcampId(Long bootcampId);
}
