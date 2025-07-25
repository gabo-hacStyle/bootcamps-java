package gabs.personas.infraestructure.adapter.out;

import gabs.personas.domain.model.Persona;
import gabs.personas.domain.port.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor

public class PersonaRepositoryImpl implements PersonaRepositoryPort {

    private final SpringDataPersonaRepository repository;


    @Override
    public Flux<Persona> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Persona> findById(Long id) {
        return repository.findById(id);
    }



    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByCorreo(String correo) {
        return repository.existsByCorreo(correo);
    }

    @Override
    public Mono<Persona> save(Persona persona) {
        return repository.save(persona);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
