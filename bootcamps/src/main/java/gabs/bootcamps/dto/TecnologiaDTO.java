package gabs.bootcamps.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO que representa una tecnología", example = """
        {
          "id": 1,
          "nombre": "Java"
        }
        """)
public class TecnologiaDTO {
    
    @Schema(description = "Identificador único de la tecnología", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la tecnología", example = "Java", required = true)
    private String nombre;

}