package gabs.personas.infraestructure.adapter.in;

import gabs.personas.application.port.BootcampPersonaUseCases;
import gabs.personas.application.port.PersonaUseCases;
import gabs.personas.domain.exception.*;
import gabs.personas.domain.model.BootcampPersona;
import gabs.personas.domain.model.Persona;
import gabs.personas.dto.EnrollRequest;
import gabs.personas.dto.PersonaRegisteredResponse;
import gabs.personas.infraestructure.config.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class PersonaHandler {

    private final PersonaUseCases service;
    private final BootcampPersonaUseCases bootcampPersonaService;
    private final GlobalExceptionHandler exceptionHandler;

    public Mono<ServerResponse> getAll (ServerRequest request) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Persona.class)
                .onErrorResume(Throwable.class, ex -> 
                    exceptionHandler.handleGenericException(ex, request));

    }
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findById(id), Persona.class)
                .onErrorResume(PersonaNotFoundException.class, ex -> 
                    exceptionHandler.handlePersonaNotFoundException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                    exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                    exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> existsById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Boolean> exists = service.existsById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exists, Boolean.class);

    }

    public Mono<ServerResponse> registerPersonInBootcamp(ServerRequest request) {
        Mono<EnrollRequest> enroll = request.bodyToMono(EnrollRequest.class);

        return enroll.flatMap(e -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bootcampPersonaService.registerInBootcamp(e), PersonaRegisteredResponse.class));
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Persona> newPersona = request.bodyToMono(Persona.class);
        return newPersona.flatMap(t -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.register(t), Persona.class))
                .onErrorResume(PersonaAlreadyExistsException.class, ex -> 
                    exceptionHandler.handlePersonaAlreadyExistsException(ex, request))
                .onErrorResume(InvalidPersonaDataException.class, ex -> 
                    exceptionHandler.handleInvalidPersonaDataException(ex, request))
                .onErrorResume(ExternalServiceException.class, ex -> 
                    exceptionHandler.handleExternalServiceException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                    exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                    exceptionHandler.handleGenericException(ex, request));
    }
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Persona> capacidad = request.bodyToMono(Persona.class);
        return capacidad.flatMap(cambios -> service.updateParcial(id, cambios))
                .flatMap(actualizado -> ServerResponse.ok().bodyValue(actualizado))
                .onErrorResume(PersonaNotFoundException.class, ex -> 
                    exceptionHandler.handlePersonaNotFoundException(ex, request))
                .onErrorResume(PersonaAlreadyExistsException.class, ex -> 
                    exceptionHandler.handlePersonaAlreadyExistsException(ex, request))
                .onErrorResume(InvalidPersonaDataException.class, ex -> 
                    exceptionHandler.handleInvalidPersonaDataException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                    exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                    exceptionHandler.handleGenericException(ex, request));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.delete(id), Void.class)
                .onErrorResume(PersonaNotFoundException.class, ex -> 
                    exceptionHandler.handlePersonaNotFoundException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                    exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                    exceptionHandler.handleGenericException(ex, request));
    }



}
