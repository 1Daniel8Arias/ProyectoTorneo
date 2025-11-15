package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.TablaPosicion;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Tabla de Posiciones
 */
public class TablaController {

    private final ModelFactoryController modelFactoryController;

    public TablaController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public TablaPosicion buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarTablaPosicionPorId(id);
    }

    public ArrayList<TablaPosicion> listarTodos() throws RepositoryException {
        return modelFactoryController.obtenerTablaPosiciones();
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────





    public ArrayList<TablaPosicion> ordenarPorPuntos() throws RepositoryException {
        return modelFactoryController.getTorneoService().ordenarTablaPorPuntos();
    }

    public ArrayList<TablaPosicion> ordenarPorDiferenciaGoles() throws RepositoryException {
        return modelFactoryController.getTorneoService().ordenarTablaPorDiferenciaGoles();
    }


}