package gabs.capacidades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class CapacidadRequest {

        private String nombre;
        private String descripcion;
        private List<Long> tecnologias;


}
