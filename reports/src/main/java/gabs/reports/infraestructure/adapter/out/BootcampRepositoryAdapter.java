package gabs.reports.infraestructure.adapter.out;

import gabs.reports.domain.model.Bootcamp;
import gabs.reports.domain.port.BootcampRepositoryPort;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BootcampRepositoryAdapter implements BootcampRepositoryPort {
    private final SpringDataBootcampRepository repository;


    @Override
    public Mono<Bootcamp> findByBootcampId(Long id) {
        return repository.findByBootcampId(id);
    }



    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return repository.save(bootcamp);
    }




    @Override
    public Flux<Bootcamp> findAll() {
        return repository.findAll();
    }
} 