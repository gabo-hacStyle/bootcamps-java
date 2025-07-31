package gabs.capacidades.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO para parámetros de paginación y ordenamiento")
public class PageAndQuery {
    
    @Schema(description = "Número de página (base 0)", example = "0")
    public int page;
    
    @Schema(description = "Tamaño de la página", example = "10")
    public int size;
    
    @Schema(description = "Campo por el cual ordenar", example = "nombre", allowableValues = {"nombre", "descripcion", "id"})
    public String sortBy;
    
    @Schema(description = "Dirección del ordenamiento", example = "asc", allowableValues = {"asc", "desc"})
    public String direction;
}
