package gabs.personas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error estandarizada",
        example = """
        {
          "message": "La persona con ID 999 no fue encontrada",
          "error": "PersonaNotFoundException",
          "status": 404,
          "timestamp": "2024-01-15 14:30:25",
          "path": "/api/personas/999"
        }
        """)
public class ErrorResponse {
    
    private String message;
    private String error;
    private int status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    
    public ErrorResponse(String message, String error, int status, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
} 