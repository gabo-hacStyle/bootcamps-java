package gabs.bootcamps.infraestructure.adapter.in;


import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.exception.BootcampException;
import gabs.bootcamps.domain.exception.BootcampNotFoundException;
import gabs.bootcamps.domain.exception.BootcampValidationException;
import gabs.bootcamps.domain.exception.ExternalServiceException;
import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.infraestructure.config.GlobalExceptionHandler;

import gabs.bootcamps.dto.BootcampRequest;
import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.BootcampSimpleResponse;
import gabs.bootcamps.dto.PageAndQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bootcamp", description = "API para gestión de bootcamps")
public class BootcampHandler {

    private final BootcampUseCases service;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(
        summary = "Obtener todos los bootcamps",
        description = "Retorna una lista paginada de todos los bootcamps con opciones de ordenamiento"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de bootcamps obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BootcampResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros de paginación inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        )
    })
    public Mono<ServerResponse> getAll(ServerRequest request) {
        try {
            int page = Integer.parseInt(request.queryParam("page").orElse("0"));
            int size = Integer.parseInt(request.queryParam("size").orElse("10"));
            String sortBy = request.queryParam("sortBy").orElse("nombre");
            String direction = request.queryParam("direction").orElse("asc");

            PageAndQuery consult = new PageAndQuery(page, size, sortBy, direction);

            log.info("SortBy: {}, Direction: {}", consult.getSortBy(), consult.getDirection());

            Flux<BootcampResponse> all = service.findAll(consult);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(all, BootcampResponse.class)
                    .onErrorResume(BootcampException.class, ex -> exceptionHandler.handleBootcampException(ex, request))
                    .onErrorResume(Throwable.class, ex -> exceptionHandler.handleGenericException(ex, request));
        } catch (NumberFormatException e) {
            return exceptionHandler.handleIllegalArgumentException(
                new IllegalArgumentException("Los parámetros page y size deben ser números válidos"), request);
        }
    }
    
    @Operation(
        summary = "Obtener bootcamp por ID",
        description = "Retorna un bootcamp específico por su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bootcamp encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BootcampResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Bootcamp no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ID inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        )
    })
    public Mono<ServerResponse> getById(ServerRequest request) {
        try {
            Long id = Long.valueOf(request.pathVariable("id"));
            Mono<BootcampResponse> bootcamp = service.findById(id);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bootcamp, BootcampResponse.class)
                    .onErrorResume(BootcampNotFoundException.class, ex -> exceptionHandler.handleBootcampNotFoundException(ex, request))
                    .onErrorResume(ExternalServiceException.class, ex -> exceptionHandler.handleExternalServiceException(ex, request))
                    .onErrorResume(BootcampException.class, ex -> exceptionHandler.handleBootcampException(ex, request))
                    .onErrorResume(Throwable.class, ex -> exceptionHandler.handleGenericException(ex, request));
        } catch (NumberFormatException e) {
            return exceptionHandler.handleIllegalArgumentException(
                new IllegalArgumentException("El ID debe ser un número válido"), request);
        }
    }

    @Operation(
        summary = "Obtener bootcamps simplificados por IDs",
        description = "Retorna una lista de bootcamps con información simplificada basada en una lista de IDs"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de bootcamps simplificados obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BootcampSimpleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "IDs inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        )
    })
    public Mono<ServerResponse> getSimpleBootcampResponseByIds(ServerRequest request) {
        try {
            List<Long> ids = request.queryParams().getOrDefault("ids", List.of())
                    .stream()
                    .flatMap(idsStr -> Arrays.stream(idsStr.split(",")))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .toList();

            Flux<BootcampSimpleResponse> bootcamps = service.findByIdSimpleResponse(ids);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bootcamps, BootcampSimpleResponse.class)
                    .onErrorResume(BootcampException.class, ex -> exceptionHandler.handleBootcampException(ex, request))
                    .onErrorResume(Throwable.class, ex -> exceptionHandler.handleGenericException(ex, request));
        } catch (NumberFormatException e) {
            return exceptionHandler.handleIllegalArgumentException(
                new IllegalArgumentException("Los IDs deben ser números válidos"), request);
        }
    }

    //public Mono<ServerResponse> findByNombre(ServerRequest request) {
    //    String nombre =request.pathVariable("nombre");
    //    Mono<Bootcamp> tecnologia = service.findByNombre(nombre);
    //    return ServerResponse.ok()
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .body(tecnologia, Bootcamp.class);
//
    //}

    @Operation(
        summary = "Crear nuevo bootcamp",
        description = "Crea un nuevo bootcamp con la información proporcionada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bootcamp creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Bootcamp.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de validación inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        )
    })
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(BootcampRequest.class)
                .flatMap(bootcampRequest -> 
                    ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.register(bootcampRequest), Bootcamp.class)
                        .onErrorResume(BootcampValidationException.class, ex -> exceptionHandler.handleBootcampValidationException(ex, request))
                        .onErrorResume(ExternalServiceException.class, ex -> exceptionHandler.handleExternalServiceException(ex, request))
                        .onErrorResume(BootcampException.class, ex -> exceptionHandler.handleBootcampException(ex, request))
                        .onErrorResume(Throwable.class, ex -> exceptionHandler.handleGenericException(ex, request))
                )
                .onErrorResume(Throwable.class, ex -> exceptionHandler.handleGenericException(ex, request));
    }
    //public Mono<ServerResponse> update(ServerRequest request) {
    //    Long id = Long.valueOf(request.pathVariable("id"));
    //    Mono<CapacidadRequest> capacidad = request.bodyToMono(CapacidadRequest.class);
    //    return capacidad.flatMap(cambios -> service.updateParcial(id, cambios))
    //            .flatMap(actualizado -> ServerResponse.ok().bodyValue(actualizado))
    //            .switchIfEmpty(ServerResponse.notFound().build());
//
    //}
//
    @Operation(
        summary = "Eliminar bootcamp por ID",
        description = "Elimina un bootcamp específico por su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bootcamp eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Bootcamp no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ID inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = gabs.bootcamps.dto.ErrorResponse.class)
            )
        )
    })
    public Mono<ServerResponse> delete(ServerRequest request) {
        try {
            Long id = Long.valueOf(request.pathVariable("id"));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.delete(id), Void.class)
                    .onErrorResume(BootcampNotFoundException.class, ex -> exceptionHandler.handleBootcampNotFoundException(ex, request))
                    .onErrorResume(ExternalServiceException.class, ex -> exceptionHandler.handleExternalServiceException(ex, request))
                    .onErrorResume(BootcampException.class, ex -> exceptionHandler.handleBootcampException(ex, request))
                    .onErrorResume(Throwable.class, ex -> exceptionHandler.handleGenericException(ex, request));
        } catch (NumberFormatException e) {
            return exceptionHandler.handleIllegalArgumentException(
                new IllegalArgumentException("El ID debe ser un número válido"), request);
        }
    }

}
