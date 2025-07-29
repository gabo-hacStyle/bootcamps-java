package gabs.bootcamps.application.listener;

import gabs.bootcamps.application.service.BootcampService;
import gabs.bootcamps.domain.event.BootcampCreatedEvent;
import gabs.bootcamps.infraestructure.adapter.out.ReportsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampCreatedListener {

    private final BootcampService bootcampService;
    private final ReportsClient reportsClient;

    @EventListener
    public void onBootcampCreated(BootcampCreatedEvent event) {
        log.info("Enviando reporte para bootcampId: {}", event.getBootcampId());
        bootcampService.findById(event.getBootcampId())
                .doOnNext(response -> log.info("Payload a enviar al reporte: {}", response))
                .flatMap(reportsClient::postBootcampReport)
                .subscribe();
    }
}
