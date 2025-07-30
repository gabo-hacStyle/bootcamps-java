package gabs.capacidades.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CapacidadBootcampResponse {

    private Long id;
    private String nombre;
    private List<Tecnologias> tecnologias;



}
