package gabs.personas.application.port;


import gabs.personas.dto.EnrollRequest;
import gabs.personas.dto.PersonaRegisteredResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampPersonaUseCases {
    //Flux<CapacidadBootcampResponse> getAllByBootcamp(Long bootcampId);

    Mono<PersonaRegisteredResponse> registerInBootcamp(EnrollRequest req);

    //Flux<BootcampPersona> saveCapacidadBootcamp(Long bootcampId, List<Long> capacidadesList);
//
    //Mono<Void> deleteCapacidadesByBootcampId(Long bootcampId);

}
