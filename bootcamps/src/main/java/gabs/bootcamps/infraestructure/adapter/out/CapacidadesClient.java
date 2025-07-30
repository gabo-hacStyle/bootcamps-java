package gabs.bootcamps.infraestructure.adapter.out;


import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.CapacidadDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component

public class CapacidadesClient {

    private final WebClient webClient;

    public CapacidadesClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://capacidades-micro:8081/skill").build();
    }

    public Mono<Boolean> existsCapsById(Long id) {
        return webClient.get()
                .uri("/exists/{id}", id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false);
    }

    public Mono<Void> postCapacidadesByBootcampId(Long id, List<Long> capsList){
        return webClient.post()
                .uri("/bootcamp/{id}", id)
                .bodyValue(capsList)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<CapacidadDTO> getById(Long id) {
        return webClient.get()
                .uri("/bootcamp/{id}", id)
                .retrieve()
                .bodyToFlux(CapacidadDTO.class);

    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/bootcamp/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }


}
