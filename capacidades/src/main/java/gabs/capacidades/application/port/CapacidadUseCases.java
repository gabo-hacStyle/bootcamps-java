package gabs.capacidades.application.port;


import org.springframework.data.domain.Pageable;

import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.dto.CapacidadRequest;
import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.PageAndQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadUseCases {

     Flux<CapacidadResponse> findAll(PageAndQuery consult);
     Mono<CapacidadResponse> findById(Long id);
     Mono<Capacidad> register(CapacidadRequest capacidadRequest);
     Mono<Capacidad> updateParcial(Long id, CapacidadRequest capacidad);
     Mono<Boolean> existsById(Long id);
     Mono<Void> delete(Long id);

}
