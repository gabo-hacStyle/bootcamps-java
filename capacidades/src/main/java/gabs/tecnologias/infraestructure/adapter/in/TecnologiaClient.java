package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.dto.CapacidadResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component

public class TecnologiaClient {

    private final WebClient webClient;

    public TecnologiaClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8080/technology").build();
    }

    public Mono<Boolean> existsTechById(Long id) {
        return webClient.get()
                .uri("/exists/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);

    }

    public Mono<CapacidadResponse.Tecnologias> getById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(CapacidadResponse.Tecnologias.class)
                .defaultIfEmpty(new CapacidadResponse.Tecnologias(null, ""));

    }


}
