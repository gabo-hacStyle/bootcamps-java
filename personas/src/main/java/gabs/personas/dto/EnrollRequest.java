package gabs.personas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de inscripción de persona en bootcamp",
        example = """
        {
          "bootcampId": 5,
          "personaId": 1
        }
        """)
public class EnrollRequest {

    public Long bootcampId;
    public Long personaId;

}
