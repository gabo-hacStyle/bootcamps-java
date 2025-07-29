package gabs.reports.infraestructure.adapter.out;

import gabs.reports.domain.model.Inscripcion;
import gabs.reports.domain.port.InscripcionRepositoryPort;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class InscripcionRepositoryAdapter implements InscripcionRepositoryPort {
    private final SpringDataInscripcionRepository repository;


    @Override
    public Mono<Inscripcion> save(Inscripcion inscripcion) {
        return repository.save(inscripcion);
    }

    @Override
    public Mono<Inscripcion> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Inscripcion> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Inscripcion> findByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId);
    }

    @Override
    public Flux<Inscripcion> findByPersonaId(Long personaId) {
        return repository.findByPersonaId(personaId);
    }
} 