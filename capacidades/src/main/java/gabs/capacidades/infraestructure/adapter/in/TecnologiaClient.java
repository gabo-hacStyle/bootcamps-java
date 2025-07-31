package gabs.capacidades.infraestructure.adapter.in;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.CapacidadTecnologiasRequest;
import gabs.capacidades.dto.Tecnologias;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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

    public Mono<Void> postTecnologiasByCapacidadId(Long id, CapacidadTecnologiasRequest techsList) {
        return webClient.post()
                .uri("/capacidad/{id}", id)
                .bodyValue(techsList)
                .retrieve()
                .bodyToMono(Void.class);

    }

    public Mono<Void> deleteTechnologiasByCapacidadDeleted(List<Long> capacidadesIds){

        String idsParam = capacidadesIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/capacidad")
                        .queryParam("ids", idsParam)
                        .build()
                )
                .retrieve()
                .bodyToMono(Void.class);

    }


}
