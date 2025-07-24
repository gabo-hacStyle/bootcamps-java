package gabs.tecnologias.infraestructure.adapter.out;

import gabs.tecnologias.domain.model.Tecnologia;
import gabs.tecnologias.domain.port.TecnologiaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor

public class TecnologiaRepositoryImpl implements TecnologiaRepositoryPort {

    private final SpringDataTecnologiaRepository repository;




    @Override
    public Flux<Tecnologia> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Tecnologia> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Tecnologia> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        return repository.existsByNombre(nombre);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Tecnologia> save(Tecnologia tecnologia) {
        return repository.save(tecnologia);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<Long> ids) {
        return repository.deleteAllById(ids);
    }
}
