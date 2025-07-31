package gabs.personas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Información de tecnología",
        example = """
        {
          "id": 1,
          "nombre": "Java"
        }
        """)
public class Tecnologias {
    private Long id;
    private String nombre;
}