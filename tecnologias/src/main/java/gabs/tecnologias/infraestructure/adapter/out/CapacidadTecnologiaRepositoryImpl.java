package gabs.tecnologias.infraestructure.adapter.out;


import gabs.tecnologias.domain.model.CapacidadTecnologia;
import gabs.tecnologias.domain.port.CapacidadTecnologiaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Void> deleteByCapacidadId(Long capacidadId) {
        return repository.deleteByCapacidadId(capacidadId);
    }
}
