package gabs.tecnologias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de petición para registrar capacidades de tecnología
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición para registrar capacidades de tecnología", example = """
        { 
            tecnologiaIds: [1, 2, 3]
        }
          
        
        """)
public class RegisterCapacidadTecnologiaRequest {

    @NotEmpty(message = "La lista de IDs de tecnologías no puede estar vacía")
    @Schema(description = "Lista de IDs de tecnologías a asociar", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> tecnologiaIds;
}
