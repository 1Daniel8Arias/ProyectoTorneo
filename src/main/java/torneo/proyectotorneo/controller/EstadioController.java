package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.model.Departamento;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Estadios
 */
public class EstadioController {

    private final ModelFactoryController modelFactoryController;

    public EstadioController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public Estadio buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarEstadioPorId(id);
    }

    public ArrayList<Estadio> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarEstadios();
    }

    public void guardarEstadio(Estadio estadio) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarEstadio(estadio);
    }

    public void actualizarEstadio(Estadio estadio) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarEstadio(estadio);
    }

    public void eliminarEstadio(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarEstadio(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────

    public ArrayList<Estadio> listarEstadiosPorDepartamento(int idDepartamento) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarEstadiosPorDepartamento(idDepartamento);
    }

    public ArrayList<Estadio> listarEstadiosPorCapacidadMinima(int capacidadMinima) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarEstadiosPorCapacidadMinima(capacidadMinima);
    }

    public ArrayList<Departamento> listarDepartamentos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarDepartamentos();
    }

    public Integer obtenerIdEstadioPorNombre(String nombreEstadio) throws RepositoryException {
        return modelFactoryController.getTorneoService().obtenerIdEstadioPorNombre(nombreEstadio);
    }
}