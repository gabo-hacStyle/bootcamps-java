package gabs.reports.application.event;

import gabs.reports.domain.event.PersonasDataRequestEvent;
import gabs.reports.domain.port.PersonaRepositoryPort;
import gabs.reports.dto.PersonaInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonasDataEventListener {
    private final PersonaRepositoryPort personaRepository;

    @EventListener
    public void handlePersonasDataRequest(PersonasDataRequestEvent event) {
        log.info("Procesando solicitud de datos para {} personas", event.getPersonaIds().size());

        Flux.fromIterable(event.getPersonaIds())
                .doOnNext(id -> log.info("Buscando persona con id {}", id))
                .flatMap(personaRepository::findByPersonaId)
                .doOnNext(persona -> log.info("Persona encontrada: {}", persona))
                .map(persona -> new PersonaInfo(persona.getNombre(), persona.getCorreo()))
                .collectList()
                .subscribe(
                        personasInfo -> {
                            log.info("Datos de personas obtenidos exitosamente: {}", personasInfo.size());
                            event.getFuture().complete(personasInfo);
                        },
                        error -> {
                            log.error("Error obteniendo datos de personas: {}", error.getMessage());
                            event.getFuture().completeExceptionally(error);
                        }
                );
    }
} 