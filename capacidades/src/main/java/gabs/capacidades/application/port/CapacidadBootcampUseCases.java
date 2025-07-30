package gabs.capacidades.application.port;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import gabs.capacidades.domain.model.CapacidadBootcamp;
import gabs.capacidades.dto.CapacidadBootcampResponse;

public interface CapacidadBootcampUseCases {
    Flux<CapacidadBootcampResponse> getAllByBootcamp(Long bootcampId);

    Flux<CapacidadBootcamp> saveCapacidadBootcamp(Long bootcampId, List<Long> capacidadesList);

    Mono<Void> deleteCapacidadesByBootcampId(Long bootcampId);

}
