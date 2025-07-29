package gabs.reports.application.service;

import gabs.reports.application.port.PersonaUseCases;
import gabs.reports.domain.model.Persona;
import gabs.reports.domain.port.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonaService implements PersonaUseCases {
    private final PersonaRepositoryPort repository;

  
    /**
     * Guarda una nueva persona en la base de datos.
     * @param persona Objeto Persona a guardar
     * @return Mono con la persona guardada
     */
    public Mono<Persona> save(Persona persona) {
        return repository.save(persona);
    }

    /**
     * Busca una persona por su ID.
     * @param id ID de la persona
     * @return Mono con la persona encontrada o vacío si no existe
     */
    public Mono<Persona> findById(Long id) {
        return repository.findByPersonaId(id);
    }

    /**
     * Lista todas las personas registradas.
     * @return Flux con todas las personas
     */
    public Flux<Persona> findAll() {
        return repository.findAll();
    }

} 