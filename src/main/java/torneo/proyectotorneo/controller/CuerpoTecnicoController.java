package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.CuerpoTecnico;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Cuerpo Técnico
 */
public class CuerpoTecnicoController {

    private final ModelFactoryController modelFactoryController;

    public CuerpoTecnicoController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public CuerpoTecnico buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarCuerpoTecnicoPorId(id);
    }

    public ArrayList<CuerpoTecnico> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarCuerpoTecnico();
    }

    public void guardarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarCuerpoTecnico(cuerpoTecnico);
    }

    public void actualizarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarCuerpoTecnico(cuerpoTecnico);
    }

    public void eliminarCuerpoTecnico(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarCuerpoTecnico(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────

    public ArrayList<CuerpoTecnico> listarCuerpoTecnicoPorEquipo(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarCuerpoTecnicoPorEquipo(idEquipo);
    }

    public ArrayList<CuerpoTecnico> listarCuerpoTecnicoPorEspecialidad(String especialidad) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarCuerpoTecnicoPorEspecialidad(especialidad);
    }
}