package gabs.capacidades.infraestructure.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import gabs.capacidades.domain.model.CapacidadBootcamp;
import gabs.capacidades.domain.port.CapacidadBootcampRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CapacidadBootcampRepositoryImpl implements CapacidadBootcampRepositoryPort {

    private final SpringDataCapacidadBootcampRepository repository;

    @Override
    public Flux<CapacidadBootcamp> findByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId);
    }

    @Override
    public Mono<CapacidadBootcamp> save(CapacidadBootcamp capacidadBootcamp) {
        return repository.save(capacidadBootcamp);
    }

    @Override
    public Flux<Long> findExclusiveCapacidadesOfBootcamp(Long bootcampId) {
        return repository.findExclusiveCapacidadesOfBootcamp(bootcampId);
    }

    @Override
    public Mono<Void> deleteByBootcampId(Long bootcampId) {
        return repository.deleteByBootcampId(bootcampId);
    }
}
