package gabs.personas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de reporte de persona",
        example = """
        {
          "personaId": 1,
          "nombre": "María González",
          "correo": "maria.gonzalez@email.com",
          "edad": 28
        }
        """)
public class PersonaReportResponse {
    private Long personaId;
    private String nombre;
    private String correo;
    private Integer edad;
}
