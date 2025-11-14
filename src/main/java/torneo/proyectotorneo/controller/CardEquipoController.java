package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.ArrayList;

/**
 * Controlador específico para la visualización de tarjetas de equipos
 * Proporciona información completa del equipo para su visualización
 */
public class CardEquipoController {

    private final ModelFactoryController modelFactoryController;

    public CardEquipoController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CONSULTAS PARA CARD ────────────────

    public Equipo obtenerEquipoCompleto(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarEquipoPorId(idEquipo);
    }

    public ArrayList<Jugador> obtenerJugadoresDelEquipo(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresPorEquipo(idEquipo);
    }

    public Tecnico obtenerTecnicoDelEquipo(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().buscarTecnicoPorEquipo(idEquipo);
    }

    public Jugador obtenerCapitanDelEquipo(int idEquipo) throws RepositoryException {
        Equipo equipo = obtenerEquipoCompleto(idEquipo);
        if (equipo.getIdJugadorCapitan() != null) {
            return modelFactoryController.getTorneoService().getJugadorService()
                    .buscarJugadorPorId(equipo.getIdJugadorCapitan());
        }
        return null;
    }

    public int contarJugadoresDelEquipo(int idEquipo) throws RepositoryException {
        return obtenerJugadoresDelEquipo(idEquipo).size();
    }

    public ArrayList<Equipo> listarTodosLosEquipos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarEquipos();
    }
}