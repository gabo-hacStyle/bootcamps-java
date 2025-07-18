package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.application.port.CapacidadUseCases;
import gabs.tecnologias.domain.model.Capacidad;
import gabs.tecnologias.dto.CapacidadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapacidadHandler {

    private final CapacidadUseCases service;

    public Mono<ServerResponse> getAll (ServerRequest request) {
        Flux<Capacidad> all = service.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(all, Capacidad.class);

    }
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Capacidad> capacidad = service.findById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(capacidad, Capacidad.class);

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
