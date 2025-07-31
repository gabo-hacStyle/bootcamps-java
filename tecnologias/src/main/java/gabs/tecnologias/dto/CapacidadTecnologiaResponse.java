package gabs.tecnologias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para CapacidadTecnologia
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de capacidad de tecnología", example = """
        {
            "id": 1,
            "nombre": "Java",
        }
        """)
public class CapacidadTecnologiaResponse {


    @Schema(description = "ID de la tecnología", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

        @Schema(description = "Información de la tecnología asociada")
        private TecnologiaResponse tecnologia;
}
