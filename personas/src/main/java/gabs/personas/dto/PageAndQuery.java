package gabs.personas.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageAndQuery {
    public int page;
    public int size;
    public String sortBy;
    public String direction;
}
