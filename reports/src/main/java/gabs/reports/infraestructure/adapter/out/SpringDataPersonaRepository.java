package gabs.reports.infraestructure.adapter.out;

import gabs.reports.domain.model.Persona;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


public interface SpringDataPersonaRepository extends ReactiveMongoRepository<Persona, String> {
    Mono<Persona> findByPersonaId(Long id);

}