package gabs.capacidades.infraestructure.adapter.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import gabs.capacidades.application.port.CapacidadBootcampUseCases;
import gabs.capacidades.domain.exception.BootcampNotFoundException;
import gabs.capacidades.domain.exception.ValidationException;
import gabs.capacidades.dto.CapacidadBootcampResponse;
import gabs.capacidades.dto.Tecnologias;
import gabs.capacidades.infraestructure.adapter.in.CapacidadBootcampHandler;
import gabs.capacidades.infraestructure.config.GlobalExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacidadBootcampHandlerTest {

    @Mock
    private CapacidadBootcampUseCases service;

    @Mock
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private ServerRequest request;

    private CapacidadBootcampHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CapacidadBootcampHandler(service, exceptionHandler);
    }

    // ========== TESTS PARA getCapacidadesByBootcamp ==========

    @Test
    void getCapacidadesByBootcamp_WhenBootcampExists_ShouldReturnCapacidades() {
        // Given
        Long bootcampId = 1L;
        CapacidadBootcampResponse response = new CapacidadBootcampResponse();
        response.setId(1L);
        response.setNombre("Test Capacidad");
        response.setTecnologias(List.of(new Tecnologias(1L, "Java")));

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.getAllByBootcamp(bootcampId)).thenReturn(Flux.just(response));

        // When & Then
        StepVerifier.create(handler.getCapacidadesByBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }

    @Test
    void getCapacidadesByBootcamp_WhenMultipleCapacidades_ShouldReturnAll() {
        // Given
        Long bootcampId = 1L;
        
        CapacidadBootcampResponse response1 = new CapacidadBootcampResponse();
        response1.setId(1L);
        response1.setNombre("Capacidad 1");
        response1.setTecnologias(List.of(new Tecnologias(1L, "Java")));

        CapacidadBootcampResponse response2 = new CapacidadBootcampResponse();
        response2.setId(2L);
        response2.setNombre("Capacidad 2");
        response2.setTecnologias(List.of(new Tecnologias(2L, "Python")));

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.getAllByBootcamp(bootcampId)).thenReturn(Flux.just(response1, response2));

        // When & Then
        StepVerifier.create(handler.getCapacidadesByBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.OK.value())
                .verifyComplete();
    }


    @Test
    void getCapacidadesByBootcamp_WhenBootcampNotFound_ShouldHandleException() {
        // Given
        Long bootcampId = 999L;
        BootcampNotFoundException exception = new BootcampNotFoundException(bootcampId);
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.NOT_FOUND).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.getAllByBootcamp(bootcampId)).thenReturn(Flux.error(exception));
        when(exceptionHandler.handleBootcampNotFound(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.getCapacidadesByBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NOT_FOUND.value())
                .verifyComplete();
    }

    @Test
    void getCapacidadesByBootcamp_WhenGenericError_ShouldHandleException() {
        // Given
        Long bootcampId = 1L;
        RuntimeException exception = new RuntimeException("Error interno");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.getAllByBootcamp(bootcampId)).thenReturn(Flux.error(exception));
        when(exceptionHandler.handleGenericException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.getCapacidadesByBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value())
                .verifyComplete();
    }

    // ========== TESTS PARA saveCapacidadBootcamp ==========

    @Test
    void saveCapacidadBootcamp_WhenValidRequest_ShouldReturnCreated() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(1L, 2L, 3L);

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {}))
                .thenReturn(Mono.just(capacidadesIds));
        when(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(handler.saveCapacidadBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.CREATED.value())
                .verifyComplete();
    }

    @Test
    void saveCapacidadBootcamp_WhenEmptyList_ShouldReturnCreated() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {}))
                .thenReturn(Mono.just(capacidadesIds));
        when(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(handler.saveCapacidadBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.CREATED.value())
                .verifyComplete();
    }

    @Test
    void saveCapacidadBootcamp_WhenValidationError_ShouldHandleException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(999L); // Capacidad inexistente
        ValidationException exception = new ValidationException("Capacidad no encontrada");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.BAD_REQUEST).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {}))
                .thenReturn(Mono.just(capacidadesIds));
        when(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .thenReturn(Flux.error(exception));
        when(exceptionHandler.handleValidationException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.saveCapacidadBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.BAD_REQUEST.value())
                .verifyComplete();
    }

    @Test
    void saveCapacidadBootcamp_WhenBootcampNotFound_ShouldHandleException() {
        // Given
        Long bootcampId = 999L;
        List<Long> capacidadesIds = List.of(1L);
        BootcampNotFoundException exception = new BootcampNotFoundException(bootcampId);
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.NOT_FOUND).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {}))
                .thenReturn(Mono.just(capacidadesIds));
        when(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .thenReturn(Flux.error(exception));
        when(exceptionHandler.handleBootcampNotFound(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.saveCapacidadBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NOT_FOUND.value())
                .verifyComplete();
    }

    @Test
    void saveCapacidadBootcamp_WhenIllegalArgumentException_ShouldHandleException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(1L);
        IllegalArgumentException exception = new IllegalArgumentException("Argumento ilegal");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.BAD_REQUEST).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {}))
                .thenReturn(Mono.just(capacidadesIds));
        when(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .thenReturn(Flux.error(exception));
        when(exceptionHandler.handleIllegalArgumentException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.saveCapacidadBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.BAD_REQUEST.value())
                .verifyComplete();
    }

    @Test
    void saveCapacidadBootcamp_WhenGenericError_ShouldHandleException() {
        // Given
        Long bootcampId = 1L;
        List<Long> capacidadesIds = List.of(1L);
        RuntimeException exception = new RuntimeException("Error interno");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(request.bodyToMono(new ParameterizedTypeReference<List<Long>>() {}))
                .thenReturn(Mono.just(capacidadesIds));
        when(service.saveCapacidadBootcamp(bootcampId, capacidadesIds))
                .thenReturn(Flux.error(exception));
        when(exceptionHandler.handleGenericException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.saveCapacidadBootcamp(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value())
                .verifyComplete();
    }

    // ========== TESTS PARA deleteCapacidadesByBootcampDeleted ==========

    @Test
    void deleteCapacidadesByBootcampDeleted_WhenBootcampExists_ShouldReturnNoContent() {
        // Given
        Long bootcampId = 1L;

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.deleteCapacidadesByBootcampId(bootcampId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(handler.deleteCapacidadesByBootcampDeleted(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NO_CONTENT.value())
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByBootcampDeleted_WhenBootcampNotFound_ShouldHandleException() {
        // Given
        Long bootcampId = 999L;
        BootcampNotFoundException exception = new BootcampNotFoundException(bootcampId);
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.NOT_FOUND).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.deleteCapacidadesByBootcampId(bootcampId)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleBootcampNotFound(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.deleteCapacidadesByBootcampDeleted(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.NOT_FOUND.value())
                .verifyComplete();
    }

    @Test
    void deleteCapacidadesByBootcampDeleted_WhenGenericError_ShouldHandleException() {
        // Given
        Long bootcampId = 1L;
        RuntimeException exception = new RuntimeException("Error interno");
        Mono<ServerResponse> errorResponse = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(request.pathVariable("id")).thenReturn(bootcampId.toString());
        when(service.deleteCapacidadesByBootcampId(bootcampId)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleGenericException(exception, request))
                .thenReturn(errorResponse);

        // When & Then
        StepVerifier.create(handler.deleteCapacidadesByBootcampDeleted(request))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value())
                .verifyComplete();
    }
} 