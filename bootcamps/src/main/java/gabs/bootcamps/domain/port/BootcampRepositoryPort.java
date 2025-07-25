package gabs.bootcamps.domain.port;

import gabs.bootcamps.domain.model.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampRepositoryPort {


    Flux<Bootcamp> findPagedByNombreAsc(int size, int offset);

    Flux<Bootcamp> findPagedByNombreDesc(int size, int offset);

    Mono<Boolean> existsById(Long id);

    Mono<Bootcamp> findById(Long id);
    Flux<Bootcamp> findByIds(List<Long> ids);
    //Mono<Bootcamp> findByNombre(String nombre);
    //Mono<Boolean> existsByNombre(String nombre);
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Void> deleteById(Long id);
}
