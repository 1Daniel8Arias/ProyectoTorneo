package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Sanciones
 */
public class SancionController {

    private final ModelFactoryController modelFactoryController;

    public SancionController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public Sancion buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarSancionPorId(id);
    }

    public ArrayList<Sancion> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarSanciones();
    }

    public void guardarSancion(Sancion sancion) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarSancion(sancion);
    }

    public void actualizarSancion(Sancion sancion) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarSancion(sancion);
    }

    public void eliminarSancion(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarSancion(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────

    public ArrayList<Sancion> listarSancionesPorJugador(int idJugador) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarSancionesPorJugador(idJugador);
    }

    public ArrayList<Sancion> listarSancionesPorTipo(String tipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarSancionesPorTipo(tipo);
    }

    public ArrayList<Sancion> listarSancionesActivas() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarSancionesActivas();
    }

    public ArrayList<Sancion> listarSancionesPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarSancionesPorFecha(fechaInicio, fechaFin);
    }
}