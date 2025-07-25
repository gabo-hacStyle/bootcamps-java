package gabs.personas.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("persona")
@Schema(description = "Users registered for the bootcamp system")
public class Persona {
    @Id
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String  nombre;
    @NotBlank
    @Size(max = 50)
    @Email
    private String  correo;
    @Min(0)
    @Max(100)
    private Integer edad;


}
