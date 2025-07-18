package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.Capacidad;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpringDataCapacidadRepository extends ReactiveCrudRepository<Capacidad, Long> {
    Mono<Capacidad> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);

}
