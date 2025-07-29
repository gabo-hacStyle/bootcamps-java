package gabs.reports.infraestructure.adapter.out;

import gabs.reports.domain.model.Inscripcion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


public interface SpringDataInscripcionRepository extends ReactiveMongoRepository<Inscripcion, Long> {
    Flux<Inscripcion> findByBootcampId(Long bootcampId);
    Flux<Inscripcion> findByPersonaId(Long personaId);
} 