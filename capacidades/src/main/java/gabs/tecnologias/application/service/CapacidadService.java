package gabs.tecnologias.application.service;

import gabs.tecnologias.application.port.CapacidadUseCases;
import gabs.tecnologias.domain.model.Capacidad;
import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.domain.port.CapacidadRepositoryPort;
import gabs.tecnologias.domain.port.CapacidadTecnologiaRepositoryPort;
import gabs.tecnologias.dto.CapacidadRequest;
import gabs.tecnologias.infraestructure.adapter.in.TecnologiaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacidadService implements CapacidadUseCases {

    private final CapacidadRepositoryPort repository;
    private final CapacidadTecnologiaRepositoryPort capacidadTecnologiaRepository;
    private final TecnologiaClient tecnologiaClient;

    @Override
    public Flux<Capacidad> findAll() { return repository.findAll(); }
    @Override
    public Mono<Capacidad> findById(Long id) { return repository.findById(id); }
    @Override
    public Mono<Capacidad> register(CapacidadRequest request) {
        return validateTechQuantity(request)
                .then(validateDoubleTechs(request))
                .then(validateTechsExist(request))
                .flatMap(validIds -> {
                    Capacidad capacidad = new Capacidad();
                    capacidad.setNombre(request.getNombre());
                    capacidad.setDescripcion(request.getDescripcion());

                    return repository.save(capacidad)
                            .flatMap(saved ->
                                    Flux.fromIterable(validIds)
                                            .map(tecnologiaId -> {
                                                CapacidadTecnologia ct = new CapacidadTecnologia();
                                                ct.setCapacidadId(saved.getId());
                                                ct.setTecnologiaId(tecnologiaId);
                                                return ct;
                                            })
                                            .flatMap(capacidadTecnologiaRepository::save)
                                            .then(Mono.just(saved))
                            );
                });
    }
    @Override
    public Mono<Capacidad> updateParcial(Long id, CapacidadRequest changes) {
        return repository.findById(id)
                .flatMap(original -> {
                    if (changes.getNombre() != null) {
                        original.setNombre(changes.getNombre());
                    }
                    if (changes.getDescripcion() != null) {
                        original.setDescripcion(changes.getDescripcion());
                    }

                    Mono<Void> techValidations = Mono.empty();
                    if (changes.getTecnologias() != null) {
                        techValidations = validateTechQuantity(changes)
                                .then(validateDoubleTechs(changes))
                                .then(validateTechsExist(changes))
                                .flatMap(validIds ->
                                        capacidadTecnologiaRepository.deleteByCapacidadId(id)
                                                .thenMany(Flux.fromIterable(validIds)
                                                        .map(tecnologiaId -> {
                                                            CapacidadTecnologia ct = new CapacidadTecnologia();
                                                            ct.setCapacidadId(id);
                                                            ct.setTecnologiaId(tecnologiaId);
                                                            return ct;
                                                        })
                                                        .flatMap(capacidadTecnologiaRepository::save)
                                                )
                                                .then()
                                );
                    }

                    return techValidations.then(repository.save(original));
                });
    }

    @Override
    public Mono<Capacidad> findByNombre(String nombre){
        return repository.findByNombre(nombre);
    }

    @Override
    public Mono<Void> delete(Long id) { return repository.deleteById(id); }


    private Mono<Void> validateTechQuantity(CapacidadRequest request) {
        if (request.getTecnologias() == null ||
                request.getTecnologias().size() < 3 ||
                request.getTecnologias().size() > 20) {
            return Mono.error(new IllegalArgumentException("La capacidad debe tener entre 3 y 20 tecnologías."));
        }
        return Mono.empty();
    }

    // Validar tecnologías duplicadas
    private Mono<Void> validateDoubleTechs(CapacidadRequest request) {
        HashSet<Long> set = new HashSet<>(request.getTecnologias());
        if (set.size() != request.getTecnologias().size()) {
            return Mono.error(new IllegalArgumentException("No se permiten tecnologías repetidas."));
        }
        return Mono.empty();
    }

    private Mono<List<Long>> validateTechsExist(CapacidadRequest request) {
        List<Long> requestedIds = request.getTecnologias();

        return Flux.fromIterable(requestedIds)
                .flatMap(id ->
                        tecnologiaClient.existsTechById(id)
                                .map(exists -> new AbstractMap.SimpleEntry<>(id, exists))
                )
                .collectList()
                .flatMap(results -> {
                    List<Long> invalidIds = results.stream()
                            .filter(entry -> !entry.getValue())
                            .map(Map.Entry::getKey)
                            .toList();

                    if (!invalidIds.isEmpty()) {
                        String msg = "Las siguientes tecnologías no existen: " + invalidIds;
                        return Mono.error(new IllegalArgumentException(msg));
                    }

                    // Solo devuelve los ids válidos (o todos, si todos existen)
                    List<Long> validIds = results.stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    return Mono.just(validIds);
                });
    }
}
