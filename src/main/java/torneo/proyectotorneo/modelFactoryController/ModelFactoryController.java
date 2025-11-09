package torneo.proyectotorneo.modelFactoryController;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.service.TorneoService;

import java.util.ArrayList;

public class ModelFactoryController {

    private static ModelFactoryController instance;
    private final TorneoService torneoService;



    public static ModelFactoryController getInstance() {
        if (instance == null) {
            instance = new ModelFactoryController();
        }
        return instance;
    }

    private ModelFactoryController() {
        this.torneoService = new TorneoService();
    }

    private void cargarDatosIniciales() {
    }

    public ArrayList<Equipo> obtenerEquipos() throws RepositoryException {
        return torneoService.listarEquipos();
    }

    public void registrarEquipo(Equipo equipo) throws RepositoryException {
        torneoService.registrarEquipo(equipo);
    }

    public ArrayList<Partido> obtenerPartidos() throws RepositoryException {
        return torneoService.listarPartidos();
    }

    public ArrayList<Jornada> obtenerJornadas() throws RepositoryException {
        return torneoService.listarJornadas();
    }

    public ArrayList<Jugador> obtenerJugadores() throws RepositoryException {
        return torneoService.listarJugadores();
    }

    public ArrayList<Arbitro> obtenerArbitros() throws RepositoryException {
        return torneoService.listarArbitros();
    }

    public ArrayList<Gol> obtenerGoles() throws RepositoryException {
        return torneoService.listarGoles();
    }

    public ArrayList<Estadio> obtenerEstadios() throws RepositoryException {
        return torneoService.listarEstadios();
    }

    public ArrayList<Tecnico> obtenerTecnicos() throws RepositoryException {
        return torneoService.listarTecnicos();
    }

}
