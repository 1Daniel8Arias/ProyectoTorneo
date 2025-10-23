package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sustitucion {
    private Integer idSustitucion;
    private Partido partido;
    private Jugador jugadorEntra;
    private Jugador jugadorSale;
}
