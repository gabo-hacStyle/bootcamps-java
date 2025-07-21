package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.CapacidadBootcamp;
import gabs.tecnologias.domain.port.CapacidadBootcampRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
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
}
