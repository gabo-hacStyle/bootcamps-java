package gabs.capacidades.infraestructure.adapter.out;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import gabs.capacidades.domain.model.CapacidadBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SpringDataCapacidadBootcampRepository extends ReactiveCrudRepository<CapacidadBootcamp, Long> {
    Flux<CapacidadBootcamp> findByBootcampId(Long bootcampId);


    @Query("""
    SELECT bc1.capacidad_id
    FROM capacidad_bootcamp bc1
    WHERE bc1.bootcamp_id = :idBootcamp
      AND NOT EXISTS (
        SELECT 1
        FROM capacidad_bootcamp bc2
        WHERE bc2.capacidad_id = bc1.capacidad_id
          AND bc2.bootcamp_id <> :idBootcamp
      )
    """)
    Flux<Long> findExclusiveCapacidadesOfBootcamp(@Param("idBootcamp") Long idBootcamp);

    @Query("DELETE FROM capacidad_bootcamp WHERE bootcamp_id = :bootcampId")
    Mono<Void> deleteByBootcampId(Long bootcampId);

}
