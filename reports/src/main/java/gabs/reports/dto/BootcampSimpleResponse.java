package gabs.reports.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BootcampSimpleResponse {
    private Long id;
    private String nombre;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private LocalDate fechaFinalizacion;

}
