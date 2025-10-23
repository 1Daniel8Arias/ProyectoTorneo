package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partido {
    private Integer idPartido;
    private LocalDate fecha;
    private String hora;
    private Jornada jornada;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private Estadio estadio;
    private ArrayList<Gol> listaGoles;
    private ArrayList<Tarjeta> listaTarjetas;
    private ArrayList<Sustitucion> listaSustituciones;
    private ArrayList<ArbitroPartido> listaArbitros;
    private ResultadoFinal resultadoFinal;
}
