package gabs.reports.application.service;

import gabs.reports.application.port.InscripcionUseCases;
import gabs.reports.domain.exception.InscripcionNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Inscripcion;
import gabs.reports.domain.event.PersonaInscritaEnBootcampEvent;
import gabs.reports.domain.port.InscripcionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class InscripcionService implements InscripcionUseCases {
    private final InscripcionRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Inscripcion> save(Inscripcion inscripcion) {
        if (inscripcion == null) {
            return Mono.error(new ValidationException("Inscripción no puede ser nula"));
        }
        if (inscripcion.getPersonaId() == null) {
            return Mono.error(new ValidationException("personaId", "No puede ser nulo"));
        }
        if (inscripcion.getBootcampId() == null) {
            return Mono.error(new ValidationException("bootcampId", "No puede ser nulo"));
        }
        if (inscripcion.getNombrePersona() == null || inscripcion.getNombrePersona().trim().isEmpty()) {
            return Mono.error(new ValidationException("nombrePersona", "No puede estar vacío"));
        }
        if (inscripcion.getCorreoPersona() == null || inscripcion.getCorreoPersona().trim().isEmpty()) {
            return Mono.error(new ValidationException("correoPersona", "No puede estar vacío"));
        }
        if (inscripcion.getNombreBootcamp() == null || inscripcion.getNombreBootcamp().trim().isEmpty()) {
            return Mono.error(new ValidationException("nombreBootcamp", "No puede estar vacío"));
        }
        
        log.info("Guardando inscripción: Persona {} en Bootcamp {}", 
                inscripcion.getNombrePersona(), inscripcion.getNombreBootcamp());
        
        return repository.save(inscripcion)
            .doOnSuccess(saved -> {
                log.info("Inscripción guardada exitosamente: ID {}", saved.getId());
                eventPublisher.publishEvent(
                    new PersonaInscritaEnBootcampEvent(saved.getBootcampId(), saved.getPersonaId())
                );
            })
            .doOnError(error -> log.error("Error al guardar inscripción: {}", error.getMessage()));
    }

    /**
     * Busca una inscripción por su ID.
     * @param id ID de la inscripción
     * @return Mono con la inscripción encontrada o vacío si no existe
     */
    public Mono<Inscripcion> findById(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("ID de la inscripción no puede ser nulo"));
        }
        
        log.info("Buscando inscripción con ID: {}", id);
        
        return repository.findById(id)
                .doOnSuccess(inscripcion -> {
                    if (inscripcion != null) {
                        log.info("Inscripción encontrada: ID {}", inscripcion.getId());
                    }
                })
                .switchIfEmpty(Mono.error(new InscripcionNotFoundException(id)));
    }

    /**
     * Lista todas las inscripciones registradas.
     * @return Flux con todas las inscripciones
     */
    public Flux<Inscripcion> findAll() {
        log.info("Listando todas las inscripciones");
        
        return repository.findAll()
                .doOnComplete(() -> log.info("Listado de inscripciones completado"))
                .doOnError(error -> log.error("Error al listar inscripciones: {}", error.getMessage()));
    }

    /**
     * Elimina una inscripción por su ID.
     * @param id ID de la inscripción
     * @return Mono vacío al completar la eliminación
     */
    public Mono<Void> deleteById(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("ID de la inscripción no puede ser nulo"));
        }
        
        log.info("Eliminando inscripción con ID: {}", id);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new InscripcionNotFoundException(id)))
                .flatMap(inscripcion -> {
                    log.info("Inscripción encontrada para eliminar: ID {}", inscripcion.getId());
                    return repository.deleteById(id);
                })
                .doOnSuccess(result -> log.info("Inscripción eliminada exitosamente: ID {}", id))
                .doOnError(error -> log.error("Error al eliminar inscripción: {}", error.getMessage()));
    }

    /**
     * Busca todas las inscripciones de un bootcamp.
     * @param bootcampId ID del bootcamp
     * @return Flux con las inscripciones encontradas
     */
    public Flux<Inscripcion> findByBootcampId(Long bootcampId) {
        if (bootcampId == null) {
            return Flux.error(new ValidationException("ID del bootcamp no puede ser nulo"));
        }
        
        log.info("Buscando inscripciones para bootcamp ID: {}", bootcampId);
        
        return repository.findByBootcampId(bootcampId)
                .doOnComplete(() -> log.info("Búsqueda de inscripciones por bootcamp completada"))
                .doOnError(error -> log.error("Error al buscar inscripciones por bootcamp: {}", error.getMessage()));
    }

    /**
     * Busca todas las inscripciones de una persona.
     * @param personaId ID de la persona
     * @return Flux con las inscripciones encontradas
     */
    public Flux<Inscripcion> findByPersonaId(Long personaId) {
        if (personaId == null) {
            return Flux.error(new ValidationException("ID de la persona no puede ser nulo"));
        }
        
        log.info("Buscando inscripciones para persona ID: {}", personaId);
        
        return repository.findByPersonaId(personaId)
                .doOnComplete(() -> log.info("Búsqueda de inscripciones por persona completada"))
                .doOnError(error -> log.error("Error al buscar inscripciones por persona: {}", error.getMessage()));
    }
} 