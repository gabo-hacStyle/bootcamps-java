package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.CapacidadTecnologia;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SpringDataCapacidadTecnologiaRepository extends ReactiveCrudRepository<CapacidadTecnologia, Long> {
    Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId);




    @Query("""
    SELECT tecnologia_id
    FROM capacidades_tecnologia
    GROUP BY tecnologia_id
    HAVING
        SUM(CASE WHEN capacidad_id NOT IN (:capacidadesIds) THEN 1 ELSE 0 END) = 0
        AND MAX(CASE WHEN capacidad_id IN (:capacidadesIds) THEN 1 ELSE 0 END) = 1
    """)
    Flux<Long> findExclusiveTechsByCapacidadesIds(List<Long> capacidadesIds);


    @Query("DELETE FROM capacidades_tecnologia WHERE capacidad_id IN (:capacidadesIds)")
    Mono<Void> deleteByCapacidadesIds(List<Long> capacidadesIds);



}
