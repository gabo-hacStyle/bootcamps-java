package gabs.bootcamps.application.service;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.domain.model.CapacidadTecnologia;
import gabs.bootcamps.domain.port.BootcampRepositoryPort;
import gabs.bootcamps.domain.port.CapacidadTecnologiaRepositoryPort;
import gabs.bootcamps.dto.BootcampRequest;
import gabs.bootcamps.dto.CapacidadDTO;
import gabs.bootcamps.dto.CapacidadRequest;
import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.PageAndQuery;
import gabs.bootcamps.infraestructure.adapter.in.CapacidadesClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BootcampService implements BootcampUseCases {

    private final BootcampRepositoryPort repository;
    private final CapacidadTecnologiaRepositoryPort capacidadTecnologiaRepository;
    private final CapacidadesClient capacidadesClient;

    @Override
    public Flux<BootcampResponse> findAll() {
        return repository.findAll()
                .flatMap(this::enriquecerBootcampConCapacidades);

    }


    private Mono<BootcampResponse> enriquecerBootcampConCapacidades(Bootcamp bootcamp) {
        return capacidadesClient.get()
                .uri("/capacidades-bootcamp/{bootcampId}", bootcamp.getId())
                .retrieve()
                .bodyToFlux(CapacidadDTO.class)
                .collectList()
                .map(capacidades -> {
                    BootcampResponse dto = new BootcampResponse();
                    dto.setId(bootcamp.getId());
                    dto.setNombre(bootcamp.getNombre());
                    dto.setDescripcion(bootcamp.getDescripcion());
                    dto.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
                    dto.setDuracion(bootcamp.getDuracion());
                    dto.setCapacidades(capacidades);
                    return dto;
                });
    }


    @Override
    public Mono<BootcampResponse> findById(Long id) {

        // Paso 1: Busca la capacidad
        return repository.findById(id)
                .flatMap(capacidad ->
                        // Paso 2: Busca los registros de la tabla intermedia
                        capacidadTecnologiaRepository.findByCapacidadId(id)
                                // Paso 3: Obtén el id de cada tecnología
                                .map(CapacidadTecnologia::getTecnologiaId)
                                // Paso 4: Llama al micro de tecnologías y obtén el DTO por cada id
                                .flatMap(capacidadesClient::getById)
                                // Paso 5: Junta todos los DTO en una lista
                                .collectList()
                                // Paso 6: Arma el response
                                .map(tecnologias -> {
                                    BootcampResponse response = new BootcampResponse();
                                    response.setNombre(capacidad.getNombre());
                                    response.setDescripcion(capacidad.getDescripcion());
                                    response.setId(capacidad.getId());
                                    response.setTecnologiasList(tecnologias);
                                    return response;
                                })
                );
    }
    @Override
    public Mono<Bootcamp> register(BootcampRequest request) {
        if (request.getCapacidades() == null || request.getCapacidades().size() < 1 || request.getCapacidades().size() > 4)
            return Mono.error(new IllegalArgumentException("Debe asociar entre 1 y 4 capacidades."));

        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setNombre(request.getNombre());
        bootcamp.setDescripcion(request.getDescripcion());
        bootcamp.setFechaLanzamiento(request.getFecha());
        bootcamp.setDuracion(request.getDuracion());

        return repository.save(bootcamp)
                .flatMap(saved -> capacidadesClient.post()
                        .uri("/capacidades-bootcamp")
                        .bodyValue(new AsociarCapacidadesRequest(saved.getId(),
                                request.getCapacidades().stream().map(CapacidadDTO::getId).toList()))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .thenReturn(saved)
                );
    }


    //@Override
    //public Mono<Bootcamp> findByNombre(String nombre){
      //  return repository.findByNombre(nombre);
//    }

    @Override
    public Mono<Void> delete(Long id) {
        return capacidadesClient.delete(id)
                .then(repository.deleteById(id));
    }


}
