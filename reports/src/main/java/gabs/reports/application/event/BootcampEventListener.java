package gabs.reports.application.event;

import gabs.reports.domain.event.PersonaInscritaEnBootcampEvent;
import gabs.reports.domain.port.BootcampRepositoryPort;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampEventListener {
    private final BootcampRepositoryPort bootcampRepository;


        
    @EventListener
    public void handlePersonaInscrita(PersonaInscritaEnBootcampEvent event) {
        log.info("Procesando solicitud de datos para persona con id {}", event.getPersonaId());
        bootcampRepository.findByBootcampId(event.getBootcampId())
                .doOnNext(bootcamp -> log.info("Bootcamp encontrado: {}", bootcamp))
            .flatMap(bootcamp -> {
                if (!bootcamp.getPersonasInscritas().contains(event.getPersonaId())) {
                    bootcamp.getPersonasInscritas().add(event.getPersonaId());
                    bootcamp.setCantidadPersonasInscritas(bootcamp.getCantidadPersonasInscritas() + 1);
                }
                return bootcampRepository.save(bootcamp);
            })
                .subscribe(
                        b -> log.info("Bootcamp actualizado: {}", b),
                        error -> log.error("Error al actualizar bootcamp", error)
                );
    }
} 