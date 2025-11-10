package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.PartidoNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final EquipoRepository equipoRepository;
    private final EstadioRepository estadioRepository;
    private final JornadaRepository jornadaRepository;
    private final ResultadoFinalRepository resultadoFinalRepository;
    private final GolRepository golRepository;
    private final TarjetaRepository tarjetaRepository;
    private final SustitucionRepository sustitucionRepository;

    public PartidoService() {
        this.partidoRepository = new PartidoRepository();
        this.equipoRepository = new EquipoRepository();
        this.estadioRepository = new EstadioRepository();
        this.jornadaRepository = new JornadaRepository();
        this.resultadoFinalRepository = new ResultadoFinalRepository();
        this.golRepository = new GolRepository();
        this.tarjetaRepository = new TarjetaRepository();
        this.sustitucionRepository = new SustitucionRepository();
    }

    /**
     * Lista todos los partidos del campeonato
     */
    public ArrayList<Partido> listarTodosLosPartidos() throws RepositoryException {
        return partidoRepository.listarTodos();
    }

    /**
     * Busca un partido por su ID
     */
    public Partido buscarPartidoPorId(int id) throws PartidoNoEncontradoException {
        try {
            Partido partido = partidoRepository.buscarPorId(id);
            if (partido == null) {
                throw new PartidoNoEncontradoException("No se encontró el partido con ID: " + id);
            }
            return partido;
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al buscar el partido: " + e.getMessage());
        }
    }

    /**
     * Programa un nuevo partido
     * Validaciones:
     * - Los equipos deben ser diferentes
     * - La fecha debe ser válida
     * - El estadio debe existir
     */
    public void guardar(Partido partido) throws PartidoNoEncontradoException {
        validarPartido(partido);

        try {
            partidoRepository.guardar(partido);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al programar el partido: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un partido
     */
    public void actualizarPartido(Partido partido) throws PartidoNoEncontradoException {
        validarPartido(partido);

        if (partido.getIdPartido() == null) {
            throw new PartidoNoEncontradoException("El ID del partido es requerido para actualizar");
        }

        try {
            partidoRepository.actualizar(partido);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al actualizar el partido: " + e.getMessage());
        }
    }

    /**
     * Registra un gol en el partido
     */
    public void registrarGol(Gol gol) throws PartidoNoEncontradoException {
        if (gol == null || gol.getPartido() == null || gol.getJugador() == null) {
            throw new PartidoNoEncontradoException("Datos del gol incompletos");
        }

        try {
            golRepository.guardar(gol);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al registrar el gol: " + e.getMessage());
        }
    }

    /**
     * Registra una tarjeta en el partido
     */
    public void registrarTarjeta(Tarjeta tarjeta) throws PartidoNoEncontradoException {
        if (tarjeta == null || tarjeta.getPartido() == null || tarjeta.getJugador() == null) {
            throw new PartidoNoEncontradoException("Datos de la tarjeta incompletos");
        }

        try {
            tarjetaRepository.guardar(tarjeta);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al registrar la tarjeta: " + e.getMessage());
        }
    }

    /**
     * Registra una sustitución en el partido
     * Validación: El jugador que sale debe estar en el campo
     */
    public void registrarSustitucion(Sustitucion sustitucion) throws PartidoNoEncontradoException {
        if (sustitucion == null || sustitucion.getPartido() == null ||
                sustitucion.getJugadorEntra() == null || sustitucion.getJugadorSale() == null) {
            throw new PartidoNoEncontradoException("Datos de la sustitución incompletos");
        }

        if (sustitucion.getJugadorEntra().getId().equals(sustitucion.getJugadorSale().getId())) {
            throw new PartidoNoEncontradoException("El jugador que entra no puede ser el mismo que sale");
        }

        try {
            sustitucionRepository.guardar(sustitucion);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al registrar la sustitución: " + e.getMessage());
        }
    }

    /**
     * Registra el resultado final del partido
     */
    public void registrarResultadoFinal(ResultadoFinal resultado) throws PartidoNoEncontradoException {
        if (resultado == null || resultado.getPartido() == null) {
            throw new PartidoNoEncontradoException("Datos del resultado incompletos");
        }

        if (resultado.getGolesLocal() < 0 || resultado.getGolesVisitante() < 0) {
            throw new PartidoNoEncontradoException("Los goles no pueden ser negativos");
        }

        try {
            resultadoFinalRepository.guardar(resultado);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al registrar el resultado: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los partidos de un equipo específico
     */
    public ArrayList<Partido> obtenerPartidosPorEquipo(int idEquipo) throws PartidoNoEncontradoException {
        try {
            return partidoRepository.buscarPartidosPorEquipo(idEquipo);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al buscar partidos del equipo: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los partidos de una jornada específica
     */
    public ArrayList<Partido> obtenerPartidosPorJornada(int idJornada) throws PartidoNoEncontradoException {
        try {
            return partidoRepository.buscarPartidosPorJornada(idJornada);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al buscar partidos de la jornada: " + e.getMessage());
        }
    }

    /**
     * Obtiene el historial de partidos jugados en un estadio
     */
    public ArrayList<Partido> obtenerPartidosPorEstadio(int idEstadio) throws PartidoNoEncontradoException {
        try {
            return partidoRepository.buscarPartidosPorEstadio(idEstadio);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al buscar partidos del estadio: " + e.getMessage());
        }
    }

    /**
     * Elimina un partido
     */
    public void eliminarPartido(int id) throws PartidoNoEncontradoException {
        try {
            Partido partido = partidoRepository.buscarPorId(id);
            if (partido == null) {
                throw new PartidoNoEncontradoException("No se encontró el partido con ID: " + id);
            }

            partidoRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al eliminar el partido: " + e.getMessage());
        }
    }

    /**
     * Validaciones para un partido
     */
    private void validarPartido(Partido partido) throws PartidoNoEncontradoException {
        if (partido == null) {
            throw new PartidoNoEncontradoException("El partido no puede ser nulo");
        }

        if (partido.getEquipoLocal() == null || partido.getEquipoVisitante() == null) {
            throw new PartidoNoEncontradoException("Ambos equipos son obligatorios");
        }

        if (partido.getEquipoLocal().getId().equals(partido.getEquipoVisitante().getId())) {
            throw new PartidoNoEncontradoException("Un equipo no puede jugar contra sí mismo");
        }

        if (partido.getFecha() == null) {
            throw new PartidoNoEncontradoException("La fecha del partido es obligatoria");
        }

        if (partido.getFecha().isBefore(LocalDate.now())) {
            throw new PartidoNoEncontradoException("La fecha del partido no puede ser anterior a hoy");
        }

        if (partido.getHora() == null || partido.getHora().trim().isEmpty()) {
            throw new PartidoNoEncontradoException("La hora del partido es obligatoria");
        }

        if (partido.getEstadio() == null) {
            throw new PartidoNoEncontradoException("El estadio es obligatorio");
        }

        if (partido.getJornada() == null) {
            throw new PartidoNoEncontradoException("La jornada es obligatoria");
        }
    }
    /**
     * Consulta Intermedia 5: Lista partidos con equipos y estadio
     */
    public ArrayList<Partido> listarPartidosConEquiposYEstadio() throws PartidoNoEncontradoException {
        try {
            return partidoRepository.listarPartidosConEquiposYEstadio();
        } catch (RepositoryException e) {
            throw new PartidoNoEncontradoException("Error al listar partidos completos: " + e.getMessage());
        }
    }

}

