package gabs.reports.application.service;

import gabs.reports.application.port.PersonaUseCases;
import gabs.reports.domain.exception.PersonaNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Persona;
import gabs.reports.domain.port.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaService implements PersonaUseCases {
    private final PersonaRepositoryPort repository;

  
    /**
     * Guarda una nueva persona en la base de datos.
     * @param persona Objeto Persona a guardar
     * @return Mono con la persona guardada
     */
    public Mono<Persona> save(Persona persona) {
        if (persona == null) {
            return Mono.error(new ValidationException("Persona no puede ser nula"));
        }
        if (persona.getNombre() == null || persona.getNombre().trim().isEmpty()) {
            return Mono.error(new ValidationException("nombre", "No puede estar vacío"));
        }
        if (persona.getCorreo() == null || persona.getCorreo().trim().isEmpty()) {
            return Mono.error(new ValidationException("correo", "No puede estar vacío"));
        }
        if (persona.getEdad() == null || persona.getEdad() <= 0) {
            return Mono.error(new ValidationException("edad", "Debe ser mayor a 0"));
        }
        
        log.info("Guardando persona: {}", persona.getNombre());
        
        return repository.save(persona)
                .doOnSuccess(saved -> log.info("Persona guardada exitosamente: {}", saved.getNombre()))
                .doOnError(error -> log.error("Error al guardar persona: {}", error.getMessage()));
    }

    /**
     * Busca una persona por su ID.
     * @param id ID de la persona
     * @return Mono con la persona encontrada o vacío si no existe
     */
    public Mono<Persona> findById(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("ID de la persona no puede ser nulo"));
        }
        
        log.info("Buscando persona con ID: {}", id);
        
        return repository.findByPersonaId(id)
                .doOnSuccess(persona -> {
                    if (persona != null) {
                        log.info("Persona encontrada: {}", persona.getNombre());
                    }
                })
                .switchIfEmpty(Mono.error(new PersonaNotFoundException(id)));
    }

    /**
     * Lista todas las personas registradas.
     * @return Flux con todas las personas
     */
    public Flux<Persona> findAll() {
        log.info("Listando todas las personas");
        
        return repository.findAll()
                .doOnComplete(() -> log.info("Listado de personas completado"))
                .doOnError(error -> log.error("Error al listar personas: {}", error.getMessage()));
    }

} 