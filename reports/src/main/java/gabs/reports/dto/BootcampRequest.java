package gabs.reports.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;


import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "BootcampRequest",
    description = "DTO para la creación de un nuevo bootcamp",
    example = """
        {
          "nombre": "Java Full Stack Developer",
          "descripcion": "Bootcamp completo para desarrolladores Java con tecnologías modernas",
          "fechaLanzamiento": "2024-03-15",
          "duracion": 12,
          "fechaFinalizacion": "2024-06-07",
          
          "capacidades": [
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
            },
            {
              "id": 2,
              "nombre": "Desarrollo Frontend",
              "tecnologias": [
                {
                  "id": 3,
                  "nombre": "React"
                }
              ]
            }
          ]
        }
        """
)
public class BootcampRequest {

    @Schema(
        description = "Identificador único del bootcamp",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre del bootcamp",
        example = "Java Full Stack Developer",
        required = true
    )
    @NotBlank(message = "El nombre del bootcamp es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Schema(
        description = "Descripción detallada del bootcamp",
        example = "Bootcamp completo para desarrolladores Java con tecnologías modernas incluyendo Spring Boot, React y bases de datos",
        required = true
    )
    @NotBlank(message = "La descripción del bootcamp es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @Schema(
        description = "Fecha de lanzamiento del bootcamp en formato YYYY-MM-DD",
        example = "2024-03-15",
        required = true
    )
    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @Future(message = "La fecha de lanzamiento debe ser futura")
    private LocalDate fechaLanzamiento;

    @Schema(
        description = "Duración del bootcamp en semanas",
        example = "12",
        minimum = "1",
        maximum = "52",
        required = true
    )
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración mínima es 1 semana")
    @Max(value = 52, message = "La duración máxima es 52 semanas")
    private Integer duracion;


    @Schema(
        description = "Lista de capacidades que se desarrollarán en el bootcamp",
        required = true
    )
    @NotEmpty(message = "Debe incluir al menos una capacidad")
    private List<CapacidadDTO> capacidades; 

    @Schema(
        description = "Fecha de finalización del bootcamp en formato YYYY-MM-DD",
        example = "2024-06-07",
        required = true
    )
    @NotNull(message = "La fecha de finalización es obligatoria")
    private LocalDate fechaFinalizacion; 
}
