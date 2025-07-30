package gabs.capacidades.infraestructure.adapter.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.application.port.CapacidadUseCases;
import gabs.capacidades.domain.exception.CapacidadNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.domain.model.Capacidad;
import gabs.capacidades.dto.CapacidadRequest;
import gabs.capacidades.dto.CapacidadResponse;
import gabs.capacidades.dto.PageAndQuery;
import gabs.capacidades.dto.Tecnologias;
import gabs.capacidades.infraestructure.adapter.in.CapacidadHandler;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacidadHandlerTest {

    @Mock
    private CapacidadUseCases service;

    @Mock
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private ServerRequest request;

    private CapacidadHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CapacidadHandler(service, exceptionHandler);
    }

    // ========== TESTS PARA getAll ==========

    @Test
    void getAll_WhenValidRequest_ShouldReturnCapacidades() {
        // Given
        PageAndQuery consult = new PageAndQuery(0, 10, "nombre", "asc");
        
        CapacidadResponse response1 = new CapacidadResponse();
        response1.setId(1L);
        response1.setNombre("Capacidad 1");
        response1.setTecnologiasList(List.of(new Tecnologias(1L, "Java")));

        CapacidadResponse response2 = new CapacidadResponse();
        response2.setId(2L);
        response2.setNombre("Capacidad 2");
        response2.setTecnologiasList(List.of(new Tecnologias(2L, "Python")));

        when(request.queryParam("page")).thenReturn(java.util.Optional.of("0"));
        when(request.queryParam("size")).thenReturn(java.util.Optional.of("10"));
        when(request.queryParam("sortBy")).thenReturn(java.util.Optional.of("nombre"));
        when(request.queryParam("direction")).thenReturn(java.util.Optional.of("asc"));
        when(service.findAll(any(PageAndQuery.class))).thenReturn(Flux.just(response1, response2));

        // When & Then
        StepVerifier.create(handler.getAll(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }

    @Test
    void getAll_WhenDefaultParameters_ShouldUseDefaults() {
        // Given
        CapacidadResponse response = new CapacidadResponse();
        response.setId(1L);
        response.setNombre("Test");

        when(request.queryParam("page")).thenReturn(java.util.Optional.empty());
        when(request.queryParam("size")).thenReturn(java.util.Optional.empty());
        when(request.queryParam("sortBy")).thenReturn(java.util.Optional.empty());
        when(request.queryParam("direction")).thenReturn(java.util.Optional.empty());
        when(service.findAll(any(PageAndQuery.class))).thenReturn(Flux.just(response));

        // When & Then
        StepVerifier.create(handler.getAll(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }

    // ========== TESTS PARA getById ==========

    @Test
    void getById_WhenCapacidadExists_ShouldReturnCapacidad() {
        // Given
        Long id = 1L;
        CapacidadResponse expectedResponse = new CapacidadResponse();
        expectedResponse.setId(id);
        expectedResponse.setNombre("Test Capacidad");
        expectedResponse.setTecnologiasList(List.of(new Tecnologias(1L, "Java")));

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.findById(id)).thenReturn(Mono.just(expectedResponse));

        // When & Then
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }

    @Test
    void getById_WhenCapacidadNotFound_ShouldHandleException() {
        // Given
        Long id = 999L;
        CapacidadNotFoundException exception = new CapacidadNotFoundException(id);
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.NOT_FOUND).build();

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.findById(id)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleCapacidadNotFound(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NOT_FOUND.value())
                .verifyComplete();
    }

    @Test
    void getById_WhenGenericError_ShouldHandleException() {
        // Given
        Long id = 1L;
        RuntimeException exception = new RuntimeException("Error interno");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.findById(id)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleGenericException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value())
                .verifyComplete();
    }

    // ========== TESTS PARA existsById ==========

    @Test
    void existsById_WhenCapacidadExists_ShouldReturnTrue() {
        // Given
        Long id = 1L;

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.existsById(id)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(handler.existsById(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }

    @Test
    void existsById_WhenCapacidadNotExists_ShouldReturnFalse() {
        // Given
        Long id = 999L;

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.existsById(id)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(handler.existsById(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }

    // ========== TESTS PARA save ==========

    @Test
    void save_WhenValidRequest_ShouldReturnCreated() {
        // Given
        CapacidadRequest requestBody = new CapacidadRequest();
        requestBody.setNombre("Test");
        requestBody.setDescripcion("Test Description");
        requestBody.setTecnologias(List.of(1L, 2L, 3L));

        Capacidad savedCapacidad = new Capacidad();
        savedCapacidad.setId(1L);
        savedCapacidad.setNombre("Test");

        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.register(requestBody)).thenReturn(Mono.just(savedCapacidad));

        // When & Then
        StepVerifier.create(handler.save(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.CREATED.value())
                .verifyComplete();
    }

    @Test
    void save_WhenValidationError_ShouldHandleException() {
        // Given
        CapacidadRequest requestBody = new CapacidadRequest();
        requestBody.setNombre("Test");
        requestBody.setTecnologias(List.of(1L, 1L, 2L)); // Duplicados

        ValidationException exception = new ValidationException("No se permiten tecnologías repetidas");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.BAD_REQUEST).build();

        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.register(requestBody)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleValidationException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.save(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.BAD_REQUEST.value())
                .verifyComplete();
    }

    @Test
    void save_WhenIllegalArgumentException_ShouldHandleException() {
        // Given
        CapacidadRequest requestBody = new CapacidadRequest();
        requestBody.setNombre("Test");

        IllegalArgumentException exception = new IllegalArgumentException("Argumento ilegal");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.BAD_REQUEST).build();

        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.register(requestBody)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleIllegalArgumentException(exception, request))
                    .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.save(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.BAD_REQUEST.value())
                .verifyComplete();
    }

    @Test
    void save_WhenGenericError_ShouldHandleException() {
        // Given
        CapacidadRequest requestBody = new CapacidadRequest();
        requestBody.setNombre("Test");

        RuntimeException exception = new RuntimeException("Error interno");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.register(requestBody)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleGenericException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.save(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value())
                .verifyComplete();
    }

    // ========== TESTS PARA update ==========

    @Test
    void update_WhenCapacidadExists_ShouldReturnUpdated() {
        // Given
        Long id = 1L;
        CapacidadRequest requestBody = new CapacidadRequest();
        requestBody.setNombre("Updated Test");

        Capacidad updatedCapacidad = new Capacidad();
        updatedCapacidad.setId(id);
        updatedCapacidad.setNombre("Updated Test");

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.updateParcial(id, requestBody)).thenReturn(Mono.just(updatedCapacidad));

        // When & Then
        StepVerifier.create(handler.update(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }


    @Test
    void update_WhenCapacidadNotFound_ShouldHandleException() {
        // Given
        Long id = 999L;
        CapacidadRequest requestBody = new CapacidadRequest();
        CapacidadNotFoundException exception = new CapacidadNotFoundException(id);
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.NOT_FOUND).build();

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.updateParcial(id, requestBody)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleCapacidadNotFound(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.update(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NOT_FOUND.value())
                .verifyComplete();
    }

    @Test
    void update_WhenValidationError_ShouldHandleException() {
        // Given
        Long id = 1L;
        CapacidadRequest requestBody = new CapacidadRequest();
        ValidationException exception = new ValidationException("Error de validación");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.BAD_REQUEST).build();

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(request.bodyToMono(CapacidadRequest.class)).thenReturn(Mono.just(requestBody));
        when(service.updateParcial(id, requestBody)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleValidationException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.update(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.BAD_REQUEST.value())
                .verifyComplete();
    }

    // ========== TESTS PARA delete ==========

    @Test
    void delete_WhenCapacidadExists_ShouldReturnNoContent() {
        // Given
        Long id = 1L;

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.delete(id)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(handler.delete(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NO_CONTENT.value())
                .verifyComplete();
    }

    @Test
    void delete_WhenCapacidadNotFound_ShouldHandleException() {
        // Given
        Long id = 999L;
        CapacidadNotFoundException exception = new CapacidadNotFoundException(id);
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.NOT_FOUND).build();

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.delete(id)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleCapacidadNotFound(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.delete(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NOT_FOUND.value())
                .verifyComplete();
    }

    @Test
    void delete_WhenGenericError_ShouldHandleException() {
        // Given
        Long id = 1L;
        RuntimeException exception = new RuntimeException("Error interno");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(request.pathVariable("id")).thenReturn(id.toString());
        when(service.delete(id)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleGenericException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.delete(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value())
                .verifyComplete();
    }
} 