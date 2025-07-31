package gabs.reports.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "BootcampResponse",
    description = "DTO para la respuesta de información de un bootcamp",
    example = """
        {
          "id": 1,
          "nombre": "Java Full Stack Developer",
          "descripcion": "Bootcamp completo para desarrolladores Java con tecnologías modernas",
          "fechaLanzamiento": "2024-03-15",
          "fechaFinalizacion": "2024-06-07",
          "duracion": 12,
          "cantidadCapacidades": 2,
          "cantidadTecnologias": 3,
          "cantidadPersonasInscritas": 25,
          "tecnologias": [
            "Java 17",
            "Spring Boot 3",
            "React"
          ],
          "capacidades": [
            "Desarrollo Backend",
            "Desarrollo Frontend"
          ],
          personas: [
          {nombre: "Juan Carlos Pérez González", correo: "juan.perez@email.com"}, 
          {nombre: "Ana María López García", correo: "ana.lopez@email.com"},
          ]
        }
        """
)
public class BootcampResponse {

    @Schema(
        description = "Identificador único del bootcamp",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre del bootcamp",
        example = "Java Full Stack Developer"
    )
    private String nombre;

    @Schema(
        description = "Descripción detallada del bootcamp",
        example = "Bootcamp completo para desarrolladores Java con tecnologías modernas incluyendo Spring Boot, React y bases de datos"
    )
    private String descripcion;

    @Schema(
        description = "Fecha de lanzamiento del bootcamp",
        example = "2024-03-15"
    )
    private LocalDate fechaLanzamiento;

    @Schema(
        description = "Fecha de finalización del bootcamp",
        example = "2024-06-07"
    )
    private LocalDate fechaFinalizacion;

    @Schema(
        description = "Duración del bootcamp en semanas",
        example = "12"
    )
    private Integer duracion;

    @Schema(
        description = "Lista de capacidades que se desarrollarán en el bootcamp",
        example = "Desarrollo Backend, Desarrollo Frontend"
    )   
    private List<String> capacidades;

    @Schema(
        description = "Lista de tecnologías que se enseñarán en el bootcamp",
        example = "Java 17, Spring Boot 3, React"
    )
    private List<String> tecnologias;

    @Schema(
        description = "Cantidad de capacidades que se desarrollarán en el bootcamp",
        example = "2"
    )
private int cantidadCapacidades;

    @Schema(
        description = "Cantidad de tecnologías que se enseñarán en el bootcamp",
        example = "3"
    )
    private int cantidadTecnologias;

    @Schema(
        description = "Cantidad de personas inscritas en el bootcamp",
        example = "25"
    )
    private int cantidadPersonasInscritas;

    @Schema(
        description = "Lista de personas inscritas en el bootcamp con su nombre y correo",
        example = """
        [{nombre: 'Juan Carlos Pérez González', correo: 'juan.perez@email.com'}, 
        {nombre: 'Ana María López García', correo: 'ana.lopez@email.com'}]
        """
    )
    private List<PersonaInfo> personas;

  
    
} 