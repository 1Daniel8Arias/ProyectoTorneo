package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.EquipoInvalidoException;
import torneo.proyectotorneo.exeptions.EquipoNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.repository.EquipoRepository;
import torneo.proyectotorneo.repository.JugadorRepository;
import torneo.proyectotorneo.repository.TecnicoRepository;

import java.util.ArrayList;

public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final JugadorRepository jugadorRepository;
    private final TecnicoRepository tecnicoRepository;

    public EquipoService() {
        this.equipoRepository = new EquipoRepository();
        this.jugadorRepository = new JugadorRepository();
        this.tecnicoRepository = new TecnicoRepository();
    }

    /**
     * Lista todos los equipos registrados
     */
    public ArrayList<Equipo> listarTodosLosEquipos() throws RepositoryException {
        return equipoRepository.listarTodos();
    }

    /**
     * Busca un equipo por su ID
     */
    public Equipo buscarEquipoPorId(int id) throws EquipoNoEncontradoException {
        try {
            Equipo equipo = equipoRepository.buscarPorId(id);
            if (equipo == null) {
                throw new EquipoNoEncontradoException("No se encontró el equipo con ID: " + id);
            }
            return equipo;
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al buscar el equipo: " + e.getMessage());
        }
    }
    public int obtenerIdPorNombre(String nombreEquipo) {
        try {
            int id=0;
             id = equipoRepository.buscarPorNombre(nombreEquipo);
            if (id == 0) {
                throw new EquipoNoEncontradoException("No se encontró el equipo con ID: " + id);
            }
            return id;
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al buscar el equipo: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo equipo
     * Validaciones: El nombre no puede estar vacío
     */
    public void registrarEquipo(Equipo equipo) throws EquipoInvalidoException {
        validarEquipo(equipo);

        try {
            equipoRepository.guardar(equipo);
        } catch (RepositoryException e) {
            throw new EquipoInvalidoException("Error al registrar el equipo: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un equipo
     */
    public void actualizarEquipo(Equipo equipo) throws EquipoInvalidoException {
        validarEquipo(equipo);

        if (equipo.getId() == null) {
            throw new EquipoInvalidoException("El ID del equipo es requerido para actualizar");
        }

        try {
            equipoRepository.actualizar(equipo);
        } catch (RepositoryException e) {
            throw new EquipoInvalidoException("Error al actualizar el equipo: " + e.getMessage());
        }
    }

    /**
     * Asigna un capitán al equipo
     * Validación: El jugador debe pertenecer al equipo
     */
    public void asignarCapitan(int idEquipo, int idJugador) throws EquipoInvalidoException {
        try {
            Equipo equipo = equipoRepository.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new EquipoInvalidoException("El equipo no existe");
            }

            Jugador jugador = jugadorRepository.buscarPorId(idJugador);
            if (jugador == null) {
                throw new EquipoInvalidoException("El jugador no existe");
            }

            if (jugador.getEquipo() == null || !jugador.getEquipo().getId().equals(idEquipo)) {
                throw new EquipoInvalidoException("El jugador debe pertenecer al equipo para ser capitán");
            }

            equipo.setCapitan(jugador);
            equipoRepository.actualizar(equipo);

        } catch (RepositoryException e) {
            throw new EquipoInvalidoException("Error al asignar el capitán: " + e.getMessage());
        }
    }

    /**
     * Cambia el director técnico de un equipo
     * Validación: Un equipo solo puede tener un técnico activo a la vez
     */
    public void cambiarDirectorTecnico(int idEquipo, Tecnico nuevoTecnico) throws EquipoInvalidoException {
        try {
            // Verificar si el equipo existe
            Equipo equipo = equipoRepository.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new EquipoInvalidoException("El equipo no existe");
            }

            // Verificar si el equipo ya tiene un técnico
            if (tecnicoRepository.equipoTieneTecnico(idEquipo)) {
                throw new EquipoInvalidoException(
                        "El equipo ya tiene un director técnico activo. Debe eliminarlo primero."
                );
            }

            // Asignar el nuevo técnico
            nuevoTecnico.setEquipo(equipo);
            tecnicoRepository.guardar(nuevoTecnico);

        } catch (RepositoryException e) {
            throw new EquipoInvalidoException("Error al cambiar el director técnico: " + e.getMessage());
        }
    }

    /**
     * Elimina un equipo del sistema
     */
    public void eliminarEquipo(int id) throws EquipoNoEncontradoException {
        try {
            Equipo equipo = equipoRepository.buscarPorId(id);
            if (equipo == null) {
                throw new EquipoNoEncontradoException("No se encontró el equipo con ID: " + id);
            }

            equipoRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al eliminar el equipo: " + e.getMessage());
        }
    }

    /**
     * Obtiene la plantilla de jugadores de un equipo
     */
    public ArrayList<Jugador> obtenerPlantillaJugadores(int idEquipo) throws EquipoNoEncontradoException {
        try {
            Equipo equipo = equipoRepository.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new EquipoNoEncontradoException("No se encontró el equipo con ID: " + idEquipo);
            }

            return equipo.getListaJugadoresJugadores();
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al obtener la plantilla: " + e.getMessage());
        }
    }

    /**
     * Validaciones generales para un equipo
     */
    private void validarEquipo(Equipo equipo) throws EquipoInvalidoException {
        if (equipo == null) {
            throw new EquipoInvalidoException("El equipo no puede ser nulo");
        }

        if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
            throw new EquipoInvalidoException("El nombre del equipo es obligatorio");
        }

        if (equipo.getNombre().length() > 100) {
            throw new EquipoInvalidoException("El nombre del equipo no puede exceder 100 caracteres");
        }
    }


    // ============================================================
    // CONSULTAS INTERMEDIAS
    // ============================================================

    /**
     * Consulta Intermedia 2: Lista equipos con su técnico
     */
    public ArrayList<Equipo> listarEquiposConTecnico() throws EquipoNoEncontradoException {
        try {
            return equipoRepository.listarEquiposConTecnico();
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al listar equipos con técnico: " + e.getMessage());
        }
    }

    /**
     * Consulta Intermedia 3: Lista equipos con cantidad de jugadores
     */
    public ArrayList<Equipo> listarEquiposConCantidadDeJugadores() throws EquipoNoEncontradoException {
        try {
            return equipoRepository.listarEquiposConCantidadDeJugadores();
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al listar equipos con cantidad de jugadores: " + e.getMessage());
        }
    }

    // ============================================================
    // CONSULTAS AVANZADAS
    // ============================================================

    /**
     * Consulta Avanzada 3: Lista equipos con jugadores sancionados
     */
    public ArrayList<Equipo> listarEquiposConSanciones() throws EquipoNoEncontradoException {
        try {
            return equipoRepository.listarEquiposConSanciones();
        } catch (RepositoryException e) {
            throw new EquipoNoEncontradoException("Error al listar equipos con sanciones: " + e.getMessage());
        }
    }


}

