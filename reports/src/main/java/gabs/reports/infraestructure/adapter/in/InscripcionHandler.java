package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.port.InscripcionUseCases;
import gabs.reports.domain.model.Inscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InscripcionHandler {
    private final InscripcionUseCases inscripcionService;

    /**
     * Handler para registrar una inscripción (POST /inscripciones/report)
     */
    public Mono<ServerResponse> registrarInscripcion(ServerRequest request) {
        return request.bodyToMono(Inscripcion.class)
                .flatMap(inscripcionService::save)
                .flatMap(inscripcion -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(inscripcion));
    }
} 