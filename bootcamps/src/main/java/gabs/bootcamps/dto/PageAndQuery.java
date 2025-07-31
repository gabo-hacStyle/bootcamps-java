package gabs.bootcamps.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageAndQuery {
    
    @Schema(description = "Número de página (base 0)", example = "0")
    public int page;
    
    @Schema(description = "Tamaño de la página", example = "10")
    public int size;
    
    @Schema(description = "Campo por el cual ordenar", example = "nombre")
    public String sortBy;
    
    @Schema(description = "Dirección del ordenamiento (asc/desc)", example = "asc")
    public String direction;
}
