package gabs.reports.application.service;

import gabs.reports.application.port.InscripcionUseCases;
import gabs.reports.domain.model.Inscripcion;
import gabs.reports.domain.event.PersonaInscritaEnBootcampEvent;
import gabs.reports.domain.port.InscripcionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class InscripcionService implements InscripcionUseCases {
    private final InscripcionRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Inscripcion> save(Inscripcion inscripcion) {
        return repository.save(inscripcion)
            .doOnSuccess(saved ->
                eventPublisher.publishEvent(
                    new PersonaInscritaEnBootcampEvent(saved.getBootcampId(), saved.getPersonaId())
                )
            );
    }

    /**
     * Busca una inscripción por su ID.
     * @param id ID de la inscripción
     * @return Mono con la inscripción encontrada o vacío si no existe
     */
    public Mono<Inscripcion> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Lista todas las inscripciones registradas.
     * @return Flux con todas las inscripciones
     */
    public Flux<Inscripcion> findAll() {
        return repository.findAll();
    }

    /**
     * Elimina una inscripción por su ID.
     * @param id ID de la inscripción
     * @return Mono vacío al completar la eliminación
     */
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    /**
     * Busca todas las inscripciones de un bootcamp.
     * @param bootcampId ID del bootcamp
     * @return Flux con las inscripciones encontradas
     */
    public Flux<Inscripcion> findByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId);
    }

    /**
     * Busca todas las inscripciones de una persona.
     * @param personaId ID de la persona
     * @return Flux con las inscripciones encontradas
     */
    public Flux<Inscripcion> findByPersonaId(Long personaId) {
        return repository.findByPersonaId(personaId);
    }
} 