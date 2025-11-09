package torneo.proyectotorneo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Torneo {

    private ArrayList<Equipo> listaEquipos;
    private ArrayList<Jugador> listaJugadores;
    private ArrayList<Partido> listaPartidos;
    private ArrayList<Arbitro> listaArbitros;
    private ArrayList<Tecnico> listaTecnicos;
    private ArrayList<Jornada> listaJornadas;
    private ArrayList<Estadio> listaEstadios;
    private ArrayList<TablaPosicion> tablaPosiciones;
    private ArrayList<Sancion> listaSanciones;
    private ArrayList<Contrato> listaContratos;
    private ArrayList<Gol> listaGoles;
    private ArrayList<Tarjeta> listaTarjetas;
    private ArrayList<Sustitucion> listaSustituciones;
    private ArrayList<Departamento> listaDepartamentos;
    private ArrayList<Municipio> listaMunicipios;
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<CuerpoTecnico> listaCuerposTecnicos;
    private ArrayList<Ciudad> listaCiudades;
}
