package gabs.personas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de registro de persona en bootcamp", 
        example = """
        {
          "nombrePersona": "Juan Pérez",
          "personaId": 1,
          "bootcampId": 5,
          "nombreBootcamp": "Java Full Stack",
          "correoPersona": "juan.perez@email.com"
        }
        """)
public class PersonaRegisteredResponse {

    private String nombrePersona;
    private Long personaId;
    private Long bootcampId;
    private String nombreBootcamp;
    private String correoPersona;

}
