package gabs.tecnologias.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * DTO para peticiones de registro de capacidades de tecnología
 */
@Data
public class RegisterCapacidadTecnologiaRequest {

    @NotNull(message = "El ID de capacidad es obligatorio")
    private Long capacidadId;

    @NotEmpty(message = "La lista de IDs de tecnologías no puede estar vacía")
    private List<Long> tecnologiaIds;
}
