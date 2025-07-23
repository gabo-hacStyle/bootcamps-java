package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.CapacidadTecnologia;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataCapacidadTecnologiaRepository extends ReactiveCrudRepository<CapacidadTecnologia, Long> {
    Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId);
    Mono<Void> deleteByCapacidadId(Long capacidadId);



    @Query("""
    SELECT ct1.tecnologia_id
    FROM CapacidadTecnologia ct1
    WHERE ct1.capacidad_id = :capacidadId
      AND NOT EXISTS (
        SELECT 1
        FROM CapacidadTecnologia ct2
        WHERE ct2.tecnologia_id = ct1.tecnologia_id
          AND ct2.capacidad_id <> :capacidadId
      )
    """)
    Flux<Long> findExclusiveTechsOfCapacidad(@Param("capacidadId") Long capacidadId);

}
