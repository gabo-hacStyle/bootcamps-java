package gabs.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "TecnologiaDTO",
    description = "DTO para representar una tecnología",
    example = """
        {
          "id": 1,
          "nombre": "Java 17"
        }
        """
)
public class TecnologiaDTO {
    
    @Schema(
        description = "Identificador único de la tecnología",
        example = "1"
    )
    private Long id;
    @Schema(
        description = "Nombre de la tecnología",
        example = "Java 17",
        required = true
    )
    @NotBlank(message = "El nombre de la tecnología es obligatorio")
    private String nombre;
}