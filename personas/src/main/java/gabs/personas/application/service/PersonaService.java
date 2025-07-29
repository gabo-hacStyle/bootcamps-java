package gabs.personas.application.service;

import gabs.personas.application.port.PersonaUseCases;
import gabs.personas.domain.model.Persona;
import gabs.personas.domain.port.PersonaRepositoryPort;

import gabs.personas.dto.PersonaReportResponse;
import gabs.personas.infraestructure.adapter.out.ReportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaService implements PersonaUseCases {

    private final PersonaRepositoryPort repository;
    private final ReportClient webClient;



    @Override
    public Flux<Persona> findAll() {
        return repository.findAll();

    }

    @Override
    public Mono<Persona> findById(Long id) {


        return repository.findById(id);
    }

    @Override

    public Mono<Persona> register(Persona request) {
        return repository.existsByCorreo(request.getCorreo())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El correo ya está en uso"));
                    } else {
                        return repository.save(request)
                                .flatMap(personaGuardada -> {
                                    PersonaReportResponse response = personaMapperToPersonaReportResponse(personaGuardada);
                                    log.info("PersonaReport enviada a webclient: {}", response);
                                    return webClient.postPersonaReport(response)
                                            .thenReturn(personaGuardada);
                                });
                    }
                });
    }
    @Override
    public Mono<Persona> updateParcial(Long id, Persona changes) {
        return repository.findById(id)
                .flatMap(original -> {
                    if (changes.getNombre() != null) {
                        original.setNombre(changes.getNombre());
                    }
                    if (changes.getCorreo() != null) {
                        original.setCorreo(changes.getCorreo());
                    }

                    return repository.save(original);
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id){
        return repository.existsById(id);
    }

    @Override
    public Mono<Void> delete(Long id) { return repository.deleteById(id); }

    private PersonaReportResponse personaMapperToPersonaReportResponse(Persona persona){
         PersonaReportResponse personaResponse = new PersonaReportResponse();
         personaResponse.setPersonaId(persona.getId());
         personaResponse.setCorreo(persona.getCorreo());
         personaResponse.setNombre(persona.getNombre());
         personaResponse.setEdad(persona.getEdad());

         return personaResponse;
    }


}
