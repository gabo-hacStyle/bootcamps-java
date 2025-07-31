package gabs.tecnologias.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta de error estandarizada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de error", example = """
        {
          "message": "Tecnología con ID 999 no encontrada",
          "error": "Not Found",
          "status": 404,
          "timestamp": "2024-01-15 10:30:45",
          "path": "/api/tecnologias/999"
        }
        """)
public class ErrorResponse {

    @Schema(description = "Mensaje de error descriptivo", example = "Tecnología con ID 999 no encontrada")
    private String message;

    @Schema(description = "Tipo de error", example = "Not Found")
    private String error;

    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp del error", example = "2024-01-15 10:30:45")
    private LocalDateTime timestamp;

    @Schema(description = "Ruta de la petición", example = "/api/tecnologias/999")
    private String path;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "Errores de validación de campos")
    private List<FieldError> fieldErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String error, int status, String path) {
        this();
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
    }

    /**
     * DTO para errores de validación de campos específicos
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error de validación de campo", example = """
            {
              "field": "nombre",
              "message": "El nombre de la tecnología es obligatorio",
              "rejectedValue": null
            }
            """)
    public static class FieldError {
        
        @Schema(description = "Nombre del campo con error", example = "nombre")
        private String field;
        
        @Schema(description = "Mensaje de error del campo", example = "El nombre de la tecnología es obligatorio")
        private String message;
        
        @Schema(description = "Valor rechazado", example = "null")
        private Object rejectedValue;
    }
}
