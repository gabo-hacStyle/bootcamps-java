package gabs.personas.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollRequest {

    public Long bootcampId;
    public Long personaId;

}
