package gabs.personas.infraestructure.adapter.out;

import gabs.personas.dto.PersonaRegisteredResponse;
import gabs.personas.dto.PersonaReportResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ReportClient {

    private final WebClient webClient;

    public ReportClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://reports-micro:8084/reports").build();
    }

    public Mono<Void>  postPersonaReport (PersonaReportResponse persona) {
        return webClient.post()
                .uri("/persons")
                .bodyValue(persona)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void>  postInscriptionReport (PersonaRegisteredResponse enroll) {
        return webClient.post()
                .uri("/enrollments")
                .bodyValue(enroll)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
