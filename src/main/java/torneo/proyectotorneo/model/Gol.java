package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gol {
    private Integer idGol;
    private int numeroGoles;
    private Partido partido;
    private Jugador jugador;
}
