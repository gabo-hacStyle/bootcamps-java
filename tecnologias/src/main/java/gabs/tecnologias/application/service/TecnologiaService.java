package gabs.tecnologias.application.service;

import gabs.tecnologias.application.port.TecnologiaUseCases;
import gabs.tecnologias.domain.model.Tecnologia;
import gabs.tecnologias.domain.port.TecnologiaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TecnologiaService implements TecnologiaUseCases {
    private final TecnologiaRepositoryPort repository;



    @Override
    public Flux<Tecnologia> findAll() { return repository.findAll(); }
    @Override
    public Mono<Tecnologia> findById(Long id) { return repository.findById(id); }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }
    @Override
    public Mono<Tecnologia> create(Tecnologia tecnologia) {
        return repository.existsByNombre(tecnologia.getNombre())
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalArgumentException("El nombre de la tecnología ya existe"))
                        : repository.save(tecnologia));
    }
    @Override
    public Mono<Tecnologia> updateParcial(Long id, Tecnologia changes) {
        return repository.findById(id)
                .flatMap(original -> {
                    if (changes.getNombre() != null) {
                        original.setNombre(changes.getNombre());
                    }
                    if (changes.getDescripcion() != null) {
                        original.setDescripcion(changes.getDescripcion());
                    }
                    return repository.save(original);
                });
    }

    @Override
    public Mono<Tecnologia> findByNombre(String nombre){
        return repository.findByNombre(nombre);
    }

    @Override
    public Mono<Void> delete(Long id) { return repository.deleteById(id); }

    private Tecnologia setId(Tecnologia t, Long id) {
        t.setId(id);
        return t;
    }
}
