package gabs.reports.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BootcampConPersonasDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private LocalDate fechaFinalizacion;
    private List<String> capacidades;
    private List<String> tecnologias;
    private int cantidadCapacidades;
    private int cantidadTecnologias;
    private int cantidadPersonasInscritas;
    private List<PersonaInfo> personas;
} 