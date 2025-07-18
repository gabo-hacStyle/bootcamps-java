package gabs.tecnologias.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("capacidad")
@Schema(description = "Skills for every technology applied in the bootcamp of the system")
public class Capacidad {
    @Id
    private Long id;



    @NotBlank
    @Size(max = 50)
    private String nombre;

    @NotBlank
    @Size(max = 90)
    private String descripcion;


}
