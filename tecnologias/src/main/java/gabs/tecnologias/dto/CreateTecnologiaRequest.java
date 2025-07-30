package gabs.tecnologias.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para peticiones de creación de tecnología
 */
public class CreateTecnologiaRequest {
    
    @NotBlank(message = "El nombre de la tecnología es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;
    
    public CreateTecnologiaRequest() {}
    
    public CreateTecnologiaRequest(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}