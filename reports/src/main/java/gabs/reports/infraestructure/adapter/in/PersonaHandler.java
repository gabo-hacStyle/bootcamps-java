package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.PersonaService;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Persona;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PersonaHandler {
    private final PersonaService personaService;
    private final GlobalExceptionHandler exceptionHandler;

    @Autowired
    public PersonaHandler(PersonaService personaService, GlobalExceptionHandler exceptionHandler) {
        this.personaService = personaService;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Handler para crear una persona (POST /personas/report)
     */
    public Mono<ServerResponse> crearPersona(ServerRequest request) {
        return request.bodyToMono(Persona.class)
                .flatMap(personaService::save)
                .flatMap(persona -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(persona))
                .onErrorResume(ValidationException.class, error -> 
                    exceptionHandler.handleValidationException(error, request.path()))
                .onErrorResume(Exception.class, error -> 
                    exceptionHandler.handleGenericException(error, request.path()));
    }
} 