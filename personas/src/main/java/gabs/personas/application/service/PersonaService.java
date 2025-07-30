package gabs.personas.application.service;

import gabs.personas.application.port.PersonaUseCases;
import gabs.personas.domain.exception.*;
import gabs.personas.domain.model.Persona;
import gabs.personas.domain.port.PersonaRepositoryPort;

import gabs.personas.dto.PersonaReportResponse;
import gabs.personas.infraestructure.adapter.out.clients.ReportClient;
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
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new PersonaNotFoundException(id)));
    }

    @Override
    public Mono<Persona> register(Persona request) {
        // Validar datos de entrada
        validatePersonaData(request);
        
        return repository.existsByCorreo(request.getCorreo())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new PersonaAlreadyExistsException(request.getCorreo()));
                    } else {
                        return repository.save(request)
                                .flatMap(personaGuardada -> {
                                    PersonaReportResponse response = personaMapperToPersonaReportResponse(personaGuardada);
                                    log.info("PersonaReport enviada a webclient: {}", response);
                                    return webClient.postPersonaReport(response)
                                            .thenReturn(personaGuardada)
                                            .onErrorMap(throwable -> new ExternalServiceException("ReportClient", throwable));
                                });
                    }
                });
    }
    @Override
    public Mono<Persona> updateParcial(Long id, Persona changes) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new PersonaNotFoundException(id)))
                .flatMap(original -> {
                    if (changes.getNombre() != null) {
                        original.setNombre(changes.getNombre());
                    }
                    if (changes.getCorreo() != null) {
                        // Validar que el nuevo correo no esté en uso por otra persona
                        return repository.existsByCorreo(changes.getCorreo())
                                .flatMap(exists -> {
                                    if (exists && !original.getCorreo().equals(changes.getCorreo())) {
                                        return Mono.error(new PersonaAlreadyExistsException(changes.getCorreo()));
                                    }
                                    original.setCorreo(changes.getCorreo());
                                    return repository.save(original);
                                });
                    }
                    return repository.save(original);
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id){
        return repository.existsById(id);
    }

    @Override
    public Mono<Void> delete(Long id) { 
        return repository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new PersonaNotFoundException(id));
                    }
                    return repository.deleteById(id);
                });
    }

    private PersonaReportResponse personaMapperToPersonaReportResponse(Persona persona){
         PersonaReportResponse personaResponse = new PersonaReportResponse();
         personaResponse.setPersonaId(persona.getId());
         personaResponse.setCorreo(persona.getCorreo());
         personaResponse.setNombre(persona.getNombre());
         personaResponse.setEdad(persona.getEdad());

         return personaResponse;
    }

    private void validatePersonaData(Persona persona) {
        if (persona.getNombre() == null || persona.getNombre().trim().isEmpty()) {
            throw new InvalidPersonaDataException("nombre", persona.getNombre());
        }
        if (persona.getCorreo() == null || persona.getCorreo().trim().isEmpty()) {
            throw new InvalidPersonaDataException("correo", persona.getCorreo());
        }
        if (!persona.getCorreo().contains("@")) {
            throw new InvalidPersonaDataException("correo", "Debe contener un formato de email válido");
        }
        if (persona.getEdad() != null && (persona.getEdad() < 0 || persona.getEdad() > 150)) {
            throw new InvalidPersonaDataException("edad", String.valueOf(persona.getEdad()));
        }
    }
}
