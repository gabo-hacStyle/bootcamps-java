package gabs.reports.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "inscripciones")
public class Inscripcion {


    @Id
    private String objectId;

    private Long id;
    private Long personaId;
    private Long bootcampId;
    private String nombrePersona;
    private String correoPersona;
    private String nombreBootcamp;
} 