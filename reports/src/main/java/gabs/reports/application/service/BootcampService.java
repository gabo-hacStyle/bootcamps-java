package gabs.reports.application.service;

import gabs.reports.application.port.BootcampUseCases;
import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Bootcamp;
import gabs.reports.domain.model.Persona;
import gabs.reports.domain.port.BootcampRepositoryPort;
import gabs.reports.domain.port.PersonaRepositoryPort;
import gabs.reports.dto.*;
import gabs.reports.domain.event.PersonasDataRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootcampService implements BootcampUseCases {

    private final BootcampRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;
    
    

 
    @Override
    public Mono<BootcampResponse> findById(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("ID del bootcamp no puede ser nulo"));
        }
        
        return repository.findByBootcampId(id)
                .flatMap(bootcamp ->
                        Mono.just(new BootcampResponse())
                                .map(response -> {
                                    response.setNombre(bootcamp.getNombre());
                                    response.setDescripcion(bootcamp.getDescripcion());
                                    response.setId(bootcamp.getBootcampId());
                                    response.setDuracion(bootcamp.getDuracion());
                                    response.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
                                    response.setFechaFinalizacion(bootcamp.getFechaFinalizacion());
                                    response.setCapacidades(bootcamp.getCapacidades());
                                    response.setTecnologias(bootcamp.getTecnologias());
                                    response.setCantidadCapacidades(bootcamp.getCantidadCapacidades());
                                    response.setCantidadTecnologias(bootcamp.getCantidadTecnologias());
                                    response.setCantidadPersonasInscritas(bootcamp.getCantidadPersonasInscritas());
                                    return response;
                                })
                )
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)));
    }

  

   
    @Override
    public Mono<Bootcamp> register(BootcampRequest request) {
        // Validaciones
        if (request == null) {
            return Mono.error(new ValidationException("Request no puede ser nulo"));
        }
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return Mono.error(new ValidationException("nombre", "No puede estar vacío"));
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            return Mono.error(new ValidationException("descripcion", "No puede estar vacío"));
        }
        if (request.getFechaLanzamiento() == null) {
            return Mono.error(new ValidationException("fechaLanzamiento", "No puede ser nulo"));
        }
        if (request.getDuracion() == null || request.getDuracion() <= 0) {
            return Mono.error(new ValidationException("duracion", "Debe ser mayor a 0"));
        }
        
        log.info("Registrando bootcamp: {}", request.getNombre());
        
        int cantidadCapacidades = request.getCapacidades() != null ? request.getCapacidades().size() : 0;
        int cantidadTecnologias = 0;
        if (request.getCapacidades() != null) {
            for (CapacidadDTO cap : request.getCapacidades()) {
                if (cap.getTecnologias() != null) {
                    cantidadTecnologias += cap.getTecnologias().size();
                }
            }
        }
        
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setNombre(request.getNombre());
        bootcamp.setDescripcion(request.getDescripcion());
        bootcamp.setFechaLanzamiento(request.getFechaLanzamiento());
        bootcamp.setDuracion(request.getDuracion());
        bootcamp.setFechaFinalizacion(request.getFechaFinalizacion());
        bootcamp.setCapacidades(request.getCapacidades() != null ?
            request.getCapacidades().stream().map(CapacidadDTO::getNombre).toList() : new java.util.ArrayList<>());
        bootcamp.setTecnologias(request.getCapacidades() != null ?
            request.getCapacidades().stream()
                .flatMap(cap -> cap.getTecnologias() != null ? cap.getTecnologias().stream().map(TecnologiaDTO::getNombre) : java.util.stream.Stream.empty())
                .toList() : new java.util.ArrayList<>());
        bootcamp.setCantidadCapacidades(cantidadCapacidades);
        bootcamp.setCantidadTecnologias(cantidadTecnologias);
        bootcamp.setCantidadPersonasInscritas(0);
        bootcamp.setBootcampId(request.getId());
        bootcamp.setPersonasInscritas(new java.util.ArrayList<>());
        
        return repository.save(bootcamp)
                .doOnSuccess(saved -> log.info("Bootcamp registrado exitosamente: {}", saved.getNombre()))
                .doOnError(error -> log.error("Error al registrar bootcamp: {}", error.getMessage()));
    }


   
    /**
     * Busca el bootcamp con la mayor cantidad de personas inscritas.
     * Retorna toda la información del bootcamp, el nombre y correo de cada persona inscrita,
     * cada una de las capacidades y cada una de las tecnologías asociadas.
     */
    public Mono<BootcampResponse> findBootcampConMasInscritos() {
        log.info("Buscando bootcamp con más inscritos");
        
        return repository.findAll()
                .sort((b1, b2) -> Integer.compare(
                        b2.getCantidadPersonasInscritas(),
                        b1.getCantidadPersonasInscritas()))
                .next()
                .flatMap(bootcamp -> {
                    log.info("Bootcamp con más inscritos encontrado: {} con {} inscritos", 
                            bootcamp.getNombre(), bootcamp.getCantidadPersonasInscritas());
                    
                    // 2. Usar Domain Events para obtener datos de personas de forma desacoplada
                    CompletableFuture<List<PersonaInfo>> future = new CompletableFuture<>();
                    eventPublisher.publishEvent(new PersonasDataRequestEvent(bootcamp.getPersonasInscritas(), future));
                    
                    return Mono.fromFuture(future)
                            .map(personasInfo -> {
                                // 3. Construir el BootcampResponse
                                BootcampResponse dto = new BootcampResponse();
                                dto.setId(bootcamp.getBootcampId());
                                dto.setNombre(bootcamp.getNombre());
                                dto.setDescripcion(bootcamp.getDescripcion());
                                dto.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
                                dto.setDuracion(bootcamp.getDuracion());
                                dto.setFechaFinalizacion(bootcamp.getFechaFinalizacion());
                                dto.setCapacidades(bootcamp.getCapacidades());
                                dto.setTecnologias(bootcamp.getTecnologias());
                                dto.setCantidadCapacidades(bootcamp.getCantidadCapacidades());
                                dto.setCantidadTecnologias(bootcamp.getCantidadTecnologias());
                                dto.setCantidadPersonasInscritas(bootcamp.getCantidadPersonasInscritas());
                                // Convertir PersonaInfo a BootcampResponse.PersonaInfo
                                List<PersonaInfo> personasResponse = personasInfo.stream()
                                    .map(p -> new PersonaInfo(p.getNombre(), p.getCorreo()))
                                    .collect(Collectors.toList());
                                dto.setPersonas(personasResponse);
                                return dto;
                            })
                            .doOnError(error -> log.error("Error al obtener datos de personas: {}", error.getMessage()));
                })
                .switchIfEmpty(Mono.error(new BootcampNotFoundException("No se encontraron bootcamps registrados")));
    }

  


   




}
