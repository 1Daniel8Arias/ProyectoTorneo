package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.model.ArbitroPartido;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.repository.ArbitroPartidoRepository;
import torneo.proyectotorneo.repository.ArbitroRepository;
import torneo.proyectotorneo.repository.PartidoRepository;

import java.util.ArrayList;

public class ArbitroService {

    private final ArbitroRepository arbitroRepository;
    private final ArbitroPartidoRepository arbitroPartidoRepository;
    private final PartidoRepository partidoRepository;

    public ArbitroService() {
        this.arbitroRepository = new ArbitroRepository();
        this.arbitroPartidoRepository = new ArbitroPartidoRepository();
        this.partidoRepository = new PartidoRepository();
    }

    /**
     * Lista todos los árbitros registrados
     */
    public ArrayList<Arbitro> listarTodosLosArbitros() throws RepositoryException {
        return arbitroRepository.listarTodos();
    }

    /**
     * Busca un árbitro por su ID
     */
    public Arbitro buscarArbitroPorId(int id) throws RepositoryException {
        Arbitro arbitro = arbitroRepository.buscarPorId(id);
        if (arbitro == null) {
            throw new RepositoryException("No se encontró el árbitro con ID: " + id);
        }
        return arbitro;
    }

    /**
     * Registra un nuevo árbitro
     */
    public void registrarArbitro(Arbitro arbitro) throws RepositoryException {
        validarArbitro(arbitro);
        arbitroRepository.guardar(arbitro);
    }

    /**
     * Actualiza la información de un árbitro
     */
    public void actualizarArbitro(Arbitro arbitro) throws RepositoryException {
        validarArbitro(arbitro);

        if (arbitro.getIdArbitro() == null) {
            throw new RepositoryException("El ID del árbitro es requerido para actualizar");
        }

        arbitroRepository.actualizar(arbitro);
    }

    /**
     * Asigna un árbitro a un partido
     * Validación: Un árbitro no puede dirigir dos partidos al mismo tiempo
     */
    public void asignarArbitroAPartido(int idArbitro, int idPartido, String tipo) throws RepositoryException {
        // Validar que el árbitro existe
        Arbitro arbitro = arbitroRepository.buscarPorId(idArbitro);
        if (arbitro == null) {
            throw new RepositoryException("El árbitro no existe");
        }

        // Validar que el partido existe
        Partido partido = partidoRepository.buscarPorId(idPartido);
        if (partido == null) {
            throw new RepositoryException("El partido no existe");
        }

        // Validar que el tipo es válido
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new RepositoryException("El tipo de árbitro es obligatorio");
        }

        if (!tipo.equals("Principal") && !tipo.equals("Asistente") && !tipo.equals("Cuarto")) {
            throw new RepositoryException("Tipo de árbitro inválido. Debe ser: Principal, Asistente o Cuarto");
        }

        // Verificar que el árbitro no esté asignado a otro partido a la misma hora
        if (tieneConflictoHorario(idArbitro, partido)) {
            throw new RepositoryException(
                    "El árbitro ya está asignado a otro partido en la misma fecha y hora"
            );
        }

        // Crear la asignación
        ArbitroPartido arbitroPartido = new ArbitroPartido();
        arbitroPartido.setArbitro(arbitro);
        arbitroPartido.setPartido(partido);
        arbitroPartido.setTipo(tipo);

