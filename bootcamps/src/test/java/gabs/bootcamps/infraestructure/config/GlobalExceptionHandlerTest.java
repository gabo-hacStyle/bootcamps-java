package gabs.bootcamps.infraestructure.config;

import gabs.bootcamps.domain.exception.BootcampNotFoundException;
import gabs.bootcamps.domain.exception.BootcampValidationException;
import gabs.bootcamps.domain.exception.ExternalServiceException;
import gabs.bootcamps.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ServerRequest request;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.path()).thenReturn("/api/bootcamps/1");
    }

    @Test
    void handleBootcampNotFoundException_Success() {
        // Arrange
        BootcampNotFoundException exception = new BootcampNotFoundException(1L);

        // Act & Assert
        StepVerifier.create(exceptionHandler.handleBootcampNotFoundException(exception, request))
                .expectNextMatches(response -> 
                    response.statusCode().value() == HttpStatus.NOT_FOUND.value()
                )
                .verifyComplete();
    }

    @Test
    void handleBootcampValidationException_Success() {
        // Arrange
        BootcampValidationException exception = BootcampValidationException.invalidCapacidadesQuantity();

        // Act & Assert
        StepVerifier.create(exceptionHandler.handleBootcampValidationException(exception, request))
                .expectNextMatches(response -> 
                    response.statusCode().value() == HttpStatus.BAD_REQUEST.value()
                )
                .verifyComplete();
    }

    @Test
    void handleExternalServiceException_Success() {
        // Arrange
        ExternalServiceException exception = ExternalServiceException.capacidadesServiceError("Service unavailable");

        // Act & Assert
        StepVerifier.create(exceptionHandler.handleExternalServiceException(exception, request))
                .expectNextMatches(response -> 
                    response.statusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()
                )
                .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_Success() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid parameter");

        // Act & Assert
        StepVerifier.create(exceptionHandler.handleIllegalArgumentException(exception, request))
                .expectNextMatches(response -> 
                    response.statusCode().value() == HttpStatus.BAD_REQUEST.value()
                )
                .verifyComplete();
    }

    @Test
    void handleGenericException_Success() {
        // Arrange
        RuntimeException exception = new RuntimeException("Unexpected error");

        // Act & Assert
        StepVerifier.create(exceptionHandler.handleGenericException(exception, request))
                .expectNextMatches(response -> 
                    response.statusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value()
                )
                .verifyComplete();
    }

    @Test
    void handleBootcampException_Success() {
        // Arrange
        BootcampValidationException exception = BootcampValidationException.duplicateCapacidades();

        // Act & Assert
        StepVerifier.create(exceptionHandler.handleBootcampException(exception, request))
                .expectNextMatches(response -> 
                    response.statusCode().value() == HttpStatus.BAD_REQUEST.value()
                )
                .verifyComplete();
    }
} 