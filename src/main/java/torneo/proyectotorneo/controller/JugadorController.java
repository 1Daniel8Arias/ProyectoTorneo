package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Contrato;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador que maneja la lógica de negocio entre la vista (ViewController)
 * y el ModelFactoryController / TorneoService.
 */
public class JugadorController {

    private final ModelFactoryController modelFactoryController;

    public JugadorController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD ────────────────

    public ArrayList<Jugador> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadores();
    }

    public void guardarJugador(Jugador jugador) throws RepositoryException {
        modelFactoryController.getTorneoService().guardarJugador(jugador);

    }

    public void actualizarJugador(Jugador jugador) throws RepositoryException {
        modelFactoryController.getTorneoService().actualizarJugador(jugador);
    }

    public void eliminarJugador(int id) throws RepositoryException {
        modelFactoryController.getTorneoService().eliminarJugador(id);

    }






    // ──────────────── CONSULTAS INTERMEDIAS ────────────────

    public ArrayList<Jugador> listarJugadoresPorEquipoYPosicion(int idEquipo, PosicionJugador posicion) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresPorEquipoYPosicion(idEquipo, posicion);
    }

    public ArrayList<Jugador> listarJugadoresPorPosicion(PosicionJugador posicion) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresPorPosicion(posicion);
    }


    public ArrayList<Jugador> listarJugadoresPorEquipo(int idEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresPorEquipo(idEquipo);
    }


    public ArrayList<Jugador> listarJugadoresConEquipo() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresConEquipo();
    }

    public ArrayList<Jugador> listarJugadoresConContratoYEquipo() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresConContratoYEquipo();
    }

    public ArrayList<Jugador> listarCapitanes() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarCapitanes();
    }

    // ──────────────── CONSULTAS AVANZADAS ────────────────

    public ArrayList<Jugador> listarJugadoresConSalarioSuperiorPromedio() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresConSalarioSuperiorPromedio();
    }

    public ArrayList<Jugador> listarJugadoresConGolesEnMasDeUnPartido() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresConGolesEnMasDeUnPartido();
    }

    public ArrayList<Jugador> listarJugadoresSinContratoActivo() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresSinContratoActivo();
    }

    public ArrayList<Jugador> listarDelanterosSinGoles() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarDelanterosSinGoles();
    }

    public ArrayList<Jugador> listarJugadoresConMayorSalarioPorPosicion() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarJugadoresConMayorSalarioPorPosicion();
    }

    // ──────────────── FILTROS AUXILIARES ────────────────

    /**
     * Retorna los nombres de los equipos disponibles (para llenar el ComboBox de la vista).
     */
    public List<String> obtenerNombresEquipos() {
        try {
            List<Equipo> equipos = modelFactoryController.getTorneoService().listarEquipos();
            return equipos.stream().map(Equipo::getNombre).collect(Collectors.toList());
        } catch (RepositoryException e) {
            System.err.println("Error obteniendo equipos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Filtra jugadores por nombre, equipo o posición.
     */
    public List<Jugador> filtrarJugadores(List<Jugador> jugadores, String nombre, String equipo, PosicionJugador posicion) {
        return jugadores.stream()
                .filter(j -> (nombre == null || j.getNombre().toLowerCase().contains(nombre.toLowerCase())) &&
                        (equipo == null || equipo.equals("Todos los equipos") || (j.getEquipo() != null && j.getEquipo().getNombre().equals(equipo))) &&
                        (posicion == null || j.getPosicion() == posicion))
                .collect(Collectors.toList());
    }

    public int obtenerIdEquipoPorNombre(String nombreEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().obtenerIdEquipoPorNombre(nombreEquipo);
    }

    public void registrarContrato(Contrato contrato) throws RepositoryException {
        modelFactoryController.getTorneoService().getJugadorService().registrarContrato(contrato);
    }

}
