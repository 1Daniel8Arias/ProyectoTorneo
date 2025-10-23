package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {
    private Integer idTarjeta;
    private Tarjeta tipo; // AMARILLO o ROJO
    private Partido partido;
    private Jugador jugador;
}
