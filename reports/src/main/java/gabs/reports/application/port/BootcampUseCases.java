package gabs.reports.application.port;

import gabs.reports.domain.model.Bootcamp;
import gabs.reports.dto.BootcampRequest;
import gabs.reports.dto.BootcampResponse;
import gabs.reports.dto.BootcampSimpleResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampUseCases {

     //Flux<BootcampResponse> findAll(PageAndQuery consult);
     Mono<BootcampResponse> findById(Long id);
     //Flux<BootcampSimpleResponse> findByIdSimpleResponse(List<Long> bootcampsId);

     Mono<Bootcamp> register(BootcampRequest request);
     Mono<BootcampResponse> findBootcampConMasInscritos();

    // Mono<Bootcamp> findByNombre(String nombre);
     //Mono<Void> delete(Long id);
}
