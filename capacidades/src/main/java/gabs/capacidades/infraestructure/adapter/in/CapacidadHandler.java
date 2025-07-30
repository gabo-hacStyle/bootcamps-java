package gabs.capacidades.infraestructure.adapter.in;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.application.port.CapacidadUseCases;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.dto.CapacidadRequest;
import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.PageAndQuery;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class CapacidadHandler {


    private final CapacidadUseCases service;
    private final GlobalExceptionHandler exceptionHandler;

    public Mono<ServerResponse> getAll (ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("nombre");
        String direction = request.queryParam("direction").orElse("asc");

        PageAndQuery consult = new PageAndQuery(page, size, sortBy, direction);

        System.out.println("SortBy: " + consult.getSortBy() + ", Direction: " + consult.getDirection());
        log.info("SortBy: {}, Direction: {}", consult.getSortBy(), consult.getDirection());

        Flux<CapacidadResponse> all = service.findAll(consult);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(all, CapacidadResponse.class);

    }
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.findById(id)
                .flatMap(capacidad -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(capacidad))
                .onErrorResume(CapacidadNotFoundException.class, ex -> 
                        exceptionHandler.handleCapacidadNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> existsById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.existsById(id)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(exists))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CapacidadRequest.class)
                .flatMap(newCapacidad -> service.register(newCapacidad)
                        .flatMap(saved -> ServerResponse.status(201)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(saved)))
                .onErrorResume(ValidationException.class, ex -> 
                        exceptionHandler.handleValidationException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                        exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CapacidadRequest.class)
                .flatMap(cambios -> service.updateParcial(id, cambios)
                        .flatMap(actualizado -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(actualizado)))
                .onErrorResume(CapacidadNotFoundException.class, ex -> 
                        exceptionHandler.handleCapacidadNotFound(ex, request))
                .onErrorResume(ValidationException.class, ex -> 
                        exceptionHandler.handleValidationException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                        exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.delete(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(CapacidadNotFoundException.class, ex -> 
                        exceptionHandler.handleCapacidadNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }



}
