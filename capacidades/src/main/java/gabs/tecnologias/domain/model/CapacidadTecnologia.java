package gabs.tecnologias.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="capacidad_tecnologia")
public class CapacidadTecnologia {
    @Id
    private Long id;

    private Long tecnologiaId;
    private Long capacidadId;
}
