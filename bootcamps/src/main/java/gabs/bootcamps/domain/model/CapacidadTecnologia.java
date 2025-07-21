package gabs.bootcamps.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="capacidad_tecnologia")
public class CapacidadTecnologia {

    @Column("tecnologia_id")
    private Long tecnologiaId;
    @Column("capacidad_id")
    private Long capacidadId;
}
