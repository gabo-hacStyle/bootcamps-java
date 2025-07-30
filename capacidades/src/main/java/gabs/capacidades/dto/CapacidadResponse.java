package gabs.capacidades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CapacidadResponse {
    private Long id;

    private String nombre;
    private String descripcion;
    private List<Tecnologias> tecnologiasList;



}
