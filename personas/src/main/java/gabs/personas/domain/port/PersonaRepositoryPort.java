package gabs.personas.domain.port;

import gabs.personas.domain.model.Persona;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonaRepositoryPort {

    //Flux<Persona> findPagedByNombreAsc(int size, int offset);
//
    //Flux<Persona> findPagedByNombreDesc(int size, int offset);

    Flux<Persona> findAll();
    Mono<Persona> findById(Long id);
    //Mono<Persona> findByNombre(String nombre);
    Mono<Boolean> existsById(Long id);
    Mono<Boolean> existsByCorreo(String correo);
    Mono<Persona>  save(Persona persona);
    Mono<Void> deleteById(Long id);
}
