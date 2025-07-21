package gabs.tecnologias.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="capacidades_tecnologia")
public class CapacidadTecnologia {
    @Id
    private Long id;

    @Column("tecnologia_id")
    private Long tecnologiaId;
    @Column("capacidad_id")
    private Long capacidadId;
}