package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import torneo.proyectotorneo.model.enums.TipoTarjeta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {
    private Integer idTarjeta;
    private TipoTarjeta tipo; // AMARILLO o ROJO
    private Partido partido;
    private Jugador jugador;
}
