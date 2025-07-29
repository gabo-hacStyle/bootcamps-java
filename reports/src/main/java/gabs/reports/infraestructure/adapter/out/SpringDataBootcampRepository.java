package gabs.reports.infraestructure.adapter.out;

import gabs.reports.domain.model.Bootcamp;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


public interface SpringDataBootcampRepository extends ReactiveMongoRepository<Bootcamp, String> {
    Mono<Bootcamp> findByBootcampId(Long id);
} 