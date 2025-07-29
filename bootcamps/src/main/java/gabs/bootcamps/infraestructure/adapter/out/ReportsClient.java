package gabs.bootcamps.infraestructure.adapter.out;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ReportsClient {

    private final WebClient webClient;

    public CapacidadesClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8084/reports").build();
    }

    public Mono<Void> postCapacidadesByBootcampId(Long id, List<Long> capsList){
        return webClient.post()
                .uri("/bootcamp/{id}", id)
                .bodyValue(capsList)
                .retrieve()
                .bodyToMono(Void.class);
    }



}
