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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.application.port.CapacidadUseCases;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.dto.CapacidadRequest;
import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.PageAndQuery;
import gabs.capacidades.dto.ErrorResponse;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Capacidades", description = "API para la gestión de capacidades")
public class CapacidadHandler {

    private final CapacidadUseCases service;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(
        summary = "Obtener todas las capacidades",
        description = "Retorna una lista paginada de todas las capacidades con sus tecnologías asociadas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de capacidades obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CapacidadResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> getAll(ServerRequest request) {
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

    @Operation(
        summary = "Obtener capacidad por ID",
        description = "Retorna una capacidad específica con sus tecnologías asociadas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capacidad encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = CapacidadResponse.class))),
        @ApiResponse(responseCode = "404", description = "Capacidad no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.findById(id)
                .flatMap(capacidad -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(capacidad))
                .onErrorResume(CapacidadNotFoundException.class, ex -> 
                        exceptionHandler.handleCapacidadNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(
        summary = "Verificar existencia de capacidad",
        description = "Verifica si una capacidad existe por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada",
            content = @Content(schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> existsById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.existsById(id)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(exists))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(
        summary = "Crear nueva capacidad",
        description = "Crea una nueva capacidad con sus tecnologías asociadas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Capacidad creada exitosamente",
            content = @Content(schema = @Schema(implementation = Capacidad.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CapacidadRequest.class)
                .flatMap(newCapacidad -> service.register(newCapacidad)
                        .flatMap(saved -> ServerResponse.status(201)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(saved)))
                .onErrorResume(ValidationException.class, ex -> 
                        exceptionHandler.handleValidationException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                        exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(
        summary = "Actualizar capacidad",
        description = "Actualiza una capacidad existente con los datos proporcionados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capacidad actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = Capacidad.class))),
        @ApiResponse(responseCode = "404", description = "Capacidad no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CapacidadRequest.class)
                .flatMap(cambios -> service.updateParcial(id, cambios)
                        .flatMap(actualizado -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(actualizado)))
                .onErrorResume(CapacidadNotFoundException.class, ex -> 
                        exceptionHandler.handleCapacidadNotFound(ex, request))
                .onErrorResume(ValidationException.class, ex -> 
                        exceptionHandler.handleValidationException(ex, request))
                .onErrorResume(IllegalArgumentException.class, ex -> 
                        exceptionHandler.handleIllegalArgumentException(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(
        summary = "Eliminar capacidad",
        description = "Elimina una capacidad por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Capacidad eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Capacidad no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.delete(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(CapacidadNotFoundException.class, ex -> 
                        exceptionHandler.handleCapacidadNotFound(ex, request))
                .onErrorResume(Throwable.class, ex -> 
                        exceptionHandler.handleGenericException(ex, request));
    }
}
