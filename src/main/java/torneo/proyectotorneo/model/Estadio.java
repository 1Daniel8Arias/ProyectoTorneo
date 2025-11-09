package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estadio {
    private Integer idEstadio;
    private String nombre;
    private int capacidad;
    private Departamento departamento;
    private ArrayList<EquipoEstadio> equipoEstadios;
    private ArrayList<Partido> listaPartidos;

}
