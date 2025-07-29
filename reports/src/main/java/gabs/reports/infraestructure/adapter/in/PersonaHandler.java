package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.PersonaService;
import gabs.reports.domain.model.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PersonaHandler {
    private final PersonaService personaService;

    @Autowired
    public PersonaHandler(PersonaService personaService) {
        this.personaService = personaService;
    }

    /**
     * Handler para crear una persona (POST /personas/report)
     */
    public Mono<ServerResponse> crearPersona(ServerRequest request) {
        return request.bodyToMono(Persona.class)
                .flatMap(personaService::save)
                .flatMap(persona -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(persona));
    }
} 