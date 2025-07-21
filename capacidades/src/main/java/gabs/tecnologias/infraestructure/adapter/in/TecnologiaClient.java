package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.dto.CapacidadResponse;
import gabs.tecnologias.dto.Tecnologias;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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

    public Mono<Tecnologias> getById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Tecnologias.class)
                .defaultIfEmpty(new Tecnologias(null, ""));

    }

    public Flux<Tecnologias> getTecnologiasByCapacidadId(Long id) {
        return webClient.get()
                .uri("/capacidad/{id}", id)
                .retrieve()
                .bodyToFlux(Tecnologias.class);
    }


}
