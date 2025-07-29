package gabs.reports.domain.port;

import gabs.reports.domain.model.Inscripcion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InscripcionRepositoryPort {
    Mono<Inscripcion> save(Inscripcion inscripcion);
    Mono<Inscripcion> findById(Long id);
    Flux<Inscripcion> findAll();
    Mono<Void> deleteById(Long id);
    Flux<Inscripcion> findByBootcampId(Long bootcampId);
    Flux<Inscripcion> findByPersonaId(Long personaId);
} 