package gabs.bootcamps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class BootcampRequest {

        private String nombre;
        private String descripcion;
        private List<Long> capacidades;
        private Integer duracion;

}
