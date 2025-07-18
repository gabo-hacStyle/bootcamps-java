package gabs.tecnologias.application.port;


import gabs.tecnologias.domain.model.Capacidad;
import gabs.tecnologias.dto.CapacidadRequest;
import gabs.tecnologias.dto.CapacidadResponse;
import gabs.tecnologias.dto.PageAndQuery;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadUseCases {

     Flux<CapacidadResponse> findAll(PageAndQuery consult);
     Mono<CapacidadResponse> findById(Long id);
     Mono<Capacidad> register(CapacidadRequest capacidadRequest);
     Mono<Capacidad> updateParcial(Long id, CapacidadRequest capacidad);
     Mono<Capacidad> findByNombre(String nombre);
     Mono<Void> delete(Long id);
}
