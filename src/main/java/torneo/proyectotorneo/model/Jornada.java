package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jornada {
    private int idJornada;
    private int numeroJornada;
    private ArrayList<Partido> listaPartidos;
}
