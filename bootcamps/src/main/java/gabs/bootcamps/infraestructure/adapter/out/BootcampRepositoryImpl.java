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
        return repository.findPagedByNombreDesc(size, offset);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return repository.findById(id);
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
