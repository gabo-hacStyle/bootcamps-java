package gabs.tecnologias.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("tecnologias")
@Schema(description = "Technologies of the system")
public class Tecnologia {
    @Id
    private Long id;



    @NotBlank
    @Size(max = 50)
    private String nombre;

    @NotBlank
    @Size(max = 90)
    private String descripcion;

}
