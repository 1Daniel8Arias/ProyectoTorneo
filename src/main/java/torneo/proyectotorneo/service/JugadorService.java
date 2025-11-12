package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.JugadorNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Contrato;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.repository.ContratoRepository;
import torneo.proyectotorneo.repository.EquipoRepository;
import torneo.proyectotorneo.repository.JugadorRepository;
import torneo.proyectotorneo.repository.SancionRepository;

import java.time.LocalDate;
import java.util.ArrayList;

public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final ContratoRepository contratoRepository;
    private final SancionRepository sancionRepository;
    private final EquipoRepository equipoRepository;

    public JugadorService() {
        this.jugadorRepository = new JugadorRepository();
        this.contratoRepository = new ContratoRepository();
        this.sancionRepository = new SancionRepository();
        this.equipoRepository = new EquipoRepository();
    }

    /**
     * Lista todos los jugadores registrados
     */
    public ArrayList<Jugador> listarTodosLosJugadores() throws RepositoryException {
        return jugadorRepository.listarTodos();
    }

    /**
     * Busca un jugador por su ID
     */
    public Jugador buscarJugadorPorId(int id) throws JugadorNoEncontradoException {
        try {
            Jugador jugador = jugadorRepository.buscarPorId(id);
            if (jugador == null) {
                throw new JugadorNoEncontradoException("No se encontró el jugador con ID: " + id);
            }
            return jugador;
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al buscar el jugador: " + e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresPorEquipo(int idEquipo) throws JugadorNoEncontradoException {
        try {
            ArrayList<Jugador> jugadores = jugadorRepository.listarJugadoresPorEquipo(idEquipo);
            if (jugadores.isEmpty()) {
                throw new JugadorNoEncontradoException("No hay jugadores registrados para el equipo con ID " + idEquipo);
            }
            return jugadores;
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores por equipo: " + e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresPorPosicion(PosicionJugador posicion) throws RepositoryException {
        return jugadorRepository.listarJugadoresPorPosicion(posicion.name());
    }

    public ArrayList<Jugador> listarJugadoresPorEquipoYPosicion(int idEquipo, PosicionJugador posicion) throws RepositoryException {
        return jugadorRepository.listarJugadoresPorEquipoYPosicion(idEquipo, posicion.name());
    }


    public void registrarJugador(Jugador jugador) throws JugadorNoEncontradoException {
        validarJugador(jugador);

        try {
            jugadorRepository.guardar(jugador);
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al registrar el jugador: " + e.getMessage());
        }
    }


    public void actualizarJugador(Jugador jugador) throws JugadorNoEncontradoException {
        validarJugador(jugador);

        if (jugador.getId() == null) {
            throw new JugadorNoEncontradoException("El ID del jugador es requerido para actualizar");
        }

        try {
            jugadorRepository.actualizar(jugador);
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al actualizar el jugador: " + e.getMessage());
        }
    }


    public void registrarContrato(Contrato contrato) throws JugadorNoEncontradoException {
        if (contrato == null) {
            throw new JugadorNoEncontradoException("El contrato no puede ser nulo");
        }

        if (contrato.getJugador() == null) {
            throw new JugadorNoEncontradoException("El jugador es obligatorio");
        }

        if (contrato.getFechaInicio() == null || contrato.getFechaFin() == null) {
            throw new JugadorNoEncontradoException("Las fechas de inicio y fin son obligatorias");
        }

        if (contrato.getFechaInicio().isAfter(contrato.getFechaFin())) {
            throw new JugadorNoEncontradoException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (contrato.getSalario() <= 0) {
            throw new JugadorNoEncontradoException("El salario debe ser mayor a cero");
        }

        try {
            contratoRepository.guardar(contrato);
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al registrar el contrato: " + e.getMessage());
        }
    }


    public void aplicarSancion(Sancion sancion) throws JugadorNoEncontradoException {
        if (sancion == null) {
            throw new JugadorNoEncontradoException("La sanción no puede ser nula");
        }

        if (sancion.getJugador() == null) {
            throw new JugadorNoEncontradoException("El jugador es obligatorio");
        }

        if (sancion.getMotivo() == null || sancion.getMotivo().trim().isEmpty()) {
            throw new JugadorNoEncontradoException("El motivo de la sanción es obligatorio");
        }

        if (sancion.getDuracion() < 0) {
            throw new JugadorNoEncontradoException("La duración de la sanción no puede ser negativa");
        }

        if (sancion.getFecha() == null) {
            sancion.setFecha(LocalDate.now());
        }

        try {
            sancionRepository.guardar(sancion);
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al aplicar la sanción: " + e.getMessage());
        }
    }


    public boolean estaSancionado(int idJugador, LocalDate fecha) throws JugadorNoEncontradoException {
        try {
            Jugador jugador = jugadorRepository.buscarPorId(idJugador);
            if (jugador == null) {
                throw new JugadorNoEncontradoException("No se encontró el jugador");
            }

            ArrayList<Sancion> sanciones = jugador.getListaSanciones();
            if (sanciones == null || sanciones.isEmpty()) {
                return false;
            }

            for (Sancion sancion : sanciones) {
                LocalDate fechaFin = sancion.getFecha().plusDays(sancion.getDuracion());
                if (!fecha.isBefore(sancion.getFecha()) && !fecha.isAfter(fechaFin)) {
                    return true;
                }
            }

            return false;
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al verificar sanciones: " + e.getMessage());
        }
    }


    public ArrayList<Sancion> obtenerSancionesJugador(int idJugador) throws JugadorNoEncontradoException {
        try {
            Jugador jugador = jugadorRepository.buscarPorId(idJugador);
            if (jugador == null) {
                throw new JugadorNoEncontradoException("No se encontró el jugador");
            }
            return jugador.getListaSanciones();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al obtener las sanciones: " + e.getMessage());
        }
    }


    public ArrayList<Contrato> obtenerContratosJugador(int idJugador) throws JugadorNoEncontradoException {
        try {
            Jugador jugador = jugadorRepository.buscarPorId(idJugador);
            if (jugador == null) {
                throw new JugadorNoEncontradoException("No se encontró el jugador");
            }
            return jugador.getListaContratos();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al obtener los contratos: " + e.getMessage());
        }
    }


    public String obtenerRendimientoJugador(int idJugador) throws JugadorNoEncontradoException {
        try {
            Jugador jugador = jugadorRepository.buscarPorId(idJugador);
            if (jugador == null) {
                throw new JugadorNoEncontradoException("No se encontró el jugador");
            }

            int totalGoles = jugador.getListaGoles() != null ? jugador.getListaGoles().size() : 0;
            int totalTarjetas = jugador.getListaTarjetas() != null ? jugador.getListaTarjetas().size() : 0;
            int totalSanciones = jugador.getListaSanciones() != null ? jugador.getListaSanciones().size() : 0;

            return String.format("Jugador: %s %s\nGoles: %d\nTarjetas: %d\nSanciones: %d",
                    jugador.getNombre(), jugador.getApellido(), totalGoles, totalTarjetas, totalSanciones);

        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al obtener el rendimiento: " + e.getMessage());
        }
    }

    public void eliminarJugador(int id) throws JugadorNoEncontradoException {
        try {
            Jugador jugador = jugadorRepository.buscarPorId(id);
            if (jugador == null) {
                throw new JugadorNoEncontradoException("No se encontró el jugador con ID: " + id);
            }

            jugadorRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al eliminar el jugador: " + e.getMessage());
        }
    }

    private void validarJugador(Jugador jugador) throws JugadorNoEncontradoException {
        if (jugador == null) {
            throw new JugadorNoEncontradoException("El jugador no puede ser nulo");
        }

        if (jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()) {
            throw new JugadorNoEncontradoException("El nombre del jugador es obligatorio");
        }

        if (jugador.getApellido() == null || jugador.getApellido().trim().isEmpty()) {
            throw new JugadorNoEncontradoException("El apellido del jugador es obligatorio");
        }

        if (jugador.getPosicion() == null) {
            throw new JugadorNoEncontradoException("La posición del jugador es obligatoria");
        }

        if (jugador.getNumeroCamiseta() == null || jugador.getNumeroCamiseta().trim().isEmpty()) {
            throw new JugadorNoEncontradoException("El número de camiseta es obligatorio");
        }

        if (jugador.getEquipo() == null) {
            throw new JugadorNoEncontradoException("El equipo del jugador es obligatorio");
        }
    }

    /**
            * Consulta Intermedia 1: Lista jugadores con su equipo
     */
    public ArrayList<Jugador> listarJugadoresConEquipo() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarJugadoresConEquipo();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores con equipo: " + e.getMessage());
        }
    }

    /**
     * Consulta Intermedia 4: Lista jugadores con contrato y equipo
     */
    public ArrayList<Jugador> listarJugadoresConContratoYEquipo() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarJugadoresConContratoYEquipo();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores con contrato: " + e.getMessage());
        }
    }

    /**
     * Consulta Intermedia 7: Lista capitanes de equipos
     */
    public ArrayList<Jugador> listarCapitanes() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarCapitanes();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar capitanes: " + e.getMessage());
        }
    }

    // ============================================================
    // CONSULTAS AVANZADAS
    // ============================================================

    /**
     * Consulta Avanzada 1: Jugadores con salario superior al promedio
     */
    public ArrayList<Jugador> listarJugadoresConSalarioSuperiorPromedio() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarJugadoresConSalarioSuperiorPromedio();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores con salario superior: " + e.getMessage());
        }
    }

    /**
     * Consulta Avanzada 2: Jugadores que han marcado goles en más de un partido
     */
    public ArrayList<Jugador> listarJugadoresConGolesEnMasDeUnPartido() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarJugadoresConGolesEnMasDeUnPartido();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores goleadores: " + e.getMessage());
        }
    }

    /**
     * Consulta Avanzada 3: Jugadores sin contrato activo
     */
    public ArrayList<Jugador> listarJugadoresSinContratoActivo() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarJugadoresSinContratoActivo();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores sin contrato: " + e.getMessage());
        }
    }

    /**
     * Consulta Avanzada 5: Delanteros sin goles
     */
    public ArrayList<Jugador> listarDelanterosSinGoles() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarDelanterosSinGoles();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar delanteros sin goles: " + e.getMessage());
        }
    }

    /**
     * Consulta Avanzada 6: Jugadores con mayor salario por posición
     */
    public ArrayList<Jugador> listarJugadoresConMayorSalarioPorPosicion() throws JugadorNoEncontradoException {
        try {
            return jugadorRepository.listarJugadoresConMayorSalarioPorPosicion();
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al listar jugadores con mayor salario: " + e.getMessage());
        }
    }
}

