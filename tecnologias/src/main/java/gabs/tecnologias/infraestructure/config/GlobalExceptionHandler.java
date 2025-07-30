package gabs.tecnologias.infraestructure.config;

import gabs.tecnologias.domain.exception.CapacidadTecnologiaNotFoundException;
import gabs.tecnologias.domain.exception.TecnologiaNotFoundException;
import gabs.tecnologias.domain.exception.ValidationException;
import gabs.tecnologias.dto.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para WebFlux
 */
@Component
@Order(-2) // Prioridad alta para que se ejecute antes que otros manejadores
public class GlobalExceptionHandler implements WebExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ErrorResponse errorResponse;
        HttpStatus status;
        
        logger.error("Error en la aplicación: {}", ex.getMessage(), ex);
        
        if (ex instanceof TecnologiaNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorResponse = new ErrorResponse(
                ex.getMessage(),
                "TECNOLOGIA_NOT_FOUND",
                status.value(),
                exchange.getRequest().getPath().value()
            );
        } else if (ex instanceof CapacidadTecnologiaNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorResponse = new ErrorResponse(
                ex.getMessage(),
                "CAPACIDAD_TECNOLOGIA_NOT_FOUND",
                status.value(),
                exchange.getRequest().getPath().value()
            );
        } else if (ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = new ErrorResponse(
                ex.getMessage(),
                "VALIDATION_ERROR",
                status.value(),
                exchange.getRequest().getPath().value()
            );
        }  else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = new ErrorResponse(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                status.value(),
                exchange.getRequest().getPath().value()
            );
        } else {
            // Error interno del servidor para excepciones no manejadas
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = new ErrorResponse(
                "Error interno del servidor",
                "INTERNAL_SERVER_ERROR",
                status.value(),
                exchange.getRequest().getPath().value()
            );
        }
        
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(
                serializeErrorResponse(errorResponse)
            ))
        );
    }
    
  
    
    private byte[] serializeErrorResponse(ErrorResponse errorResponse) {
        try {
            // Usar Jackson para serializar la respuesta de error
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            logger.error("Error serializando respuesta de error", e);
            return "{\"error\":\"Error serializando respuesta\"}".getBytes();
        }
    }
} 