package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.application.port.TecnologiaUseCases;
import gabs.tecnologias.domain.model.Tecnologia;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TecnologiaHandler {

    private final TecnologiaUseCases service;

    public Mono<ServerResponse> getAll (ServerRequest request) {
        Flux<Tecnologia> all = service.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(all, Tecnologia.class);

    }

    public Mono<ServerResponse> getById (ServerRequest request){
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Tecnologia> tech = service.findById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tech, Boolean.class);
    }
    public Mono<ServerResponse> existsById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Boolean> techExists = service.existsById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(techExists, Boolean.class);

    }

    public Mono<ServerResponse> findByNombre(ServerRequest request) {
        String nombre =request.pathVariable("nombre");
        Mono<Tecnologia> tecnologia = service.findByNombre(nombre);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tecnologia, Tecnologia.class);

    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Tecnologia> newTecnologia = request.bodyToMono(Tecnologia.class);
        return newTecnologia.flatMap(t -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.create(t), Tecnologia.class));
    }
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Tecnologia> tecnologia = request.bodyToMono(Tecnologia.class);
        return tecnologia.flatMap(cambios -> service.updateParcial(id, cambios))
                .flatMap(actualizado -> ServerResponse.ok().bodyValue(actualizado))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.delete(id), Tecnologia.class);

    }



}
