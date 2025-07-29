package gabs.personas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaReportResponse {
    private Long personaId;
    private String nombre;
    private String correo;
    private Integer edad;
}
