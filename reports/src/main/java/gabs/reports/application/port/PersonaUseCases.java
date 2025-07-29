package gabs.reports.application.port;

import gabs.reports.domain.model.Persona;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonaUseCases {
    Mono<Persona> save(Persona persona);
    Mono<Persona> findById(Long id);
    Flux<Persona> findAll();

} 