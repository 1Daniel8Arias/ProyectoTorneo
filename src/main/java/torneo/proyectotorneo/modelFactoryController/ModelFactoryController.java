package torneo.proyectotorneo.modelFactoryController;

import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Partido;

import java.util.ArrayList;

public class ModelFactoryController {

    private static ModelFactoryController instance;
    private ArrayList<Equipo> listaEquipos;
    private ArrayList<Jugador> listaJugadores;
    private ArrayList<Arbitro> listaArbitros;
    private ArrayList<Partido> listaPartidos;


    public static ModelFactoryController getInstance() {
        if (instance == null) {
            instance = new ModelFactoryController();
        }
        return instance;
    }

    private ModelFactoryController() {

    }

    private void cargarDatosIniciales() {
    }


}
