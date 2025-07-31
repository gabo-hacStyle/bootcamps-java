package gabs.tecnologias.infraestructure.adapter.in;

import gabs.tecnologias.application.port.CapacidadTecnologiaUseCases;
import gabs.tecnologias.dto.CapacidadTecnologiaResponse;
import gabs.tecnologias.dto.RegisterCapacidadTecnologiaRequest;
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

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Tag(name = "Capacidades de Tecnología", description = "API para gestión de capacidades de tecnología")
public class CapacidadTecnologiaHandler {

    private final CapacidadTecnologiaUseCases capService;
    private final Validator validator;

    @Operation(
            summary = "Obtener tecnologías por capacidad",
            description = "Retorna todas las tecnologías asociadas a una capacidad específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tecnologías obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CapacidadTecnologiaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Capacidad no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> getTechsByCapacidadId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Flux<CapacidadTecnologiaResponse> techList = capService.getTechnologiesListByCapacidad(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(techList, CapacidadTecnologiaResponse.class);
    }

        @Operation(
            summary = "Registrar capacidades de tecnología",
            description = "Asocia tecnologías a una capacidad específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capacidades registradas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Capacidad o tecnologías no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> saveCapacidadTecnologia(ServerRequest request) {
        Long capacidadId = Long.valueOf(request.pathVariable("id"));

        

        return request.bodyToMono(RegisterCapacidadTecnologiaRequest.class)
                .flatMap(this::validateRegisterRequest)
                .flatMap(registerRequest -> capService.register(capacidadId, registerRequest.getTecnologiaIds()).then(Mono.empty()))
                .then(ServerResponse.ok().build());
    }

    @Operation(
            summary = "Eliminar capacidades de tecnología",
            description = "Elimina las asociaciones de tecnologías con capacidades por IDs de capacidades"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capacidades eliminadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "IDs de capacidades inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> deleteTecnologiasOfCapacidadesIds(ServerRequest request) {
        List<Long> ids = request.queryParams().getOrDefault("ids", List.of())
                .stream()
                .flatMap(idsStr -> Arrays.stream(idsStr.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();

        return capService.deleteCapacidadesByCapacidadesIds(ids)
                .then(ServerResponse.ok().build());
    }
    
    private Mono<RegisterCapacidadTecnologiaRequest> validateRegisterRequest(RegisterCapacidadTecnologiaRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "registerCapacidadTecnologiaRequest");
        validator.validate(request, errors);
        
        if (errors.hasErrors()) {
            return Mono.error(new RuntimeException("Error de validación: " + errors.getAllErrors()));
        }
        
        return Mono.just(request);
    }
}
