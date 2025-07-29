package gabs.reports.dto;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BootcampRequest {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private List<CapacidadDTO> capacidades; // IDs o nombres de capacidades
    private LocalDate fechaFinalizacion; // IDs o nombres de tecnologías
}
