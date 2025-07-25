package gabs.personas.application.service;

import gabs.personas.application.port.PersonaUseCases;
import gabs.personas.domain.model.Persona;
import gabs.personas.domain.port.PersonaRepositoryPort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaService implements PersonaUseCases {

    private final PersonaRepositoryPort repository;



    @Override
    public Flux<Persona> findAll() {
        return repository.findAll();

    }

    @Override
    public Mono<Persona> findById(Long id) {


        return repository.findById(id);
    }

    @Override
    public Mono<Persona> register(Persona request) {

        return repository.existsByCorreo(request.getCorreo())
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalArgumentException("El correo ya está en uso"))
                        : repository.save(request)
                );
    }
    @Override
    public Mono<Persona> updateParcial(Long id, Persona changes) {
        return repository.findById(id)
                .flatMap(original -> {
                    if (changes.getNombre() != null) {
                        original.setNombre(changes.getNombre());
                    }
                    if (changes.getCorreo() != null) {
                        original.setCorreo(changes.getCorreo());
                    }

                    return repository.save(original);
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id){
        return repository.existsById(id);
    }

    @Override
    public Mono<Void> delete(Long id) { return repository.deleteById(id); }



}
