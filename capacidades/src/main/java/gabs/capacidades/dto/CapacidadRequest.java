package gabs.capacidades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para crear o actualizar una capacidad", example = """
        {
            "nombre": "Desarrollo Backend con Spring Boot",
            "descripcion": "Capacidad para desarrollar aplicaciones backend robustas y escalables utilizando Spring Boot, incluyendo APIs RESTful, autenticación, autorización y integración con bases de datos",
            "tecnologias": [1, 2, 3, 4, 5]
        }
        """)
public class CapacidadRequest {

    @Schema(
        description = "Nombre de la capacidad", 
        example = "Desarrollo Backend con Spring Boot", 
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String nombre;
    
    @Schema(
        description = "Descripción detallada de la capacidad", 
        example = "Capacidad para desarrollar aplicaciones backend robustas y escalables utilizando Spring Boot, incluyendo APIs RESTful, autenticación, autorización y integración con bases de datos",
        maxLength = 500
    )
    private String descripcion;
    
    @Schema(
        description = "Lista de IDs de tecnologías asociadas a la capacidad (mínimo 3, máximo 20)", 
        example = "[1, 2, 3, 4, 5]", 
        required = true
    )
    private List<Long> tecnologias;
}
