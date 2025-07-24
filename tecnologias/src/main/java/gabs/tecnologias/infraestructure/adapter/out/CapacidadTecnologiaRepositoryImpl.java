package gabs.tecnologias.infraestructure.adapter.out;


import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.domain.port.CapacidadTecnologiaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CapacidadTecnologiaRepositoryImpl implements CapacidadTecnologiaRepositoryPort {

    private  final SpringDataCapacidadTecnologiaRepository repository;

    @Override
    public Flux<CapacidadTecnologia> findByCapacidadId(Long capacidadId) {
        return repository.findByCapacidadId(capacidadId);
    }

    @Override
    public Mono<CapacidadTecnologia> save(CapacidadTecnologia capacidadTecnologia) {
        return repository.save(capacidadTecnologia);
    }

    @Override
    public Flux<Long> findExclusiveTechsByCapacidadesIds(List<Long> capacidadId) {
        return repository.findExclusiveTechsByCapacidadesIds(capacidadId);
    }

    @Override
    public Mono<Void> deleteByCapacidadesIds(List<Long> capacidadesIds) {
        return repository.deleteByCapacidadesIds(capacidadesIds);
    }
}
