package gabs.reports.dto;

import lombok.Data;

@Data
public class PersonaRequest {
    private Long id;
    private String nombre;
    private String correo;
    private Integer edad;
}