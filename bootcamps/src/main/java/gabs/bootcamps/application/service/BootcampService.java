package gabs.bootcamps.application.service;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.model.Bootcamp;

import gabs.bootcamps.domain.port.BootcampRepositoryPort;

import gabs.bootcamps.dto.BootcampRequest;
import gabs.bootcamps.dto.CapacidadDTO;

import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.dto.PageAndQuery;
import gabs.bootcamps.infraestructure.adapter.in.CapacidadesClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BootcampService implements BootcampUseCases {

    private final BootcampRepositoryPort repository;
    private final CapacidadesClient capacidadesClient;

    @Override
    public Flux<BootcampResponse> findAll() {
        return null;
    }





    @Override
    public Mono<BootcampResponse> findById(Long id) {

       return null;
    }

    @Override
    public Mono<Bootcamp> register(BootcampRequest request) {
        return validateDoubleCapacities(request.getCapacidades())
                .then(validateCapsQuantity(request.getCapacidades()))
                .flatMap(capsIds -> {
                    Bootcamp bootcamp = new Bootcamp();
                    bootcamp.setNombre(request.getNombre());
                    bootcamp.setDescripcion(request.getDescripcion());
                    bootcamp.setFechaLanzamiento(LocalDate.now());
                    bootcamp.setDuracion(request.getDuracion());

                    return repository.save(bootcamp)
                            .flatMap(saved ->
                                    capacidadesClient.postCapacidadesByBootcampId(saved.getId(), capsIds)
                                            .then(Mono.just(saved))
                            );
                });




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

    private Mono<List<Long>> validateCapsQuantity(List<Long> capacidades) {
        if (capacidades == null || capacidades.size() < 1 || capacidades.size() > 4) {
            return Mono.error(new IllegalArgumentException("Debe asociar entre 1 y 4 capacidades."));
        }

        return Mono.just(capacidades);
    }

    private Mono<Void> validateDoubleCapacities(List<Long> capacidades) {
        HashSet<Long> set = new HashSet<>(capacidades);
        if (set.size() != capacidades.size()) {
            return Mono.error(new IllegalArgumentException("No se permiten tecnologías repetidas."));
        }
        return Mono.empty();
    }




}
