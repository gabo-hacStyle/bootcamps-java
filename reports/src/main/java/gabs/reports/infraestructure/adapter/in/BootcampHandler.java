package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.BootcampService;
import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.dto.BootcampRequest;
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
public class BootcampHandler {
    private final BootcampService bootcampService;
    private final GlobalExceptionHandler exceptionHandler;

    /**
     * Handler para registrar un bootcamp (POST /bootcamps/report)
     */
    public Mono<ServerResponse> registrarBootcamp(ServerRequest request) {
        return request.bodyToMono(BootcampRequest.class)
                .flatMap(bootcampService::register)
                .flatMap(bootcamp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(bootcamp))
                .onErrorResume(ValidationException.class, error -> 
                    exceptionHandler.handleValidationException(error, request.path()))
                .onErrorResume(BootcampNotFoundException.class, error -> 
                    exceptionHandler.handleBootcampNotFoundException(error, request.path()))
                .onErrorResume(Exception.class, error -> 
                    exceptionHandler.handleGenericException(error, request.path()));
    }

    /**
     * Handler para obtener el bootcamp con más inscritos (GET /bootcamps/report/max-inscritos)
     */
    public Mono<ServerResponse> bootcampConMasInscritos(ServerRequest request) {
        return bootcampService.findBootcampConMasInscritos()
                .flatMap(bootcamp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(bootcamp))
                .onErrorResume(BootcampNotFoundException.class, error -> 
                    exceptionHandler.handleBootcampNotFoundException(error, request.path()))
                .onErrorResume(Exception.class, error -> 
                    exceptionHandler.handleGenericException(error, request.path()));
    }
}
