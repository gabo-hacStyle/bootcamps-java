package gabs.reports.domain.port;

import gabs.reports.domain.model.Persona;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonaRepositoryPort {
    Mono<Persona> save(Persona persona);
    Mono<Persona> findByPersonaId(Long id);
    Flux<Persona> findAll();

} 