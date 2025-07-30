package gabs.bootcamps.application.listener;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.event.BootcampCreatedEvent;
import gabs.bootcamps.dto.BootcampResponse;
import gabs.bootcamps.infraestructure.adapter.out.ReportsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampCreatedEventListener {

    private final BootcampUseCases bootcampUseCases;
    private final ReportsClient reportsClient;

    @Async
    @EventListener
    public void handleBootcampCreatedEvent(BootcampCreatedEvent event) {
        log.info("Procesando evento de creación de bootcamp con ID: {}", event.getBootcampId());
        
        bootcampUseCases.findById(event.getBootcampId())
                .flatMap(bootcampResponse -> {
                    log.info("Enviando reporte para bootcamp: {}", bootcampResponse.getNombre());
                    return reportsClient.postBootcampReport(bootcampResponse);
                })
                .doOnSuccess(result -> log.info("Reporte enviado exitosamente para bootcamp ID: {}", event.getBootcampId()))
                .doOnError(error -> log.error("Error al enviar reporte para bootcamp ID: {} - Error: {}", 
                    event.getBootcampId(), error.getMessage()))
                .subscribe();
    }
} 