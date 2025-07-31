package gabs.reports.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "CapacidadDTO",
        description = "DTO para representar una capacidad con sus tecnologías asociadas",
        example = """
            {
              "id": 1,
              "nombre": "Desarrollo Backend",
              "tecnologias": [
                {
                  "id": 1,
                  "nombre": "Java 17"
                },
                {
                  "id": 2,
                  "nombre": "Spring Boot 3"
                }
              ]
            }
            """
    )
public class CapacidadDTO {
    @Schema(
            description = "Identificador único de la capacidad",
            example = "1"
        )
        private Long id;

        @Schema(
            description = "Nombre de la capacidad",
            example = "Desarrollo Backend",
            required = true
        )
        @NotBlank(message = "El nombre de la capacidad es obligatorio")
        private String nombre;

        @Schema(
            description = "Lista de tecnologías asociadas a esta capacidad",
            required = true
        )
        @NotEmpty(message = "Debe incluir al menos una tecnología para la capacidad")
        private List<TecnologiaDTO> tecnologias;
}

