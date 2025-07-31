package gabs.bootcamps.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "DTO que representa una capacidad con sus tecnologías asociadas", example = """
        {
          "id": 1,
          "nombre": "Desarrollo Backend",
          "tecnologias": [
            {
              "id": 1,
              "nombre": "Java"
            },
            {
              "id": 2,
              "nombre": "Spring Boot"
            }
          ]
        }
        """)
public class CapacidadDTO {
    
    @Schema(description = "Identificador único de la capacidad", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la capacidad", example = "Desarrollo Backend", required = true)
    private String nombre;
    
    @Schema(description = "Lista de tecnologías asociadas a la capacidad")
    private List<TecnologiaDTO> tecnologias;
}
