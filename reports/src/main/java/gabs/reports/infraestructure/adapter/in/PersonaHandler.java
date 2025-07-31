package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.PersonaService;
import gabs.reports.domain.model.Persona;
import gabs.reports.dto.PersonaDTO;
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
    name = "Person Management",
    description = "API para la gestión de personas (participantes de bootcamps)"
)
public class PersonaHandler {

    private final PersonaService personaService;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(
        summary = "Registrar nueva persona",
        description = """
            Crea un nuevo registro de persona (participante) en el sistema.
            
            **Validaciones:**
            - Nombre: 2-100 caracteres, solo letras y espacios
            - Correo: formato válido de email
            - Edad: 16-100 años
            
            **Información requerida:**
            - Nombre completo
            - Correo electrónico
            - Edad
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la persona a registrar",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PersonaDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Persona válida",
                        summary = "Ejemplo de persona con datos válidos",
                        value = """
                            {
                              "nombre": "María González Rodríguez",
                              "correo": "maria.gonzalez@email.com",
                              "edad": 25
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Desarrollador senior",
                        summary = "Ejemplo de desarrollador con experiencia",
                        value = """
                            {
                              "nombre": "Carlos Alberto Silva",
                              "correo": "carlos.silva@techcompany.com",
                              "edad": 32
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
            description = "Persona registrada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PersonaDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Respuesta exitosa",
                        value = """
                            {
                              "personaId": 1,
                              "nombre": "María González Rodríguez",
                              "correo": "maria.gonzalez@email.com",
                              "edad": 25
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los datos de la persona",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Error de validación - nombre inválido",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "El nombre solo puede contener letras y espacios",
                              "path": "/personas"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Error de validación - correo inválido",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "El formato del correo electrónico no es válido",
                              "path": "/personas"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Error de validación - edad inválida",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "La edad mínima es 16 años",
                              "path": "/personas"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflicto - correo ya registrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Correo duplicado",
                        value = """
                            {
                              "status": 409,
                              "error": "Conflict",
                              "message": "Ya existe una persona registrada con este correo electrónico",
                              "path": "/personas"
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
    public Mono<ServerResponse> crearPersona(ServerRequest request) {
        return request.bodyToMono(Persona.class)
                .flatMap(personaService::save)
                .flatMap(persona -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(persona))
                .onErrorResume(ValidationException.class, error ->
                        exceptionHandler.handleValidationException(error, request.path()))
                .onErrorResume(Exception.class, error ->
                        exceptionHandler.handleGenericException(error, request.path()));
    }
} 