package gabs.bootcamps.infraestructure.adapter.in;

import gabs.bootcamps.application.port.BootcampUseCases;
import gabs.bootcamps.domain.exception.BootcampNotFoundException;
import gabs.bootcamps.domain.exception.BootcampValidationException;
import gabs.bootcamps.domain.exception.ExternalServiceException;
import gabs.bootcamps.domain.model.Bootcamp;
import gabs.bootcamps.dto.*;
import gabs.bootcamps.infraestructure.config.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampHandlerTest {

    @Mock
    private BootcampUseCases service;
    
    @Mock
    private GlobalExceptionHandler exceptionHandler;

    private BootcampHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BootcampHandler(service, exceptionHandler);
    }

    // ========== TESTS PARA getAll ==========

    @Test
    void getAll_Success() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps?page=0&size=10&sortBy=nombre&direction=asc");
        List<BootcampResponse> responses = Arrays.asList(
            createTestBootcampResponse(1L),
            createTestBootcampResponse(2L)
        );

        when(service.findAll(any(PageAndQuery.class))).thenReturn(Flux.fromIterable(responses));

        // Act & Assert
        StepVerifier.create(handler.getAll(request))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(service).findAll(any(PageAndQuery.class));
    }

    @Test
    void getAll_InvalidPageParameter() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps?page=invalid&size=10");
        
        when(exceptionHandler.handleIllegalArgumentException(any(IllegalArgumentException.class), eq(request)))
                .thenReturn(ServerResponse.badRequest().build());

        // Act & Assert
        StepVerifier.create(handler.getAll(request))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(service, never()).findAll(any());
    }

    @Test
    void getAll_ServiceException() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps?page=0&size=10");
        RuntimeException exception = new RuntimeException("Service error");
        
        when(service.findAll(any(PageAndQuery.class))).thenReturn(Flux.error(exception));
        when(exceptionHandler.handleGenericException(any(Throwable.class), eq(request)))
                .thenReturn(ServerResponse.status(500).build());

        // Act & Assert
        StepVerifier.create(handler.getAll(request))
                .expectNextMatches(response -> response.statusCode().value() == 500)
                .verifyComplete();

        verify(service).findAll(any(PageAndQuery.class));
    }

    // ========== TESTS PARA getById ==========

    @Test
    void getById_Success() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/1");
        BootcampResponse response = createTestBootcampResponse(1L);

        when(service.findById(1L)).thenReturn(Mono.just(response));

        // Act & Assert
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().value() == 200)
                .verifyComplete();

        verify(service).findById(1L);
    }

    @Test
    void getById_NotFound() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/999");
        BootcampNotFoundException exception = new BootcampNotFoundException(999L);
        
        when(service.findById(999L)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleBootcampNotFoundException(any(BootcampNotFoundException.class), eq(request)))
                .thenReturn(ServerResponse.status(404).build());

        // Act & Assert
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(response -> response.statusCode().value() == 404)
                .verifyComplete();

        verify(service).findById(999L);
    }

    @Test
    void getById_InvalidId() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/invalid");
        
        when(exceptionHandler.handleIllegalArgumentException(any(IllegalArgumentException.class), eq(request)))
                .thenReturn(ServerResponse.badRequest().build());

        // Act & Assert
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(service, never()).findById(any());
    }

    @Test
    void getById_ExternalServiceError() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/1");
        ExternalServiceException exception = ExternalServiceException.capacidadesServiceError("Service unavailable");
        
        when(service.findById(1L)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleExternalServiceException(any(ExternalServiceException.class), eq(request)))
                .thenReturn(ServerResponse.status(503).build());

        // Act & Assert
        StepVerifier.create(handler.getById(request))
                .expectNextMatches(response -> response.statusCode().value() == 503)
                .verifyComplete();

        verify(service).findById(1L);
    }

    // ========== TESTS PARA getSimpleBootcampResponseByIds ==========

    @Test
    void getSimpleBootcampResponseByIds_Success() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/simple?ids=1,2,3");
        List<BootcampSimpleResponse> responses = Arrays.asList(
            createTestBootcampSimpleResponse(1L),
            createTestBootcampSimpleResponse(2L),
            createTestBootcampSimpleResponse(3L)
        );

        when(service.findByIdSimpleResponse(Arrays.asList(1L, 2L, 3L))).thenReturn(Flux.fromIterable(responses));

        // Act & Assert
        StepVerifier.create(handler.getSimpleBootcampResponseByIds(request))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(service).findByIdSimpleResponse(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void getSimpleBootcampResponseByIds_InvalidIds() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/simple?ids=1,invalid,3");
        
        when(exceptionHandler.handleIllegalArgumentException(any(IllegalArgumentException.class), eq(request)))
                .thenReturn(ServerResponse.badRequest().build());

        // Act & Assert
        StepVerifier.create(handler.getSimpleBootcampResponseByIds(request))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(service, never()).findByIdSimpleResponse(any());
    }

    // ========== TESTS PARA save ==========

    @Test
    void save_Success() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps");
        BootcampRequest bootcampRequest = createValidBootcampRequest();
        Bootcamp savedBootcamp = createTestBootcamp(1L);

        when(request.bodyToMono(BootcampRequest.class)).thenReturn(Mono.just(bootcampRequest));
        when(service.register(bootcampRequest)).thenReturn(Mono.just(savedBootcamp));

        // Act & Assert
        StepVerifier.create(handler.save(request))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(service).register(bootcampRequest);
    }

    @Test
    void save_ValidationError() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps");
        BootcampRequest bootcampRequest = createValidBootcampRequest();
        BootcampValidationException exception = BootcampValidationException.invalidCapacidadesQuantity();

        when(request.bodyToMono(BootcampRequest.class)).thenReturn(Mono.just(bootcampRequest));
        when(service.register(bootcampRequest)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleBootcampValidationException(any(BootcampValidationException.class), eq(request)))
                .thenReturn(ServerResponse.status(400).build());

        // Act & Assert
        StepVerifier.create(handler.save(request))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(service).register(bootcampRequest);
    }

    @Test
    void save_ExternalServiceError() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps");
        BootcampRequest bootcampRequest = createValidBootcampRequest();
        ExternalServiceException exception = ExternalServiceException.capacidadesServiceError("Service error");

        when(request.bodyToMono(BootcampRequest.class)).thenReturn(Mono.just(bootcampRequest));
        when(service.register(bootcampRequest)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleExternalServiceException(any(ExternalServiceException.class), eq(request)))
                .thenReturn(ServerResponse.status(503).build());

        // Act & Assert
        StepVerifier.create(handler.save(request))
                .expectNextMatches(response -> response.statusCode().value() == 503)
                .verifyComplete();

        verify(service).register(bootcampRequest);
    }

    // ========== TESTS PARA delete ==========

    @Test
    void delete_Success() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/1");

        when(service.delete(1L)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.delete(request))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(service).delete(1L);
    }

    @Test
    void delete_NotFound() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/999");
        BootcampNotFoundException exception = new BootcampNotFoundException(999L);

        when(service.delete(999L)).thenReturn(Mono.error(exception));
        when(exceptionHandler.handleBootcampNotFoundException(any(BootcampNotFoundException.class), eq(request)))
                .thenReturn(ServerResponse.status(404).build());

        // Act & Assert
        StepVerifier.create(handler.delete(request))
                .expectNextMatches(response -> response.statusCode().value() == 404)
                .verifyComplete();

        verify(service).delete(999L);
    }

    @Test
    void delete_InvalidId() {
        // Arrange
        ServerRequest request = createMockRequest("/bootcamps/invalid");
        
        when(exceptionHandler.handleIllegalArgumentException(any(IllegalArgumentException.class), eq(request)))
                .thenReturn(ServerResponse.badRequest().build());

        // Act & Assert
        StepVerifier.create(handler.delete(request))
                .expectNextMatches(response -> response.statusCode().value() == 400)
                .verifyComplete();

        verify(service, never()).delete(any());
    }

    // ========== MÉTODOS AUXILIARES ==========

    private ServerRequest createMockRequest(String path) {
        ServerRequest request = mock(ServerRequest.class);
        
        // Simular path variables
        if (path.contains("/bootcamps/") && !path.contains("?")) {
            String id = path.substring(path.lastIndexOf("/") + 1);
            when(request.pathVariable("id")).thenReturn(id);
        }
        
        // Simular query parameters
        if (path.contains("?")) {
            String query = path.substring(path.indexOf("?") + 1);
            String[] params = query.split("&");
            
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    when(request.queryParam(keyValue[0])).thenReturn(java.util.Optional.of(keyValue[1]));
                }
            }
        }
        
        when(request.path()).thenReturn(path);
        return request;
    }

    private BootcampResponse createTestBootcampResponse(Long id) {
        BootcampResponse response = new BootcampResponse();
        response.setId(id);
        response.setNombre("Test Bootcamp " + id);
        response.setDescripcion("Test Description");
        response.setDuracion(30);
        response.setFechaLanzamiento(LocalDate.now());
        response.setFechaFinalizacion(LocalDate.now().plusDays(30));
        response.setCapacidades(Arrays.asList(
            new CapacidadDTO(1L, "Java", Arrays.asList(new TecnologiaDTO(1L, "Java")))
        ));
        return response;
    }

    private BootcampSimpleResponse createTestBootcampSimpleResponse(Long id) {
        BootcampSimpleResponse response = new BootcampSimpleResponse();
        response.setId(id);
        response.setNombre("Test Bootcamp " + id);
        response.setDuracion(30);
        response.setFechaLanzamiento(LocalDate.now());
        response.setFechaFinalizacion(LocalDate.now().plusDays(30));
        return response;
    }

    private BootcampRequest createValidBootcampRequest() {
        BootcampRequest request = new BootcampRequest();
        request.setNombre("Test Bootcamp");
        request.setDescripcion("Test Description");
        request.setDuracion(30);
        request.setFechaLanzamiento(LocalDate.now());
        request.setCapacidades(Arrays.asList(1L, 2L));
        return request;
    }

    private Bootcamp createTestBootcamp(Long id) {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(id);
        bootcamp.setNombre("Test Bootcamp");
        bootcamp.setDescripcion("Test Description");
        bootcamp.setDuracion(30);
        bootcamp.setFechaLanzamiento(LocalDate.now());
        bootcamp.setFechaFinalizacion(LocalDate.now().plusDays(30));
        return bootcamp;
    }
} 