package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {
    private Integer id;
    private String nombre;
    private Jugador capitan;
    private Tecnico tecnico;
    private ArrayList<Jugador> listaJugadoresJugadores;
    private ArrayList<CuerpoTecnico> listaCuerpoTecnicos;
    private ArrayList<EquipoEstadio> estadios;
    private ArrayList<Partido> listaPartidosLocal;
    private ArrayList<Partido> listaPartidosVisitante;
    private TablaPosicion tablaPosicion;
    private int cantidadJugadores;

    public int getCantidadJugadores() {

        if (this.listaJugadoresJugadores != null && !this.listaJugadoresJugadores.isEmpty()) {
            return this.listaJugadoresJugadores.size();
        }

        return this.cantidadJugadores;
    }
}
