package torneo.proyectotorneo.modelFactoryController;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.service.JugadorService;
import torneo.proyectotorneo.service.TablaPosicionService;
import torneo.proyectotorneo.service.TorneoService;

import java.util.ArrayList;

public class ModelFactoryController {

    private static ModelFactoryController instance;
    private final TorneoService torneoService;
    private final TablaPosicionService tablaPosicionService;


    public static ModelFactoryController getInstance() {
        if (instance == null) {
            instance = new ModelFactoryController();
        }
        return instance;
    }

    private ModelFactoryController() {
        this.tablaPosicionService = new TablaPosicionService();
        this.torneoService = new TorneoService();
    }

    public TorneoService getTorneoService() {
        return torneoService;
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

    public ArrayList<TablaPosicion> obtenerTablaPosiciones() throws RepositoryException {
        return tablaPosicionService.obtenerTablaPosiciones();
    }

    public Usuario obtenerUsuario(String usuario, String contrasenia) {
        return torneoService.obtenerUsuario( usuario,  contrasenia);
    }

    // ================== ESTADÍSTICAS ==================
    public int contarEquipos() throws RepositoryException {
        return torneoService.contarEquipos();
    }

    public int contarJugadores() throws RepositoryException {
        return torneoService.contarJugadores();
    }

    public int contarPartidosJugados() throws RepositoryException {
        return torneoService.contarPartidosJugados();
    }

    public int contarJornadas() throws RepositoryException {
        return torneoService.contarJornadas();
    }

    // ================== TABLA DE POSICIONES ==================
    public ArrayList<TablaPosicion> obtenerTablaPosicionesTop5() throws RepositoryException {
        return torneoService.obtenerTablaPosicionesTop5();
    }

    // ================== PARTIDOS ==================
    public ArrayList<Partido> obtenerProximosPartidos() throws RepositoryException {
        return torneoService.obtenerProximosPartidos();
    }

    public ArrayList<Partido> obtenerResultadosRecientes() throws RepositoryException {
        return torneoService.obtenerResultadosRecientes();
    }

}
