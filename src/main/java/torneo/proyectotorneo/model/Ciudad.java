package torneo.proyectotorneo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ciudad {
    private Integer idCiudad;
    private String nombre;
    private Municipio municipio;
}
