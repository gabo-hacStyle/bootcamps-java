package gabs.personas.domain.port;

import gabs.personas.domain.model.BootcampPersona;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampPersonaRepositoryPort {
    Flux<BootcampPersona> findByBootcampId(Long bootcampId);
    Flux<BootcampPersona> findByPersonaId(Long personaId);
    Mono<BootcampPersona> save(BootcampPersona bootcampPersona);

}
