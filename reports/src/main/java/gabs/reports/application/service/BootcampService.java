package gabs.reports.application.service;

import gabs.reports.application.port.BootcampUseCases;
import gabs.reports.domain.exception.BootcampNotFoundException;
import gabs.reports.domain.exception.ValidationException;
import gabs.reports.domain.model.Bootcamp;
import gabs.reports.dto.*;
import gabs.reports.domain.event.PersonasDataRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import gabs.reports.domain.port.BootcampRepositoryPort;

@Service
@Slf4j
public class BootcampService implements BootcampUseCases {

    private final BootcampRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    public BootcampService(BootcampRepositoryPort repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Mono<BootcampResponse> findById(Long id) {
        validateBootcampId(id);
        
        return repository.findByBootcampId(id)
                .flatMap(this::buildBootcampResponseWithPersonas)
                .switchIfEmpty(Mono.error(new BootcampNotFoundException(id)));
    }

    @Override
    public Mono<Bootcamp> register(BootcampRequest request) {
        validateBootcampRequest(request);
        
        log.info("Registrando bootcamp: {}", request.getNombre());
        
        Bootcamp bootcamp = createBootcampFromRequest(request);
        
        return repository.save(bootcamp)
                .doOnSuccess(saved -> log.info("Bootcamp registrado exitosamente: {}", saved.getNombre()))
                .doOnError(error -> log.error("Error al registrar bootcamp: {}", error.getMessage()));
    }

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
                    
                    return buildBootcampResponseWithPersonas(bootcamp)
                            .doOnError(error -> log.error("Error al obtener datos de personas: {}", error.getMessage()));
                })
                .switchIfEmpty(Mono.error(new BootcampNotFoundException("No se encontraron bootcamps registrados")));
    }

    // ========== PRIVATE METHODS - SINGLE RESPONSIBILITY ==========

    /**
     * Valida que el ID del bootcamp no sea nulo
     */
    private void validateBootcampId(Long id) {
        if (id == null) {
            throw new ValidationException("ID del bootcamp no puede ser nulo");
        }
    }

    /**
     * Valida todos los campos del request de bootcamp
     */
    private void validateBootcampRequest(BootcampRequest request) {
        if (request == null) {
            throw new ValidationException("Request no puede ser nulo");
        }
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new ValidationException("nombre", "No puede estar vacío");
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new ValidationException("descripcion", "No puede estar vacío");
        }
        if (request.getFechaLanzamiento() == null) {
            throw new ValidationException("fechaLanzamiento", "No puede ser nulo");
        }
        if (request.getDuracion() == null || request.getDuracion() <= 0) {
            throw new ValidationException("duracion", "Debe ser mayor a 0");
        }
    }

    /**
     * Crea un objeto Bootcamp a partir del request
     */
    private Bootcamp createBootcampFromRequest(BootcampRequest request) {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setNombre(request.getNombre());
        bootcamp.setDescripcion(request.getDescripcion());
        bootcamp.setFechaLanzamiento(request.getFechaLanzamiento());
        bootcamp.setDuracion(request.getDuracion());
        bootcamp.setFechaFinalizacion(request.getFechaFinalizacion());
        bootcamp.setBootcampId(request.getId());
        
        // Extraer capacidades y tecnologías
        List<String> capacidades = extractCapacidadesFromRequest(request);
        List<String> tecnologias = extractTecnologiasFromRequest(request);
        
        bootcamp.setCapacidades(capacidades);
        bootcamp.setTecnologias(tecnologias);
        bootcamp.setCantidadCapacidades(capacidades.size());
        bootcamp.setCantidadTecnologias(tecnologias.size());
        bootcamp.setCantidadPersonasInscritas(0);
        bootcamp.setPersonasInscritas(new ArrayList<>());
        
        return bootcamp;
    }

    /**
     * Extrae las capacidades del request
     */
    private List<String> extractCapacidadesFromRequest(BootcampRequest request) {
        if (request.getCapacidades() == null) {
            return new ArrayList<>();
        }
        return request.getCapacidades().stream()
                .map(CapacidadDTO::getNombre)
                .toList();
    }

    /**
     * Extrae las tecnologías del request
     */
    private List<String> extractTecnologiasFromRequest(BootcampRequest request) {
        if (request.getCapacidades() == null) {
            return new ArrayList<>();
        }
        return request.getCapacidades().stream()
                .flatMap(cap -> cap.getTecnologias() != null ? 
                        cap.getTecnologias().stream().map(TecnologiaDTO::getNombre) : 
                        java.util.stream.Stream.empty())
                .toList();
    }

    /**
     * Construye un BootcampResponse a partir de un Bootcamp
     */
    private Mono<BootcampResponse> buildBootcampResponseWithPersonas(Bootcamp bootcamp) {
        BootcampResponse response = buildBasicBootcampResponse(bootcamp);
        
        List<Long> personasIds = bootcamp.getPersonasInscritas();
        
        if (personasIds == null || personasIds.isEmpty()) {
            response.setPersonas(new ArrayList<>());
            return Mono.just(response);
        }

        return fetchPersonasData(personasIds)
                .map(personasInfos -> {
                    response.setPersonas(personasInfos);
                    return response;
                });
    }

    /**
     * Construye la respuesta básica del bootcamp sin datos de personas
     */
    private BootcampResponse buildBasicBootcampResponse(Bootcamp bootcamp) {
        BootcampResponse response = new BootcampResponse();
        response.setId(bootcamp.getBootcampId());
        response.setNombre(bootcamp.getNombre());
        response.setDescripcion(bootcamp.getDescripcion());
        response.setDuracion(bootcamp.getDuracion());
        response.setFechaLanzamiento(bootcamp.getFechaLanzamiento());
        response.setFechaFinalizacion(bootcamp.getFechaFinalizacion());
        response.setCapacidades(bootcamp.getCapacidades());
        response.setTecnologias(bootcamp.getTecnologias());
        response.setCantidadCapacidades(bootcamp.getCantidadCapacidades());
        response.setCantidadTecnologias(bootcamp.getCantidadTecnologias());
        response.setCantidadPersonasInscritas(bootcamp.getCantidadPersonasInscritas());
        return response;
    }

    /**
     * Obtiene los datos de las personas usando eventos de dominio
     */
    private Mono<List<PersonaInfo>> fetchPersonasData(List<Long> personasIds) {
        CompletableFuture<List<PersonaInfo>> future = new CompletableFuture<>();
        PersonasDataRequestEvent event = new PersonasDataRequestEvent(personasIds, future);
        eventPublisher.publishEvent(event);
        
        return Mono.fromFuture(future);
    }
}
