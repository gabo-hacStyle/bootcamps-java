package gabs.personas.application.port;


import gabs.personas.domain.model.Persona;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonaUseCases {

     Flux<Persona> findAll();
     Mono<Persona> findById(Long id);
     Mono<Persona> register(Persona request);
     Mono<Persona> updateParcial(Long id, Persona persona);
     Mono<Boolean> existsById(Long id);
     Mono<Void> delete(Long id);

}
