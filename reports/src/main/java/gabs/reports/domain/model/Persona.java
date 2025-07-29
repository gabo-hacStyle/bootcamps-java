package gabs.reports.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "personas")
public class Persona {
    @Id
    private String objectId;


    private Long personaId;
    private String nombre;
    private String correo;
    private Integer edad;
} 