package gabs.capacidades.infraestructure.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.application.port.CapacidadBootcampUseCases;
import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.CapacidadBootcamp;
import gabs.capacidades.dto.CapacidadBootcampResponse;
import gabs.capacidades.dto.ErrorResponse;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Capacidades-Bootcamp", description = "API para la gestión de capacidades asociadas a bootcamps")
public class CapacidadBootcampHandler {

    private final CapacidadBootcampUseCases service;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(
        summary = "Obtener capacidades por bootcamp",
        description = "Retorna todas las capacidades asociadas a un bootcamp específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capacidades obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = CapacidadBootcampResponse.class))),
        @ApiResponse(responseCode = "404", description = "Bootcamp no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> getCapacidadesByBootcamp(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return service.getAllByBootcamp(id)
                .collectList()
                .flatMap(capacidades -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(capacidades))
                .onErrorResume(BootcampNotFoundException.class, ex -> 
                        exceptionHandler.handleBootcampNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(
        summary = "Asociar capacidades a bootcamp",
        description = "Asocia una lista de capacidades a un bootcamp específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Capacidades asociadas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Bootcamp no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> saveCapacidadBootcamp(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {})
                .flatMap(capacidadesId -> service.saveCapacidadBootcamp(id, capacidadesId)
                        .then(Mono.just(ServerResponse.status(201).build())))
                .flatMap(response -> response)
                .onErrorResume(BootcampNotFoundException.class, ex -> 
                        exceptionHandler.handleBootcampNotFound(ex, request))
                .onErrorResume(ValidationException.class, ex -> 
                        exceptionHandler.handleValidationException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                        exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(
        summary = "Eliminar capacidades de bootcamp",
        description = "Elimina todas las capacidades asociadas a un bootcamp específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Capacidades eliminadas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Bootcamp no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> deleteCapacidadesByBootcampDeleted(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        return service.deleteCapacidadesByBootcampId(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(BootcampNotFoundException.class, ex -> 
                        exceptionHandler.handleBootcampNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }
}
