package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.application.port.CapacidadBootcampUseCases;
import gabs.tecnologias.domain.model.CapacidadBootcamp;
import gabs.tecnologias.dto.CapacidadBootcampResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacidadBootcampHandler {

    private final CapacidadBootcampUseCases service;

    public Mono<ServerResponse> getCapacidadesByBootcamp(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllByBootcamp(id), CapacidadBootcampResponse.class);
    }

    public Mono<ServerResponse> saveCapacidadBootcamp(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        Mono<List<Long>> capacidadesIdsMono = request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {});

        return capacidadesIdsMono
                .flatMap(capacidadesId -> service.saveCapacidadBootcamp(id, capacidadesId).then())
                .then(ServerResponse.ok().build());

    }

    public Mono<ServerResponse> deleteCapacidadesByBootcampDeleted(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.deleteCapacidadesByBootcampId(id), Void.class);
    }
}
