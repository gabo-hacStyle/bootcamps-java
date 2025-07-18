package gabs.tecnologias.application.port;


import gabs.tecnologias.domain.model.Capacidad;
import gabs.tecnologias.dto.CapacidadRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadUseCases {

     Flux<Capacidad> findAll();
     Mono<Capacidad> findById(Long id);
     Mono<Capacidad> register(CapacidadRequest capacidadRequest);
     Mono<Capacidad> updateParcial(Long id, CapacidadRequest capacidad);
     Mono<Capacidad> findByNombre(String nombre);
     Mono<Void> delete(Long id);
}
