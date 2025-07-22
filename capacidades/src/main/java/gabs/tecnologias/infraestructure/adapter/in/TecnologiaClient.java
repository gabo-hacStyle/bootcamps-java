package gabs.tecnologias.infraestructure.adapter.in;


import gabs.tecnologias.dto.CapacidadResponse;
import gabs.tecnologias.dto.Tecnologias;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component

public class TecnologiaClient {

    private final WebClient webClient;

    public TecnologiaClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://tecnologias-micro:8085/technology").build();
    }

    public Mono<Boolean> existsTechById(Long id) {
        return webClient.get()
                .uri("/exists/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);

    }



    public Flux<Tecnologias> getTecnologiasByCapacidadId(Long id) {
        return webClient.get()
                .uri("/capacidad/{id}", id)
                .retrieve()
                .bodyToFlux(Tecnologias.class);
    }

    public Mono<Void> postTecnologiasByCapacidadId(Long id, List<Long> techsList) {
        return webClient.post()
                .uri("/capacidad/{id}", id)
                .bodyValue(techsList)
                .retrieve()
                .bodyToMono(Void.class);

    }


}
