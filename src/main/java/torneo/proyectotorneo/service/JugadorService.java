package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.JugadorNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.repository.*;

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

    /**
     * Registra un nuevo jugador
     * Validaciones:
     * - Nombre y apellido obligatorios
     * - Número de camiseta único en el equipo
     * - Posición válida
     */
    public void registrarJugador(Jugador jugador) throws JugadorNoEncontradoException {
        validarJugador(jugador);

        try {
            jugadorRepository.guardar(jugador);
        } catch (RepositoryException e) {
            throw new JugadorNoEncontradoException("Error al registrar el jugador: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un jugador
     */
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

    /**
     * Registra un contrato para un jugador
     * Validaciones:
     * - Fecha inicio debe ser anterior a fecha fin
     * - Salario debe ser positivo
     */
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

    /**
     * Aplica una sanción a un jugador
     * Las sanciones afectan la participación en partidos futuros
     */
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

    /**
     * Verifica si un jugador está sancionado en una fecha específica
     */
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

    /**
     * Obtiene todas las sanciones de un jugador
     */
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

    /**
     * Obtiene el historial de contratos de un jugador
     */
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

    /**
     * Obtiene el rendimiento de un jugador (goles, tarjetas, etc.)
     */
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

    /**
     * Elimina un jugador del sistema
     */
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

    /**
     * Validaciones para un jugador
     */
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
}

