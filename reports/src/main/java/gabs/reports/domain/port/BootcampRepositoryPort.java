package gabs.reports.domain.port;

import gabs.reports.domain.model.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampRepositoryPort {

   Flux<Bootcamp> findAll();




    Mono<Bootcamp> findByBootcampId(Long id);

    //Mono<Bootcamp> findByNombre(String nombre);
    //Mono<Boolean> existsByNombre(String nombre);
    Mono<Bootcamp> save(Bootcamp bootcamp);

}
