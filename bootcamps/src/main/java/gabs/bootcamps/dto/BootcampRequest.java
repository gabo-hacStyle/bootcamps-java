package gabs.bootcamps.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para crear un nuevo bootcamp", example = """
        {
          "nombre": "Bootcamp de Desarrollo Full Stack",
          "descripcion": "Programa intensivo para aprender desarrollo web completo",
          "capacidades": [1, 2, 3],
          "duracion": 12,
          "fechaLanzamiento": "2024-01-15"
        }
        """)
public class BootcampRequest {

    @Schema(description = "Nombre del bootcamp", example = "Bootcamp de Desarrollo Full Stack", required = true)
    private String nombre;
    
    @Schema(description = "Descripción detallada del bootcamp", example = "Programa intensivo para aprender desarrollo web completo", required = true)
    private String descripcion;
    
    @Schema(description = "Lista de IDs de capacidades asociadas al bootcamp", example = "[1, 2, 3]")
    private List<Long> capacidades;
    
    @Schema(description = "Duración del bootcamp en semanas", example = "12", required = true)
    private Integer duracion;
    
    @Schema(description = "Fecha de lanzamiento del bootcamp", example = "2024-01-15", required = true)
    private LocalDate fechaLanzamiento;

}
