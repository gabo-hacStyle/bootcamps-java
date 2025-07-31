package gabs.bootcamps.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
@Schema(description = "DTO de respuesta simplificado para un bootcamp", example = """
        {
          "id": 1,
          "nombre": "Bootcamp de Desarrollo Full Stack",
          "fechaLanzamiento": "2024-01-15",
          "duracion": 12,
          "fechaFinalizacion": "2024-04-15"
        }
        """)
public class BootcampSimpleResponse {
    
    @Schema(description = "Identificador único del bootcamp", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del bootcamp", example = "Bootcamp de Desarrollo Full Stack")
    private String nombre;
    
    @Schema(description = "Fecha de lanzamiento del bootcamp", example = "2024-01-15")
    private LocalDate fechaLanzamiento;
    
    @Schema(description = "Duración del bootcamp en semanas", example = "12")
    private Integer duracion;
    
    @Schema(description = "Fecha de finalización calculada del bootcamp", example = "2024-04-15")
    private LocalDate fechaFinalizacion;

}
