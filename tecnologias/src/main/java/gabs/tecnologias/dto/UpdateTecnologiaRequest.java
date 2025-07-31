package gabs.tecnologias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de petición para actualizar una tecnología existente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición para actualizar una tecnología", example = """
        {
          "nombre": "Spring Boot 3.0",
          "descripcion": "Framework actualizado para crear aplicaciones Spring independientes"
        }
        """)
public class UpdateTecnologiaRequest {

    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Schema(description = "Nombre de la tecnología", example = "Spring Boot 3.0")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Schema(description = "Descripción de la tecnología", example = "Framework actualizado para crear aplicaciones Spring independientes")
    private String descripcion;
}