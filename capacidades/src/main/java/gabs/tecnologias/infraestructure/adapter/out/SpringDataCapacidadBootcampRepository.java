package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.CapacidadBootcamp;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SpringDataCapacidadBootcampRepository extends ReactiveCrudRepository<CapacidadBootcamp, Long> {
    Flux<CapacidadBootcamp> findByBootcampId(Long bootcampId);


    @Query("""
    SELECT bc1.capacidad_id
    FROM CapacidadBootcamp bc1
    WHERE bc1.bootcamp_id = :idBootcamp
      AND NOT EXISTS (
        SELECT 1
        FROM CapacidadBootcamp bc2
        WHERE bc2.capacidad_id = bc1.capacidad_id
          AND bc2.bootcamp_id <> :idBootcamp
      )
    """)
    Flux<Long> findExclusiveCapacidadesOfBootcamp(@Param("idBootcamp") Long idBootcamp);
}
