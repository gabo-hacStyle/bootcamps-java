package gabs.tecnologias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de petición para crear una nueva tecnología
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición para crear una nueva tecnología", example = """
        {
          "nombre": "Spring Boot",
          "descripcion": "Framework para crear aplicaciones Spring independientes"
        }
        """)
public class CreateTecnologiaRequest {

    @NotBlank(message = "El nombre de la tecnología es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Schema(description = "Nombre de la tecnología", example = "Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Schema(description = "Descripción de la tecnología", example = "Framework para crear aplicaciones Spring independientes")
    private String descripcion;
}