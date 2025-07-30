package gabs.tecnologias.dto;



import jakarta.validation.constraints.Size;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para peticiones de actualización de tecnología
 */

 @Data
 @NoArgsConstructor
 @AllArgsConstructor
public class UpdateTecnologiaRequest {
    
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

}