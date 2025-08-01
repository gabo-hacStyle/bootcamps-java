package gabs.bootcamps.application.port;


import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.dto.BootcampRequest;
import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.BootcampSimpleResponse;
import gabs.bootcamps.dto.PageAndQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampUseCases {

     Flux<BootcampResponse> findAll(PageAndQuery consult);
     Mono<BootcampResponse> findById(Long id);
     Flux<BootcampSimpleResponse> findByIdSimpleResponse(List<Long> bootcampsId);

     Mono<Bootcamp> register(BootcampRequest request);

    // Mono<Bootcamp> findByNombre(String nombre);
     Mono<Void> delete(Long id);
}
