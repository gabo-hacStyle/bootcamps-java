package gabs.personas.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("persona")
@Schema(description = "Entidad que representa una persona en el sistema de bootcamps",
        example = """
        {
          "id": 1,
          "nombre": "Carlos Rodríguez",
          "correo": "carlos.rodriguez@email.com",
          "edad": 25
        }
        """)
public class Persona {
    @Id
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String nombre;
    
    @NotBlank
    @Size(max = 50)
    @Email
    private String correo;
    
    @Min(0)
    @Max(100)
    private Integer edad;
}
