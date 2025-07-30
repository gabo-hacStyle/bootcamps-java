package gabs.reports.infraestructure.adapter.in;

import gabs.reports.application.service.BootcampService;
import gabs.reports.domain.model.Bootcamp;
import gabs.reports.dto.BootcampRequest;
import gabs.reports.dto.BootcampResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampHandlerTest {

    @Mock
    private BootcampService bootcampService;

    @Mock
    private GlobalExceptionHandler exceptionHandler;

    @InjectMocks
    private BootcampHandler bootcampHandler;

    private BootcampRequest validBootcampRequest;
    private Bootcamp savedBootcamp;
    private BootcampResponse bootcampResponse;

    @BeforeEach
    void setUp() {
        validBootcampRequest = new BootcampRequest();
        validBootcampRequest.setId(1L);
        validBootcampRequest.setNombre("Java Bootcamp");
        validBootcampRequest.setDescripcion("Bootcamp de Java");
        validBootcampRequest.setFechaLanzamiento(LocalDate.now());
        validBootcampRequest.setDuracion(12);
        validBootcampRequest.setFechaFinalizacion(LocalDate.now().plusMonths(3));

        savedBootcamp = new Bootcamp();
        savedBootcamp.setBootcampId(1L);
        savedBootcamp.setNombre("Java Bootcamp");
        savedBootcamp.setDescripcion("Bootcamp de Java");
        savedBootcamp.setFechaLanzamiento(LocalDate.now());
        savedBootcamp.setDuracion(12);
        savedBootcamp.setFechaFinalizacion(LocalDate.now().plusMonths(3));

        bootcampResponse = new BootcampResponse();
        bootcampResponse.setId(1L);
        bootcampResponse.setNombre("Java Bootcamp");
        bootcampResponse.setDescripcion("Bootcamp de Java");
        bootcampResponse.setCantidadPersonasInscritas(5);
    }

    @Test
    void registrarBootcamp_WithValidRequest_ShouldReturnOkResponse() {
        // Given
        ServerRequest mockRequest = createServerRequest(validBootcampRequest);
        when(mockRequest.bodyToMono(BootcampRequest.class)).thenReturn(Mono.just(validBootcampRequest));
        when(bootcampService.register(any(BootcampRequest.class))).thenReturn(Mono.just(savedBootcamp));

        // When & Then
        StepVerifier.create(bootcampHandler.registrarBootcamp(mockRequest))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(bootcampService).register(any(BootcampRequest.class));
    }

    @Test
    void bootcampConMasInscritos_WithValidData_ShouldReturnOkResponse() {
        // Given
        ServerRequest mockRequest = createEmptyServerRequest();
        when(bootcampService.findBootcampConMasInscritos()).thenReturn(Mono.just(bootcampResponse));

        // When & Then
        StepVerifier.create(bootcampHandler.bootcampConMasInscritos(mockRequest))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(bootcampService).findBootcampConMasInscritos();
    }

    private ServerRequest createServerRequest(Object body) {
        // Mock implementation for testing
        return mock(ServerRequest.class);
    }

    private ServerRequest createEmptyServerRequest() {
        // Mock implementation for testing
        return mock(ServerRequest.class);
    }
} 