package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.Capacidad;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataCapacidadRepository extends ReactiveCrudRepository<Capacidad, Long> {
    Mono<Capacidad> findByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);
    @Query("SELECT * FROM capacidad ORDER BY nombre ASC LIMIT :size OFFSET :offset")
    Flux<Capacidad> findPagedByNombreAsc(@Param("size") int size, @Param("offset") int offset);

    @Query("SELECT * FROM capacidad ORDER BY nombre DESC LIMIT :size OFFSET :offset")
    Flux<Capacidad> findPagedByNombreDesc(@Param("size") int size, @Param("offset") int offset);


}
