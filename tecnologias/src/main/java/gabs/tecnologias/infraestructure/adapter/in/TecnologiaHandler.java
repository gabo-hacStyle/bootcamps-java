package gabs.tecnologias.infraestructure.adapter.in;

import gabs.tecnologias.application.port.TecnologiaUseCases;
import gabs.tecnologias.domain.model.Tecnologia;
import gabs.tecnologias.dto.CreateTecnologiaRequest;
import gabs.tecnologias.dto.UpdateTecnologiaRequest;
import gabs.tecnologias.dto.TecnologiaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Tag(name = "Tecnologías", description = "API para gestión de tecnologías")
public class TecnologiaHandler {

    private final TecnologiaUseCases service;
    private final Validator validator;


    @Operation(
            summary = "Obtener todas las tecnologías",
            description = "Retorna una lista de todas las tecnologías disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tecnologías obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TecnologiaResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<TecnologiaResponse> all = service.findAll()
                .map(this::mapToResponse);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(all, TecnologiaResponse.class);
    }

    @Operation(
            summary = "Obtener tecnología por ID",
            description = "Retorna una tecnología específica por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnología encontrada exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TecnologiaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tecnología no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<TecnologiaResponse> tech = service.findById(id)
                .map(this::mapToResponse);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tech, TecnologiaResponse.class);
    }
    @Operation(
            summary = "Verificar existencia de tecnología",
            description = "Verifica si existe una tecnología con el ID especificado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación completada",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> existsById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Boolean> techExists = service.existsById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(techExists, Boolean.class);
    }

    @Operation(
            summary = "Buscar tecnología por nombre",
            description = "Busca una tecnología específica por su nombre exacto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnología encontrada exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TecnologiaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tecnología no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> findByNombre(ServerRequest request) {
        String nombre = request.pathVariable("nombre");
        Mono<TecnologiaResponse> tecnologia = service.findByNombre(nombre)
                .map(this::mapToResponse);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tecnologia, TecnologiaResponse.class);
    }

    @Operation(
            summary = "Crear nueva tecnología",
            description = "Crea una nueva tecnología con los datos proporcionados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnología creada exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TecnologiaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Tecnología con ese nombre ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CreateTecnologiaRequest.class)
                .flatMap(this::validateCreateRequest)
                .flatMap(createRequest -> {
                    Tecnologia tecnologia = new Tecnologia();
                    tecnologia.setNombre(createRequest.getNombre());
                    tecnologia.setDescripcion(createRequest.getDescripcion());
                    return service.create(tecnologia);
                })
                .map(this::mapToResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
    @Operation(
            summary = "Actualizar tecnología",
            description = "Actualiza una tecnología existente con los datos proporcionados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnología actualizada exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TecnologiaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tecnología no encontrada"),
            @ApiResponse(responseCode = "409", description = "Tecnología con ese nombre ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(UpdateTecnologiaRequest.class)
                .flatMap(this::validateUpdateRequest)
                .flatMap(updateRequest -> {
                    Tecnologia cambios = new Tecnologia();
                    cambios.setNombre(updateRequest.getNombre());
                    cambios.setDescripcion(updateRequest.getDescripcion());
                    return service.updateParcial(id, cambios);
                })
                .map(this::mapToResponse)
                .flatMap(actualizado -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(actualizado))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Operation(
            summary = "Eliminar tecnología",
            description = "Elimina una tecnología específica por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tecnología eliminada exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TecnologiaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tecnología no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return service.delete(id)
                .then(ServerResponse.ok().build());
    }

    /**
     * Mapea un modelo de dominio a DTO de respuesta
     */
    private TecnologiaResponse mapToResponse(Tecnologia tecnologia) {
        return new TecnologiaResponse(
                tecnologia.getId(),
                tecnologia.getNombre(),
                tecnologia.getDescripcion()
        );
    }

    /**
     * Valida la petición de creación
     */
    private Mono<CreateTecnologiaRequest> validateCreateRequest(CreateTecnologiaRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "createTecnologiaRequest");
        validator.validate(request, errors);
        
        if (errors.hasErrors()) {
            return Mono.error(new RuntimeException("Error de validación: " + errors.getAllErrors()));
        }
        
        return Mono.just(request);
    }

    /**
     * Valida la petición de actualización
     */
    private Mono<UpdateTecnologiaRequest> validateUpdateRequest(UpdateTecnologiaRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "updateTecnologiaRequest");
        validator.validate(request, errors);
        
        if (errors.hasErrors()) {
            return Mono.error(new RuntimeException("Error de validación: " + errors.getAllErrors()));
        }
        
        return Mono.just(request);
    }




}
