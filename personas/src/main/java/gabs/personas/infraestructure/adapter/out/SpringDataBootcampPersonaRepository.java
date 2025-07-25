package gabs.personas.infraestructure.adapter.out;

import gabs.personas.domain.model.BootcampPersona;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataBootcampPersonaRepository extends ReactiveCrudRepository<BootcampPersona, Long> {
    Flux<BootcampPersona> findByBootcampId(Long bootcampId);
    Flux<BootcampPersona> findByPersonaId(Long personaId);




    @Query("DELETE FROM capacidad_bootcamp WHERE bootcamp_id = :bootcampId")
    Mono<Void> deleteByBootcampId(Long bootcampId);

}
