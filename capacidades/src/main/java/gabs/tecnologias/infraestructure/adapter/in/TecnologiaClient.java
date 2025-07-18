package gabs.tecnologias.infraestructure.adapter.in;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component

public class TecnologiaClient {

    private final WebClient webClient;

    public TecnologiaClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8080").build();
    }

    public Mono<Boolean> existsTechById(Long id) {
        return webClient.get()
                .uri("/technology/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);

    }


}
