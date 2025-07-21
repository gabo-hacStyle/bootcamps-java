package gabs.bootcamps.infraestructure.adapter.out;

import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.domain.port.BootcampRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor

public class BootcampRepositoryImpl implements BootcampRepositoryPort {

    private final SpringDataCapacidadRepository repository;




    @Override
    public Flux<Bootcamp> findPagedByNombreAsc(int size, int offset) {
        return repository.findPagedByNombreAsc(size, offset);
    }

    @Override
    public Flux<Bootcamp> findPagedByNombreDesc(int size, int offset) {
        Flux<Bootcamp> result = repository.findPagedByNombreDesc(size, offset);
        result.doOnNext(r -> System.out.println("Result in repository: " + r.getNombre()))
                .subscribe(); // Solo para debug, no recomendado fuera de desarrollo
        return result;
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Bootcamp> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        return repository.existsByNombre(nombre);
    }

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return repository.save(bootcamp);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
