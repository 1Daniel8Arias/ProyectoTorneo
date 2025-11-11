package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.model.TablaPosicion;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

public class HomeController {
    private final ModelFactoryController modelFactory;

    public HomeController() {
        this.modelFactory = ModelFactoryController.getInstance();
    }

    // ================== ESTADÍSTICAS ==================
    public int obtenerNumeroEquipos() {
        try {
            return modelFactory.contarEquipos();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener equipos: " + e.getMessage());
            return 0;
        }
    }

    public int obtenerNumeroJugadores() {
        try {
            return modelFactory.contarJugadores();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener jugadores: " + e.getMessage());
            return 0;
        }
    }

    public int obtenerNumeroPartidosJugados() {
        try {
            return modelFactory.contarPartidosJugados();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener partidos: " + e.getMessage());
            return 0;
        }
    }

    public int obtenerNumeroJornadas() {
        try {
            return modelFactory.contarJornadas();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener jornadas: " + e.getMessage());
            return 0;
        }
    }

    // ================== TABLA DE POSICIONES ==================
    public ArrayList<TablaPosicion> obtenerTablaPosiciones() {
        try {
            return modelFactory.obtenerTablaPosicionesTop5();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener tabla de posiciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ================== PARTIDOS ==================
    public ArrayList<Partido> obtenerProximosPartidos() {
        try {
            return modelFactory.obtenerProximosPartidos();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener próximos partidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ArrayList<Partido> obtenerResultadosRecientes() {
        try {
            return modelFactory.obtenerResultadosRecientes();
        } catch (RepositoryException e) {
            System.err.println("Error al obtener resultados recientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
