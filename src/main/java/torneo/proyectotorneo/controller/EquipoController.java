package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador que maneja la lógica de negocio para Equipos
 */
public class EquipoController {

    private final ModelFactoryController modelFactoryController;

    public EquipoController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public Equipo buscarPorId(int id) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarEquipoPorId(id);
    }

    public ArrayList<Equipo> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarEquipos();
    }

    public void guardarEquipo(Equipo equipo) throws RepositoryException {
        modelFactoryController.getTorneoService().registrarEquipo(equipo);
    }

    public void actualizarEquipo(Equipo equipo) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarEquipo(equipo);
    }

    public void eliminarEquipo(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarEquipo(id);
    }

    // ──────────────── CONSULTAS ESPECÍFICAS ────────────────

    public ArrayList<Equipo> listarEquiposConTecnico() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarEquiposConTecnico();
    }

    public ArrayList<Jugador> listarJugadoresPorEquipo(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresPorEquipo(idEquipo);
    }

    public void asignarCapitan(int idEquipo, int idJugador) throws RepositoryException {
        modelFactoryController.getTorneoService().asignarCapitan(idEquipo, idJugador);
    }

    public int obtenerIdEquipoPorNombre(String nombreEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().obtenerIdEquipoPorNombre(nombreEquipo);
    }
}