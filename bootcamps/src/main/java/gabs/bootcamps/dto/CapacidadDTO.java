package gabs.bootcamps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CapacidadDTO {
    private Long id;
    private String nombre;
    private List<TecnologiaDTO> tecnologias;
}
