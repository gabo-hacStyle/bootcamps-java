package gabs.tecnologias.application.port;

import dto.CapacidadTecnologiaResponse;
import gabs.tecnologias.domain.model.CapacidadTecnologia;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadTecnologiaUseCases {
    Flux<CapacidadTecnologiaResponse> getTechnologiesListByCapacidad(Long id);
    Flux<CapacidadTecnologia> register(Long capacidadId, List<Long> tecnologiaId);


}
