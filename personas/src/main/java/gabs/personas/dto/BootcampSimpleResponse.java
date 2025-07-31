package gabs.personas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@Schema(description = "Respuesta simplificada de bootcamp",
        example = """
        {
          "id": 5,
          "nombre": "Java Full Stack Development",
          "fechaLanzamiento": "2024-02-01",
          "duracion": 16,
          "fechaFinalizacion": "2024-05-31"
        }
        """)
public class BootcampSimpleResponse {
    private Long id;
    private String nombre;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private LocalDate fechaFinalizacion;

}
