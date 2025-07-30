package gabs.tecnologias.application.port;

import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.dto.CapacidadTecnologiaResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadTecnologiaUseCases {
    Flux<CapacidadTecnologiaResponse> getTechnologiesListByCapacidad(Long id);
    Flux<CapacidadTecnologia> register(Long capacidadId, List<Long> tecnologiaId);
    Mono<Void> deleteCapacidadesByCapacidadesIds(List<Long> capacidadesId);


}
