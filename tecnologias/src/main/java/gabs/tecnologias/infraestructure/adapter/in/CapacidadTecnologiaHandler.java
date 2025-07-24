package gabs.tecnologias.infraestructure.adapter.in;


import dto.CapacidadTecnologiaResponse;
import gabs.tecnologias.application.port.CapacidadTecnologiaUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CapacidadTecnologiaHandler {

    private final CapacidadTecnologiaUseCases capService;

    public Mono<ServerResponse> getTechsByCapacidadId(ServerRequest request){
        Long id = Long.valueOf(request.pathVariable("id"));
        Flux<CapacidadTecnologiaResponse> techList = capService.getTechnologiesListByCapacidad(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(techList, CapacidadTecnologiaResponse.class);
    }

    public Mono<ServerResponse> saveCapacidadTecnologia(ServerRequest request) {
        Long capacidadId = Long.valueOf(request.pathVariable("id"));
        Mono<List<Long>> tecnologiaIdsMono = request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {});

        return tecnologiaIdsMono
                .flatMap(tecnologiaIds -> capService.register(capacidadId, tecnologiaIds).then())
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> deleteTecnologiasOfCapacidadesIds(ServerRequest request){
        List<Long> ids = request.queryParams().getOrDefault("ids", List.of())
                .stream()
                .flatMap(idsStr -> Arrays.stream(idsStr.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();

        // 2. Llama al servicio reactivo
        return capService.deleteCapacidadesByCapacidadesIds(ids)
                .then(ServerResponse.ok().build());
    }
}
