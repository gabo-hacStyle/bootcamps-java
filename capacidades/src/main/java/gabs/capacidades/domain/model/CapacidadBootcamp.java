package gabs.capacidades.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("capacidad_bootcamp")
@AllArgsConstructor
@NoArgsConstructor
public class CapacidadBootcamp {
    @Id
    private Long id;

    @Column("bootcamp_id")
    private Long bootcampId;
    @Column("capacidad_id")
    private Long capacidadId;

}
