package gabs.capacidades.domain.port;

import org.springframework.data.domain.Pageable;

import gabs.capacidades.domain.model.Capacidad;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadRepositoryPort {

    Flux<Capacidad> findPagedByNombreAsc(int size, int offset);

    Flux<Capacidad> findPagedByNombreDesc(int size, int offset);


    Mono<Capacidad> findById(Long id);
    Mono<Capacidad> findByNombre(String nombre);
    Mono<Boolean> existsById(Long id);
    Mono<Capacidad> save(Capacidad capacidad);
    Mono<Void> deleteById(Long id);
}
