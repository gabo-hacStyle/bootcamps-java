package gabs.tecnologias.application.port;


import gabs.tecnologias.domain.model.Tecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TecnologiaUseCases {

     Flux<Tecnologia> findAll();
     Mono<Tecnologia> findById(Long id);
     Mono<Boolean> existsById(Long id);
     Mono<Tecnologia> create(Tecnologia tecnologia);
     Mono<Tecnologia> updateParcial(Long id, Tecnologia tecnologia);
     Mono<Tecnologia> findByNombre(String nombre);
     Mono<Void> delete(Long id);
}
