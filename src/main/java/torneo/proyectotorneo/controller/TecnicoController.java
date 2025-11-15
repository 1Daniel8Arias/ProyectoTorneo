package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Técnicos
 */
public class TecnicoController {

    private final ModelFactoryController modelFactoryController;

    public TecnicoController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public Tecnico buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarTecnicoPorId(id);
    }

    public ArrayList<Tecnico> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarTecnicos();
    }

    public void guardarTecnico(Tecnico tecnico) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarTecnico(tecnico);
    }

    public void actualizarTecnico(Tecnico tecnico) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarTecnico(tecnico);
    }

    public void eliminarTecnico(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarTecnico(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────



    public ArrayList<Tecnico> listarTecnicosConEquipo() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarTecnicosConEquipo();
    }
    public ArrayList<Tecnico> listarTecniSinContrato() throws RepositoryException {
        return modelFactoryController.getTorneoService().getTecnicoService().listarTecnicosSinEquipo();
    }

}