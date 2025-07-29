package gabs.reports.infraestructure.adapter.out;

import gabs.reports.domain.model.Persona;
import gabs.reports.domain.port.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {
    private final SpringDataPersonaRepository repository;

   
    @Override
    public Mono<Persona> save(Persona persona) {
        return repository.save(persona);
    }

    @Override
    public Mono<Persona> findByPersonaId(Long id) {
        return repository.findByPersonaId(id);
    }

    @Override
    public Flux<Persona> findAll() {
        return repository.findAll();
    }


} 