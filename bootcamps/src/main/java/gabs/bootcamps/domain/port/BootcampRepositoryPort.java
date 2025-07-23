package gabs.bootcamps.domain.port;

import gabs.bootcamps.domain.model.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampRepositoryPort {


    Flux<Bootcamp> findPagedByNombreAsc(int size, int offset);

    Flux<Bootcamp> findPagedByNombreDesc(int size, int offset);

    Mono<Boolean> existsById(Long id);

    Mono<Bootcamp> findById(Long id);
    //Mono<Bootcamp> findByNombre(String nombre);
    //Mono<Boolean> existsByNombre(String nombre);
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Void> deleteById(Long id);
}
