package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Arbitro {
    private Integer idArbitro;
    private String nombre;
    private String apellido;
    private ArrayList<ArbitroPartido> arbitrosPartidos;
    private int partidosArbitrados;
}
