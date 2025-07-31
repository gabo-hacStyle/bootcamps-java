package gabs.personas.infraestructure.adapter.in;

import gabs.personas.application.port.BootcampPersonaUseCases;
import gabs.personas.application.port.PersonaUseCases;
import gabs.personas.domain.exception.*;
import gabs.personas.domain.model.BootcampPersona;
import gabs.personas.domain.model.Persona;
import gabs.personas.dto.EnrollRequest;
import gabs.personas.dto.ErrorResponse;
import gabs.personas.dto.PersonaRegisteredResponse;
import gabs.personas.infraestructure.config.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Personas", description = "API para gestión de personas en el sistema de bootcamps")
public class PersonaHandler {

    private final PersonaUseCases service;
    private final BootcampPersonaUseCases bootcampPersonaService;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(summary = "Obtener todas las personas", 
               description = "Retorna una lista de todas las personas registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de personas obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Persona.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> getAll (ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Persona.class)
                .onErrorResume(Throwable.class, ex -> 
                    exceptionHandler.handleGenericException(ex, request));
    }

    @Operation(summary = "Obtener persona por ID", 
               description = "Retorna una persona específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Persona.class))),
        @ApiResponse(responseCode = "404", description = "Persona no encontrada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "Verificar existencia de persona", 
               description = "Verifica si una persona existe por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    })
    public Mono<ServerResponse> existsById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<Boolean> exists = service.existsById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exists, Boolean.class);
    }

    @Operation(summary = "Inscribir persona en bootcamp", 
               description = "Inscribe una persona en un bootcamp específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona inscrita exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PersonaRegisteredResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de inscripción inválidos",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ServerResponse> registerPersonInBootcamp(ServerRequest request) {
        Mono<EnrollRequest> enroll = request.bodyToMono(EnrollRequest.class);

        return enroll.flatMap(e -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bootcampPersonaService.registerInBootcamp(e), PersonaRegisteredResponse.class));
    }

    @Operation(summary = "Crear nueva persona", 
               description = "Crea una nueva persona en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona creada exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Persona.class))),
        @ApiResponse(responseCode = "409", description = "Persona ya existe",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de persona inválidos",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "Actualizar persona", 
               description = "Actualiza los datos de una persona existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Persona.class))),
        @ApiResponse(responseCode = "404", description = "Persona no encontrada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "Eliminar persona", 
               description = "Elimina una persona del sistema por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Persona no encontrada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
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
