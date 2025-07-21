package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.application.port.CapacidadUseCases;
import gabs.tecnologias.domain.model.Capacidad;
import gabs.tecnologias.dto.CapacidadRequest;
import gabs.tecnologias.dto.CapacidadResponse;
import gabs.tecnologias.dto.PageAndQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class CapacidadHandler {


    private final CapacidadUseCases service;

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
        Mono<CapacidadResponse> capacidad = service.findById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(capacidad, CapacidadResponse.class);

    }

    public Mono<ServerResponse> findByNombre(ServerRequest request) {
        String nombre =request.pathVariable("nombre");
        Mono<Capacidad> tecnologia = service.findByNombre(nombre);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tecnologia, Capacidad.class);

    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<CapacidadRequest> newCapacidad = request.bodyToMono(CapacidadRequest.class);
        return newCapacidad.flatMap(t -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.register(t), Capacidad.class));
    }
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<CapacidadRequest> capacidad = request.bodyToMono(CapacidadRequest.class);
        return capacidad.flatMap(cambios -> service.updateParcial(id, cambios))
                .flatMap(actualizado -> ServerResponse.ok().bodyValue(actualizado))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.delete(id), Capacidad.class);

    }



}
