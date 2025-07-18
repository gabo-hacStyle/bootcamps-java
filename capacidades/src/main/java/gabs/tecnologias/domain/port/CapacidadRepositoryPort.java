package gabs.tecnologias.domain.port;

import gabs.tecnologias.domain.model.Capacidad;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadRepositoryPort {

    Flux<Capacidad> findPagedByNombreAsc(int size, int offset);

    Flux<Capacidad> findPagedByNombreDesc(int size, int offset);


    Mono<Capacidad> findById(Long id);
    Mono<Capacidad> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);
    Mono<Capacidad> save(Capacidad capacidad);
    Mono<Void> deleteById(Long id);
}
