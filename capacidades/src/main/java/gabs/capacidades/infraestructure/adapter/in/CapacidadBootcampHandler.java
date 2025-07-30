package gabs.capacidades.infraestructure.adapter.in;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.application.port.CapacidadBootcampUseCases;
import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.CapacidadBootcamp;
import gabs.capacidades.dto.CapacidadBootcampResponse;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class CapacidadBootcampHandler {

    private final CapacidadBootcampUseCases service;
    private final GlobalExceptionHandler exceptionHandler;

    public Mono<ServerResponse> getCapacidadesByBootcamp(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return service.getAllByBootcamp(id)
                .collectList()
                .flatMap(capacidades -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(capacidades))
                .onErrorResume(BootcampNotFoundException.class, ex -> 
                        exceptionHandler.handleBootcampNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> saveCapacidadBootcamp(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {})
                .flatMap(capacidadesId -> service.saveCapacidadBootcamp(id, capacidadesId)
                        .then(Mono.just(ServerResponse.status(201).build())))
                .flatMap(response -> response)
                .onErrorResume(BootcampNotFoundException.class, ex -> 
                        exceptionHandler.handleBootcampNotFound(ex, request))
                .onErrorResume(ValidationException.class, ex -> 
                        exceptionHandler.handleValidationException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                        exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> deleteCapacidadesByBootcampDeleted(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        return service.deleteCapacidadesByBootcampId(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(BootcampNotFoundException.class, ex -> 
                        exceptionHandler.handleBootcampNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }
}