        arbitroPartidoRepository.guardar(arbitroPartido);
    }

    /**
     * Verifica si un árbitro tiene conflicto de horario
     * Un árbitro no puede dirigir dos partidos al mismo tiempo
     */
    private boolean tieneConflictoHorario(int idArbitro, Partido nuevoPartido) throws RepositoryException {
        ArrayList<ArbitroPartido> asignaciones = arbitroPartidoRepository.listarTodos();

        for (ArbitroPartido ap : asignaciones) {
            if (ap.getArbitro().getIdArbitro().equals(idArbitro)) {
                Partido partidoExistente = ap.getPartido();

                // Verificar si es la misma fecha y hora
                if (partidoExistente.getFecha().equals(nuevoPartido.getFecha()) &&
                        partidoExistente.getHora().equals(nuevoPartido.getHora())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Obtiene todos los árbitros asignados a un partido
     */
    public ArrayList<ArbitroPartido> obtenerArbitrosDelPartido(int idPartido) throws RepositoryException {
        return arbitroPartidoRepository.buscarPorPartido(idPartido);
    }

    /**
     * Obtiene el historial de partidos dirigidos por un árbitro
     */
    public ArrayList<Partido> obtenerPartidosDirigidos(int idArbitro) throws RepositoryException {
        ArrayList<ArbitroPartido> asignaciones = arbitroPartidoRepository.listarTodos();
        ArrayList<Partido> partidos = new ArrayList<>();

        for (ArbitroPartido ap : asignaciones) {
            if (ap.getArbitro().getIdArbitro().equals(idArbitro)) {
                partidos.add(ap.getPartido());
            }
        }

        return partidos;
    }

    /**
     * Obtiene estadísticas de un árbitro
     */
    public String obtenerEstadisticasArbitro(int idArbitro) throws RepositoryException {
        Arbitro arbitro = arbitroRepository.buscarPorId(idArbitro);
        if (arbitro == null) {
            throw new RepositoryException("No se encontró el árbitro");
        }

        ArrayList<Partido> partidosDirigidos = obtenerPartidosDirigidos(idArbitro);

        return String.format(
                "Árbitro: %s %s\n" +
                        "Partidos Dirigidos: %d\n",
                arbitro.getNombre(),
                arbitro.getApellido(),
                partidosDirigidos.size()
        );
    }

    /**
     * Elimina la asignación de un árbitro a un partido
     */
    public void removerArbitroDePartido(int idPartido, int idArbitro) throws RepositoryException {
        arbitroPartidoRepository.eliminar(idPartido, idArbitro);
    }

    /**
     * Elimina un árbitro del sistema
     */
    public void eliminarArbitro(int id) throws RepositoryException {
        Arbitro arbitro = arbitroRepository.buscarPorId(id);
        if (arbitro == null) {
            throw new RepositoryException("No se encontró el árbitro con ID: " + id);
        }

        arbitroRepository.eliminar(id);
    }

    /**
     * Verifica si un partido tiene todos los árbitros necesarios
     */
    public boolean partidoTieneArbitrosCompletos(int idPartido) throws RepositoryException {
        ArrayList<ArbitroPartido> arbitros = arbitroPartidoRepository.buscarPorPartido(idPartido);

        boolean tienePrincipal = false;
        int cantidadAsistentes = 0;

        for (ArbitroPartido ap : arbitros) {
            if (ap.getTipo().equals("Principal")) {
                tienePrincipal = true;
            } else if (ap.getTipo().equals("Asistente")) {
                cantidadAsistentes++;
            }
        }

        // Un partido completo debe tener: 1 principal, 2 asistentes (mínimo)
        return tienePrincipal && cantidadAsistentes >= 2;
    }

    /**
     * Validaciones para un árbitro
     */
    private void validarArbitro(Arbitro arbitro) throws RepositoryException {
        if (arbitro == null) {
            throw new RepositoryException("El árbitro no puede ser nulo");
        }

        if (arbitro.getNombre() == null || arbitro.getNombre().trim().isEmpty()) {
            throw new RepositoryException("El nombre del árbitro es obligatorio");
        }

        if (arbitro.getApellido() == null || arbitro.getApellido().trim().isEmpty()) {
            throw new RepositoryException("El apellido del árbitro es obligatorio");
        }
    }

    // ============================================================
    // CONSULTAS AVANZADAS
    // ============================================================

    /**
     * Consulta Avanzada 7: Lista árbitros con conteo de partidos arbitrados
     */
    public ArrayList<Arbitro> listarArbitrosConConteoDePartidos() throws RepositoryException {
        return arbitroRepository.listarArbitrosConConteoDePartidos();
    }

}

