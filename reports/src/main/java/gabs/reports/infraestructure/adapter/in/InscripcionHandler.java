package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.port.InscripcionUseCases;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Inscripcion;
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
public class InscripcionHandler {
    private final InscripcionUseCases inscripcionService;
    private final GlobalExceptionHandler exceptionHandler;

    /**
     * Handler para registrar una inscripción (POST /inscripciones/report)
     */
    public Mono<ServerResponse> registrarInscripcion(ServerRequest request) {
        return request.bodyToMono(Inscripcion.class)
                .flatMap(inscripcionService::save)
                .flatMap(inscripcion -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(inscripcion))
                .onErrorResume(ValidationException.class, error -> 
                    exceptionHandler.handleValidationException(error, request.path()))
                .onErrorResume(Exception.class, error -> 
                    exceptionHandler.handleGenericException(error, request.path()));
    }
} 