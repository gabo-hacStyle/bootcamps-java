package gabs.personas.infraestructure.adapter.out;

import gabs.personas.domain.model.BootcampPersona;
import gabs.personas.domain.port.BootcampPersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BootcampPersonaRepositoryImpl implements BootcampPersonaRepositoryPort {

    private final SpringDataBootcampPersonaRepository repository;

    @Override
    public Flux<BootcampPersona> findByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId);
    }

    @Override
    public Flux<BootcampPersona> findByPersonaId(Long personaId) {
        return repository.findByPersonaId(personaId);
    }

    @Override
    public Mono<BootcampPersona> save(BootcampPersona bootcampPersona) {
        return repository.save(bootcampPersona);
    }


}
