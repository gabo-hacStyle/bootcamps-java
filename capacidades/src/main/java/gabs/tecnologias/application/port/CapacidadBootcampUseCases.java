package gabs.tecnologias.application.port;

import gabs.tecnologias.domain.model.CapacidadBootcamp;
import gabs.tecnologias.dto.CapacidadBootcampResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadBootcampUseCases {
    Flux<CapacidadBootcampResponse> getAllByBootcamp(Long bootcampId);

    Flux<CapacidadBootcamp> saveCapacidadBootcamp(Long bootcampId, List<Long> capacidadesList);

}
