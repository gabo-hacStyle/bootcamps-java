package gabs.reports.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "bootcamps")
public class Bootcamp {


    @Id
    private String objectId;


    private Long bootcampId;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private LocalDate fechaFinalizacion;
    private List<String> capacidades; // IDs o nombres de capacidades
    private List<String> tecnologias; // IDs o nombres de tecnologías
    private List<Long> personasInscritas; // IDs de personas
    private int cantidadCapacidades;
    private int cantidadTecnologias;
    private int cantidadPersonasInscritas;
}
