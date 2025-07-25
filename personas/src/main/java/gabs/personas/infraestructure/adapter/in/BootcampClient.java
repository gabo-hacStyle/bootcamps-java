package gabs.personas.infraestructure.adapter.in;


import gabs.personas.dto.BootcampSimpleResponse;
import gabs.personas.dto.Tecnologias;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component

public class BootcampClient {

    private final WebClient webClient;

    public BootcampClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8082/bootcamp").build();
    }



    public Flux<BootcampSimpleResponse> bringSimpleResponseForManyBootcamps(String idsString) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/simple")
                        .queryParam("ids", idsString)
                        .build())
                .retrieve()
                .bodyToFlux(BootcampSimpleResponse.class);
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
