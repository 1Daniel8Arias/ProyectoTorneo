package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.exeptions.PartidoNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador que maneja la lógica de negocio entre la vista (ViewController)
 * y el ModelFactoryController para la gestión de partidos.
 */
public class PartidoController {

    private final ModelFactoryController modelFactoryController;

    public PartidoController() {
        this.modelFactoryController = ModelFactoryController.getInstance();
    }

    // ──────────────── CRUD BÁSICO ────────────────

    /**
     * Busca un partido por ID con TODOS sus datos completos
     */
    public Partido buscarPorId(int id) throws RepositoryException {
        try {
            return modelFactoryController.getTorneoService().buscarPartidoPorId(id);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * Lista todos los partidos
     */
    public ArrayList<Partido> listarTodos() throws RepositoryException {
        return modelFactoryController.getTorneoService().listarPartidos();
    }

    /**
     * Guarda un nuevo partido
     */
    public void guardarPartido(Partido partido) throws RepositoryException {
        modelFactoryController.getTorneoService().registrarPartido(partido);
    }

    /**
     * Actualiza un partido existente
     */
    public void actualizarPartido(Partido partido) throws RepositoryException {
        try {
            modelFactoryController.getTorneoService().actualizarPartido(partido);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * Elimina un partido
     */
    public void eliminarPartido(int id) throws RepositoryException {
        try {
            modelFactoryController.getTorneoService().eliminarPartido(id);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    // ──────────────── CONSULTAS POR FILTROS ────────────────

    /**
     * Lista partidos por equipo (local o visitante)
     */
    public ArrayList<Partido> listarPartidosPorEquipo(int idEquipo) throws RepositoryException {
        try {
            return modelFactoryController.getTorneoService().obtenerPartidosPorEquipo(idEquipo);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * Lista partidos por jornada
     */
    public ArrayList<Partido> listarPartidosPorJornada(int idJornada) throws RepositoryException {
        try {
            return modelFactoryController.getTorneoService().obtenerPartidosPorJornada(idJornada);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * Lista partidos por estadio
     */
    public ArrayList<Partido> listarPartidosPorEstadio(int idEstadio) throws RepositoryException {
        try {
            return modelFactoryController.getTorneoService().obtenerPartidosPorEstadio(idEstadio);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    /**
     * Lista partidos con equipos y estadio (consulta intermedia 5)
     */
    public ArrayList<Partido> listarPartidosConEquiposYEstadio() throws RepositoryException {
        try {
            return modelFactoryController.getTorneoService().listarPartidosConEquiposYEstadio();
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    // ──────────────── FILTROS AUXILIARES ────────────────

    /**
     * Filtra partidos por estado
     */
    public List<Partido> filtrarPorEstado(List<Partido> partidos, String estado) {
        LocalDate hoy = LocalDate.now();

        return partidos.stream().filter(p -> {
            switch (estado.toLowerCase()) {
                case "programados":
                    return p.getFecha().isAfter(hoy) && p.getResultadoFinal() == null;
                case "en curso":
                    return p.getFecha().equals(hoy) && p.getResultadoFinal() == null;
                case "finalizados":
                    return p.getResultadoFinal() != null;
                case "todos":
                default:
                    return true;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Filtra partidos por múltiples criterios
     */
    public List<Partido> filtrarPartidos(
            List<Partido> partidos,
            Integer idEquipo,
            String tipoEquipo,
            Integer idJornada,
            Integer idEstadio,
            String estado
    ) {
        return partidos.stream()
                .filter(p -> {
                    // Filtro por equipo
                    if (idEquipo != null) {
                        if ("local".equalsIgnoreCase(tipoEquipo)) {
                            if (p.getEquipoLocal() == null || !p.getEquipoLocal().getId().equals(idEquipo)) {
                                return false;
                            }
                        } else if ("visitante".equalsIgnoreCase(tipoEquipo)) {
                            if (p.getEquipoVisitante() == null || !p.getEquipoVisitante().getId().equals(idEquipo)) {
                                return false;
                            }
                        } else { // ambos
                            if ((p.getEquipoLocal() == null || !p.getEquipoLocal().getId().equals(idEquipo)) &&
                                    (p.getEquipoVisitante() == null || !p.getEquipoVisitante().getId().equals(idEquipo))) {
                                return false;
                            }
                        }
                    }

                    // Filtro por jornada


                    if (!Objects.equals(p.getJornada().getIdJornada(), idJornada)) {
                        return false;
                    }

                    // Filtro por estadio
                    if (idEstadio != null && (p.getEstadio() == null || !p.getEstadio().getIdEstadio().equals(idEstadio))) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el estado de un partido
     */
    public String obtenerEstadoPartido(Partido partido) {
        if (partido.getResultadoFinal() != null) {
            return "Finalizado";
        }

        LocalDate hoy = LocalDate.now();
        if (partido.getFecha().isBefore(hoy)) {
            return "En Curso";
        } else if (partido.getFecha().equals(hoy)) {
            return "Hoy";
        } else {
            return "Programado";
        }
    }

    // ──────────────── OBTENER LISTAS PARA FILTROS ────────────────

    /**
     * Obtiene nombres de equipos para el ComboBox
     */
    public List<String> obtenerNombresEquipos() {
        try {
            List<Equipo> equipos = modelFactoryController.obtenerEquipos();
            return equipos.stream().map(Equipo::getNombre).collect(Collectors.toList());
        } catch (RepositoryException e) {
            System.err.println("Error obteniendo equipos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene ID de equipo por nombre
     */
    public int obtenerIdEquipoPorNombre(String nombreEquipo) throws RepositoryException {
        return modelFactoryController.getTorneoService().obtenerIdEquipoPorNombre(nombreEquipo);
    }

    /**
     * Obtiene lista de jornadas disponibles
     */
    public List<String> obtenerJornadas() {
        try {
            ArrayList<Jornada> jornadas = modelFactoryController.getTorneoService().listarJornadas();
            return jornadas.stream()
                    .map(j -> "Jornada " + j.getNumeroJornada())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error obteniendo jornadas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene lista de estadios disponibles
     */
    public List<String> obtenerEstadios() {
        try {
            ArrayList<Estadio> estadios = modelFactoryController.getTorneoService().listarEstadios();
            return estadios.stream()
                    .map(Estadio::getNombre)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error obteniendo estadios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene el marcador de un partido
     */
    public String obtenerMarcador(Partido partido) {
        if (partido.getResultadoFinal() != null) {
            return partido.getResultadoFinal().getGolesLocal() + " - " +
                    partido.getResultadoFinal().getGolesVisitante();
        }
        return "vs";
    }

    /**
     * Obtiene el árbitro principal de un partido
     */
    public String obtenerArbitroPrincipal(Partido partido) {
        if (partido.getListaArbitros() != null && !partido.getListaArbitros().isEmpty()) {
            for (ArbitroPartido ap : partido.getListaArbitros()) {
                if ("Principal".equalsIgnoreCase(ap.getTipo())) {
                    Arbitro a = ap.getArbitro();
                    return a.getNombre() + " " + a.getApellido();
                }
            }
        }
        return "No asignado";
    }
}