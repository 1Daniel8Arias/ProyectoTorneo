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

    public void guardarTablaPosicion(TablaPosicion tablaPosicion) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarTablaPosicion(tablaPosicion);
    }

    public void actualizarTablaPosicion(TablaPosicion tablaPosicion) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarTablaPosicion(tablaPosicion);
    }

    public void eliminarTablaPosicion(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarTablaPosicion(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────

    public TablaPosicion buscarPorEquipo(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarTablaPosicionPorEquipo(idEquipo);
    }

    public ArrayList<TablaPosicion> obtenerTablaPosicionesTop5() throws RepositoryException {
        return modelFactoryController.obtenerTablaPosicionesTop5();
    }

    public ArrayList<TablaPosicion> ordenarPorPuntos() throws RepositoryException {
        return modelFactoryController.getTorneoService().ordenarTablaPorPuntos();
    }

    public ArrayList<TablaPosicion> ordenarPorDiferenciaGoles() throws RepositoryException {
        return modelFactoryController.getTorneoService().ordenarTablaPorDiferenciaGoles();
    }

    public void actualizarTablaDespuesDePartido(int idPartido) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarTablaDespuesDePartido(idPartido);
    }
}