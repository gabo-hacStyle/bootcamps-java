package gabs.personas.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaRegisteredResponse {

    private String nombrePersona;
    private Long personaId;
    private Long bootcampId;
    private String nombreBootcamp;
    private String correoPersona;

}
