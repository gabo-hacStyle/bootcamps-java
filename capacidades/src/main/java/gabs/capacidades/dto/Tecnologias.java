package gabs.capacidades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO para representar una tecnología", example = """
        {
            "id": 1,
            "nombre": "Java"
        }
        """)
public class Tecnologias {
    
    @Schema(description = "ID único de la tecnología", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la tecnología", example = "Java")
    private String nombre;
}