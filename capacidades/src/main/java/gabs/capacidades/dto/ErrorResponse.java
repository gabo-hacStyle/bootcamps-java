package gabs.capacidades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para respuestas de error estandarizadas")
public class ErrorResponse {
    
    @Schema(description = "Timestamp del error", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;
    
    @Schema(description = "Código de error específico", example = "CAPACIDAD_NOT_FOUND")
    private String error;
    
    @Schema(description = "Mensaje descriptivo del error", example = "Capacidad con ID 999 no encontrada")
    private String message;
    
    @Schema(description = "Ruta de la petición que causó el error", example = "/api/capacidades/999")
    private String path;

    public ErrorResponse(String error, String message, int status) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }
}

