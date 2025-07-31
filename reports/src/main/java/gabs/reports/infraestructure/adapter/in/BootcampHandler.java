package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.port.BootcampUseCases;
import gabs.reports.dto.BootcampRequest;
import gabs.reports.dto.BootcampResponse;
import gabs.reports.domain.model.Bootcamp;
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
import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.dto.ErrorResponse;

@Component
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Bootcamp Management",
    description = "API para la gestión de bootcamps y reportes de inscripciones"
)
public class BootcampHandler {

    private final BootcampUseCases bootcampService;
    private final GlobalExceptionHandler exceptionHandler;

    @Operation(
        summary = "Registrar nuevo bootcamp",
        description = """
            Crea un nuevo bootcamp con toda la información necesaria incluyendo:
            - Datos básicos (nombre, descripción, fechas)
            - Tecnologías que se enseñarán
            - Capacidades que se desarrollarán
            
            **Validaciones:**
            - Nombre: 3-100 caracteres
            - Descripción: 10-500 caracteres
            - Fecha de lanzamiento: debe ser futura
            - Duración: 1-52 semanas
            - Al menos una tecnología y una capacidad
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del bootcamp a crear",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BootcampRequest.class)
               
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bootcamp creado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BootcampResponse.class)
              
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los datos del bootcamp",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Error de validación",
                        value = """
                            {
                              "status": 400,
                              "error": "Bad Request",
                              "message": "El nombre del bootcamp es obligatorio",
                              "path": "/bootcamps"
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

    @Operation(
        summary = "Obtener bootcamp con más inscritos",
        description = """
            Retorna el bootcamp que tiene la mayor cantidad de participantes inscritos.
            Incluye información detallada del bootcamp y estadísticas de participación.
            
            **Información retornada:**
            - Datos completos del bootcamp
            - Número total de inscritos
            - Tecnologías y capacidades
            - Estado actual del bootcamp
            """,
        parameters = {
            @Parameter(
                name = "No parameters required",
                description = "Este endpoint no requiere parámetros"
            )
        }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bootcamp con más inscritos encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BootcampResponse.class)
                
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontraron bootcamps con inscritos",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "No hay bootcamps con inscritos",
                        value = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "message": "No se encontraron bootcamps con inscritos",
                              "path": "/bootcamps/mas-inscritos"
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
