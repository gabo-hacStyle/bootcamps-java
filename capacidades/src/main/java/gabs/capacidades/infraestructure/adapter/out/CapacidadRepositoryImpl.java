package gabs.capacidades.infraestructure.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.domain.port.CapacidadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor

public class CapacidadRepositoryImpl implements CapacidadRepositoryPort {

    private final SpringDataCapacidadRepository repository;




    @Override
    public Flux<Capacidad> findPagedByNombreAsc(int size, int offset) {
        return repository.findPagedByNombreAsc(size, offset);
    }

    @Override
    public Flux<Capacidad> findPagedByNombreDesc(int size, int offset) {
        return repository.findPagedByNombreDesc(size, offset);

    }

    @Override
    public Mono<Capacidad> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Capacidad> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Capacidad> save(Capacidad capacidad) {
        return repository.save(capacidad);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
