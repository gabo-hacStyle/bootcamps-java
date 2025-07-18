package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.Capacidad;
import gabs.tecnologias.domain.port.CapacidadRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
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
        Flux<Capacidad> result = repository.findPagedByNombreDesc(size, offset);
        result.doOnNext(r -> System.out.println("Result in repository: " + r.getNombre()))
                .subscribe(); // Solo para debug, no recomendado fuera de desarrollo
        return result;
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
    public Mono<Boolean> existsByNombre(String nombre) {
        return repository.existsByNombre(nombre);
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
