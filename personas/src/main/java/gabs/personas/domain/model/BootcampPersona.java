package gabs.personas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("bootcamp_persona")
@AllArgsConstructor
@NoArgsConstructor
public class BootcampPersona {
    @Id
    private Long id;

    @Column("id_bootcamp")
    private Long  bootcampId;
    @Column("id_persona")
    private Long personaId;

}
