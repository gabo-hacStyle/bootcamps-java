package gabs.tecnologias.domain.port;

import gabs.tecnologias.domain.model.Tecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TecnologiaRepositoryPort {

    Flux<Tecnologia> findAll();
    Mono<Tecnologia> findById(Long id);
    Mono<Tecnologia> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);
    Mono<Boolean> existsById(Long id);
    Mono<Tecnologia> save(Tecnologia tecnologia);
    Mono<Void> deleteById(Long id);
    Mono<Void> deleteAllById(Iterable<Long> ids);
}
