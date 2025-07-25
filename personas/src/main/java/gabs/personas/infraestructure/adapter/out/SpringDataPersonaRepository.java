package gabs.personas.infraestructure.adapter.out;

import gabs.personas.domain.model.Persona;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataPersonaRepository extends ReactiveCrudRepository<Persona, Long> {
    Mono<Persona> findByNombre(String nombre);
    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByCorreo(String correo);


}
