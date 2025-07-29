package gabs.bootcamps.infraestructure.adapter.out;


import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.dto.BootcampResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ReportsClient {

    private final WebClient webClient;

    public ReportsClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://reports-micro:8084/reports").build();
    }

    public Mono<Void> postBootcampReport(BootcampResponse bootcamp){
        return webClient.post()
                .uri("/bootcamps")
                .bodyValue(bootcamp)
                .retrieve()
                .bodyToMono(Void.class);
    }



}
