package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Árbitros
 */
public class ArbitroController {

    private final ModelFactoryController modelFactoryController;

    public ArbitroController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public Arbitro buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarArbitroPorId(id);
    }

    public ArrayList<Arbitro> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarArbitros();
    }

    public void guardarArbitro(Arbitro arbitro) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarArbitro(arbitro);
    }

    public void actualizarArbitro(Arbitro arbitro) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarArbitro(arbitro);
    }

    public void eliminarArbitro(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarArbitro(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────

    public ArrayList<Arbitro> listarArbitrosConPartidos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarArbitrosConPartidos();
    }

    public ArrayList<Arbitro> listarArbitrosPorTipo(String tipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarArbitrosPorTipo(tipo);
    }
}