package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.Tecnologia;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpringDataTecnologiaRepository extends ReactiveCrudRepository<Tecnologia, Long> {
    Mono<Tecnologia> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);

}
