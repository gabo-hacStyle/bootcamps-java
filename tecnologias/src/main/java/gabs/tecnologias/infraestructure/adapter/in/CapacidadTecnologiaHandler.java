package gabs.tecnologias.infraestructure.adapter.in;

import gabs.tecnologias.application.port.CapacidadTecnologiaUseCases;
import gabs.tecnologias.dto.CapacidadTecnologiaResponse;
import gabs.tecnologias.dto.RegisterCapacidadTecnologiaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CapacidadTecnologiaHandler {

    private final CapacidadTecnologiaUseCases capService;
    private final Validator validator;

    public Mono<ServerResponse> getTechsByCapacidadId(ServerRequest request){
        Long id = Long.valueOf(request.pathVariable("id"));
        Flux<CapacidadTecnologiaResponse> techList = capService.getTechnologiesListByCapacidad(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(techList, CapacidadTecnologiaResponse.class);
    }

    public Mono<ServerResponse> saveCapacidadTecnologia(ServerRequest request) {
       
        return request.bodyToMono(RegisterCapacidadTecnologiaRequest.class)
                .flatMap(this::validateRegisterRequest)
                .flatMap(registerRequest -> capService.register(registerRequest.getCapacidadId(), registerRequest.getTecnologiaIds()).then(Mono.empty()))
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> deleteTecnologiasOfCapacidadesIds(ServerRequest request){
        List<Long> ids = request.queryParams().getOrDefault("ids", List.of())
                .stream()
                .flatMap(idsStr -> Arrays.stream(idsStr.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();

        // 2. Llama al servicio reactivo
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
