package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.port.InscripcionUseCases;
import gabs.reports.domain.model.Inscripcion;
import gabs.reports.dto.InscripcionDTO;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(
    name = "Enrollment Management",
    description = "API para la gestión de inscripciones de personas en bootcamps"
)
public class InscripcionHandler {

    private final InscripcionUseCases inscripcionService;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(
        summary = "Registrar nueva inscripción",
        description = """
            Crea una nueva inscripción de una persona en un bootcamp específico.
            
            **Validaciones:**
            - ID de persona: debe existir en el sistema
            - ID de bootcamp: debe existir y estar activo
            - Nombre de persona: 2-100 caracteres
            - Correo de persona: formato válido de email
            - Nombre de bootcamp: 3-100 caracteres
            
            **Información requerida:**
            - ID de la persona
            - ID del bootcamp
            - Nombre de la persona
            - Correo de la persona
            - Nombre del bootcamp
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la inscripción a registrar",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InscripcionDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Inscripción válida",
                        summary = "Ejemplo de inscripción con datos válidos",
                        value = """
                            {
                              "personaId": 1,
                              "bootcampId": 1,
                              "nombrePersona": "Ana María López",
                              "correoPersona": "ana.lopez@email.com",
                              "nombreBootcamp": "Java Full Stack Developer"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Inscripción desarrollador senior",
                        summary = "Ejemplo de inscripción de desarrollador con experiencia",
                        value = """
                            {
                              "personaId": 2,
                              "bootcampId": 1,
                              "nombrePersona": "Roberto Carlos Méndez",
                              "correoPersona": "roberto.mendez@techcompany.com",
                              "nombreBootcamp": "Java Full Stack Developer"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Inscripción registrada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InscripcionDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Respuesta exitosa",
                        value = """
                            {
                              "id": 1,
                              "personaId": 1,
                              "bootcampId": 1,
                              "nombrePersona": "Ana María López",
                              "correoPersona": "ana.lopez@email.com",
                              "nombreBootcamp": "Java Full Stack Developer",
                              "fechaInscripcion": "2024-01-15T10:30:00",
                              "estado": "ACTIVA"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los datos de la inscripción",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Error de validación - persona no existe",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "La persona con ID 999 no existe en el sistema",
                              "path": "/inscripciones"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Error de validación - bootcamp no existe",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "El bootcamp con ID 999 no existe en el sistema",
                              "path": "/inscripciones"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Error de validación - datos inválidos",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "El nombre de la persona es obligatorio",
                              "path": "/inscripciones"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto - inscripción duplicada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Inscripción duplicada",
                        value = """
                            {
                              "status": 409,
                              "error": "Conflict",
                              "message": "La persona ya está inscrita en este bootcamp",
                              "path": "/inscripciones"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
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