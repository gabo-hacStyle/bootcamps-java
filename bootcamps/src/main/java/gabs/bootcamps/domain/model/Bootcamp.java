package gabs.bootcamps.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@Data
@Table("bootcamp")
@Schema(description = "Main bootcamp model for the system")
public class Bootcamp {
    @Id
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String nombre;
    @NotBlank
    @Size(max = 90)
    private String descripcion;

    private LocalDate fechaLanzamiento;
    private Integer duracion;

    @Column("fecha_finalizacion")
    private LocalDate fechaFinalizacion;
}
