package gabs.bootcamps.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    @Schema(description = "Código de error", example = "BOOTCAMP_NOT_FOUND")
    private String errorCode;
    
    @Schema(description = "Mensaje descriptivo del error", example = "Bootcamp con ID 1 no encontrado")
    private String message;
    
    @Schema(description = "Ruta de la petición que generó el error", example = "/api/bootcamps/1")
    private String path;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp del error", example = "2024-01-15 10:30:45")
    private LocalDateTime timestamp;
    
    public ErrorResponse(String errorCode, String message, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
} 