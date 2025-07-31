package gabs.capacidades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@Schema(description = "DTO de respuesta para una capacidad", example = """
        {
            "id": 1,
            "nombre": "Desarrollo Backend con Spring Boot",
            "descripcion": "Capacidad para desarrollar aplicaciones backend robustas y escalables utilizando Spring Boot, incluyendo APIs RESTful, autenticación, autorización y integración con bases de datos",
            "tecnologiasList": [
                {
                    "id": 1,
                    "nombre": "Java"
                },
                {
                    "id": 2,
                    "nombre": "Spring Boot"
                },
                {
                    "id": 3,
                    "nombre": "MySQL"
                },
                {
                    "id": 4,
                    "nombre": "Docker"
                },
                {
                    "id": 5,
                    "nombre": "Git"
                }
            ]
        }
        """)
public class CapacidadResponse {
    
    @Schema(description = "ID único de la capacidad", example = "1")
    private Long id;

    @Schema(description = "Nombre de la capacidad", example = "Desarrollo Backend")
    private String nombre;
    
    @Schema(description = "Descripción detallada de la capacidad", example = "Capacidad para desarrollar aplicaciones backend con Spring Boot")
    private String descripcion;
    
    @Schema(description = "Lista de tecnologías asociadas a la capacidad")
    private List<Tecnologias> tecnologiasList;
}
