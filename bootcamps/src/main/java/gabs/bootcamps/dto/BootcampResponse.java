package gabs.bootcamps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor

public class BootcampResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private LocalDate fechaFinalizacion;
    private List<CapacidadDTO> capacidades;


}
