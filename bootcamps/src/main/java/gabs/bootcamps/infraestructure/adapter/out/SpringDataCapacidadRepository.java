package gabs.bootcamps.infraestructure.adapter.out;

import gabs.bootcamps.domain.model.Bootcamp;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataCapacidadRepository extends ReactiveCrudRepository<Bootcamp, Long> {
    Mono<Bootcamp> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);
    @Query("SELECT * FROM bootcamp ORDER BY nombre ASC LIMIT :size OFFSET :offset")
    Flux<Bootcamp> findPagedByNombreAsc(@Param("size") int size, @Param("offset") int offset);

    @Query("SELECT * FROM bootcamp ORDER BY nombre DESC LIMIT :size OFFSET :offset")
    Flux<Bootcamp> findPagedByNombreDesc(@Param("size") int size, @Param("offset") int offset);


}
