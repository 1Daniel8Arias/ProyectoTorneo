package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jornada;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.repository.JornadaRepository;
import torneo.proyectotorneo.repository.PartidoRepository;

import java.util.ArrayList;

public class JornadaService {

    private final JornadaRepository jornadaRepository;
    private final PartidoRepository partidoRepository;

    public JornadaService() {
        this.jornadaRepository = new JornadaRepository();
        this.partidoRepository = new PartidoRepository();
    }

    /**
     * Lista todas las jornadas del campeonato
     */
    public ArrayList<Jornada> listarTodasLasJornadas() throws RepositoryException {
        return jornadaRepository.listarTodos();
    }

    /**
     * Busca una jornada por su ID
     */
    public Jornada buscarJornadaPorId(int id) throws RepositoryException {
        Jornada jornada = jornadaRepository.buscarPorId(id);
        if (jornada == null) {
            throw new RepositoryException("No se encontró la jornada con ID: " + id);
        }
        return jornada;
    }

    /**
     * Busca una jornada por su número
     */
    public Jornada buscarJornadaPorNumero(int numeroJornada) throws RepositoryException {
        Jornada jornada = jornadaRepository.buscarPorNumero(numeroJornada);
        if (jornada == null) {
            throw new RepositoryException("No se encontró la jornada número: " + numeroJornada);
        }
        return jornada;
    }

    /**
     * Crea una nueva jornada
     */
    public void crearJornada(int numeroJornada) throws RepositoryException {
        if (numeroJornada <= 0) {
            throw new RepositoryException("El número de jornada debe ser mayor a cero");
        }

        // Verificar si ya existe una jornada con ese número
        Jornada jornadaExistente = jornadaRepository.buscarPorNumero(numeroJornada);
        if (jornadaExistente != null) {
            throw new RepositoryException("Ya existe la jornada número " + numeroJornada);
        }

        Jornada jornada = new Jornada();
        jornada.setNumeroJornada(numeroJornada);
        jornada.setListaPartidos(new ArrayList<>());

        jornadaRepository.guardar(jornada);
    }

    /**
     * Actualiza una jornada
     */
    public void actualizarJornada(Jornada jornada) throws RepositoryException {
        if (jornada == null) {
            throw new RepositoryException("La jornada no puede ser nula");
        }

        if (jornada.getIdJornada() <= 0) {
            throw new RepositoryException("ID de jornada inválido");
        }

        if (jornada.getNumeroJornada() <= 0) {
            throw new RepositoryException("El número de jornada debe ser mayor a cero");
        }

        jornadaRepository.actualizar(jornada);
    }

    /**
     * Obtiene todos los partidos de una jornada
     */
    public ArrayList<Partido> obtenerPartidosDeJornada(int idJornada) throws RepositoryException {
        return partidoRepository.buscarPartidosPorJornada(idJornada);
    }

    /**
     * Obtiene la última jornada registrada
     */
    public Jornada obtenerUltimaJornada() throws RepositoryException {
        return jornadaRepository.obtenerUltimaJornada();
    }

    /**
     * Crea la siguiente jornada automáticamente
     */
    public Jornada crearSiguienteJornada() throws RepositoryException {
        Jornada ultimaJornada = jornadaRepository.obtenerUltimaJornada();

        int numeroSiguiente = 1;
        if (ultimaJornada != null) {
            numeroSiguiente = ultimaJornada.getNumeroJornada() + 1;
        }

        crearJornada(numeroSiguiente);
        return jornadaRepository.buscarPorNumero(numeroSiguiente);
    }

    /**
     * Obtiene información completa de una jornada
     */
    public String obtenerInformacionJornada(int idJornada) throws RepositoryException {
        Jornada jornada = jornadaRepository.buscarPorId(idJornada);
        if (jornada == null) {
            throw new RepositoryException("No se encontró la jornada");
        }

        ArrayList<Partido> partidos = partidoRepository.buscarPartidosPorJornada(idJornada);

        StringBuilder info = new StringBuilder();
        info.append("==== JORNADA ").append(jornada.getNumeroJornada()).append(" ====\n");
        info.append("Total de partidos: ").append(partidos.size()).append("\n\n");

        if (partidos.isEmpty()) {
            info.append("No hay partidos programados para esta jornada.\n");
        } else {
            info.append("Partidos:\n");
            for (Partido partido : partidos) {
                info.append(String.format("- %s vs %s (%s - %s)\n",
                        partido.getEquipoLocal() != null ? partido.getEquipoLocal().getNombre() : "TBD",
                        partido.getEquipoVisitante() != null ? partido.getEquipoVisitante().getNombre() : "TBD",
                        partido.getFecha(),
                        partido.getHora()
                ));
            }
        }

        return info.toString();
    }

    /**
     * Verifica si una jornada está completa (todos los partidos jugados)
     */
    public boolean jornadaCompleta(int idJornada) throws RepositoryException {
        ArrayList<Partido> partidos = partidoRepository.buscarPartidosPorJornada(idJornada);

        if (partidos.isEmpty()) {
            return false;
        }

        // Verificar que todos los partidos tengan resultado final
        for (Partido partido : partidos) {
            if (partido.getResultadoFinal() == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Elimina una jornada (solo si no tiene partidos asociados)
     */
    public void eliminarJornada(int id) throws RepositoryException {
        Jornada jornada = jornadaRepository.buscarPorId(id);
        if (jornada == null) {
            throw new RepositoryException("No se encontró la jornada con ID: " + id);
        }

        // Verificar que no tenga partidos
        ArrayList<Partido> partidos = partidoRepository.buscarPartidosPorJornada(id);
        if (!partidos.isEmpty()) {
            throw new RepositoryException(
                    "No se puede eliminar la jornada porque tiene partidos asociados"
            );
        }

        jornadaRepository.eliminar(id);
    }

    /**
     * Obtiene el número total de jornadas registradas
     */
    public int obtenerTotalJornadas() throws RepositoryException {
        return jornadaRepository.listarTodos().size();
    }

    /**
     * Obtiene resumen de todas las jornadas
     */
    public String obtenerResumenJornadas() throws RepositoryException {
        ArrayList<Jornada> jornadas = jornadaRepository.listarTodos();

        if (jornadas.isEmpty()) {
            return "No hay jornadas registradas";
        }

        StringBuilder resumen = new StringBuilder("==== RESUMEN DE JORNADAS ====\n\n");

        for (Jornada jornada : jornadas) {
            ArrayList<Partido> partidos = partidoRepository.buscarPartidosPorJornada(jornada.getIdJornada());
            int partidosJugados = 0;

            for (Partido p : partidos) {
                if (p.getResultadoFinal() != null) {
                    partidosJugados++;
                }
            }

            String estado = partidosJugados == partidos.size() ? "Completada" : "En progreso";

            resumen.append(String.format("Jornada %d: %d/%d partidos jugados - %s\n",
                    jornada.getNumeroJornada(),
                    partidosJugados,
                    partidos.size(),
                    estado
            ));
        }

        return resumen.toString();
    }
}