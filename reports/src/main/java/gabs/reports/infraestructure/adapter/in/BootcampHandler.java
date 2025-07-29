package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.BootcampService;
import gabs.reports.dto.BootcampRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BootcampHandler {
    private final BootcampService bootcampService;

    /**
     * Handler para registrar un bootcamp (POST /bootcamps/report)
     */
    public Mono<ServerResponse> registrarBootcamp(ServerRequest request) {
        return request.bodyToMono(BootcampRequest.class)
                .flatMap(bootcampService::register)
                .flatMap(bootcamp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(bootcamp));
    }

    /**
     * Handler para obtener el bootcamp con más inscritos (GET /bootcamps/report/max-inscritos)
     */
    public Mono<ServerResponse> bootcampConMasInscritos(ServerRequest request) {
        // Aquí deberías implementar la lógica para buscar el bootcamp con más inscritos
        // y retornar toda la información requerida
        return bootcampService.findBootcampConMasInscritos()
                .flatMap(bootcamp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(bootcamp))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
