package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.ResultadoFinal;
import torneo.proyectotorneo.model.TablaPosicion;
import torneo.proyectotorneo.repository.EquipoRepository;
import torneo.proyectotorneo.repository.ResultadoFinalRepository;
import torneo.proyectotorneo.repository.TablaPosicionRepository;

import java.util.ArrayList;
import java.util.Comparator;

public class TablaPosicionService {

    private final TablaPosicionRepository tablaPosicionRepository;
    private final EquipoRepository equipoRepository;
    private final ResultadoFinalRepository resultadoFinalRepository;

    public TablaPosicionService() {
        this.tablaPosicionRepository = new TablaPosicionRepository();
        this.equipoRepository = new EquipoRepository();
        this.resultadoFinalRepository = new ResultadoFinalRepository();
    }

    /**
     * Obtiene la tabla de posiciones ordenada
     * Criterios: Puntos, Diferencia de goles, Goles a favor
     */
    public ArrayList<TablaPosicion> obtenerTablaPosiciones() throws RepositoryException {
        ArrayList<TablaPosicion> tabla = tablaPosicionRepository.listarTodos();

        // Ordenar por: 1) Puntos DESC, 2) Diferencia de goles DESC, 3) Goles a favor DESC
        tabla.sort(Comparator
                .comparingInt(TablaPosicion::getPuntos).reversed()
                .thenComparingInt(TablaPosicion::getDiferenciaGoles).reversed()
                .thenComparingInt(TablaPosicion::getGolesAFavor).reversed()
        );

        return tabla;
    }

    /**
     * Inicializa la tabla de posiciones para un equipo
     */
    public void inicializarPosicionEquipo(Equipo equipo) throws RepositoryException {
        if (equipo == null || equipo.getId() == null) {
            throw new RepositoryException("El equipo no puede ser nulo");
        }

        TablaPosicion posicion = new TablaPosicion();
        posicion.setEquipo(equipo);
        posicion.setGanados(0);
        posicion.setEmpates(0);
        posicion.setPerdidos(0);
        posicion.setGolesAFavor(0);
        posicion.setGolesEnContra(0);
        posicion.setDiferenciaGoles(0);
        posicion.setPuntos(0);

        tablaPosicionRepository.guardar(posicion);
    }

    /**
     * Actualiza la tabla de posiciones después de un partido
     * Victoria = 3 puntos, Empate = 1 punto, Derrota = 0 puntos
     */
    public void actualizarTrasPartido(ResultadoFinal resultado) throws RepositoryException {
        if (resultado == null || resultado.getPartido() == null) {
            throw new RepositoryException("El resultado del partido no puede ser nulo");
        }

        int golesLocal = resultado.getGolesLocal();
        int golesVisitante = resultado.getGolesVisitante();

        Equipo equipoLocal = resultado.getPartido().getEquipoLocal();
        Equipo equipoVisitante = resultado.getPartido().getEquipoVisitante();

        // Obtener posiciones actuales
        TablaPosicion posicionLocal = obtenerPosicionPorEquipo(equipoLocal.getId());
        TablaPosicion posicionVisitante = obtenerPosicionPorEquipo(equipoVisitante.getId());

        if (posicionLocal == null || posicionVisitante == null) {
            throw new RepositoryException("No se encontraron las posiciones de los equipos");
        }

        // Actualizar goles
        posicionLocal.setGolesAFavor(posicionLocal.getGolesAFavor() + golesLocal);
        posicionLocal.setGolesEnContra(posicionLocal.getGolesEnContra() + golesVisitante);

        posicionVisitante.setGolesAFavor(posicionVisitante.getGolesAFavor() + golesVisitante);
        posicionVisitante.setGolesEnContra(posicionVisitante.getGolesEnContra() + golesLocal);

        // Determinar resultado y actualizar puntos
        if (golesLocal > golesVisitante) {
            // Victoria del local
            posicionLocal.setGanados(posicionLocal.getGanados() + 1);
            posicionLocal.setPuntos(posicionLocal.getPuntos() + 3);
            posicionVisitante.setPerdidos(posicionVisitante.getPerdidos() + 1);
        } else if (golesLocal < golesVisitante) {
            // Victoria del visitante
            posicionVisitante.setGanados(posicionVisitante.getGanados() + 1);
            posicionVisitante.setPuntos(posicionVisitante.getPuntos() + 3);
            posicionLocal.setPerdidos(posicionLocal.getPerdidos() + 1);
        } else {
            // Empate
            posicionLocal.setEmpates(posicionLocal.getEmpates() + 1);
            posicionLocal.setPuntos(posicionLocal.getPuntos() + 1);
            posicionVisitante.setEmpates(posicionVisitante.getEmpates() + 1);
            posicionVisitante.setPuntos(posicionVisitante.getPuntos() + 1);
        }

        // Actualizar diferencia de goles
        posicionLocal.setDiferenciaGoles(
                posicionLocal.getGolesAFavor() - posicionLocal.getGolesEnContra()
        );
        posicionVisitante.setDiferenciaGoles(
                posicionVisitante.getGolesAFavor() - posicionVisitante.getGolesEnContra()
        );

        // Guardar cambios
        tablaPosicionRepository.actualizar(posicionLocal);
        tablaPosicionRepository.actualizar(posicionVisitante);
    }

    /**
     * Obtiene la posición de un equipo específico
     */
    public TablaPosicion obtenerPosicionPorEquipo(int idEquipo) throws RepositoryException {
        ArrayList<TablaPosicion> tabla = tablaPosicionRepository.listarTodos();

        for (TablaPosicion posicion : tabla) {
            if (posicion.getEquipo() != null && posicion.getEquipo().getId().equals(idEquipo)) {
                return posicion;
            }
        }

        return null;
    }

    /**
     * Obtiene el ranking de un equipo en la tabla
     */
    public int obtenerRankingEquipo(int idEquipo) throws RepositoryException {
        ArrayList<TablaPosicion> tabla = obtenerTablaPosiciones();

        for (int i = 0; i < tabla.size(); i++) {
            if (tabla.get(i).getEquipo() != null && tabla.get(i).getEquipo().getId().equals(idEquipo)) {
                return i + 1; // Posición 1-indexed
            }
        }

        return -1; // No encontrado
    }

    /**
     * Obtiene las estadísticas completas de un equipo
     */
    public String obtenerEstadisticasEquipo(int idEquipo) throws RepositoryException {
        TablaPosicion posicion = obtenerPosicionPorEquipo(idEquipo);

        if (posicion == null) {
            return "No se encontraron estadísticas para el equipo";
        }

        int partidosJugados = posicion.getGanados() + posicion.getEmpates() + posicion.getPerdidos();
        int ranking = obtenerRankingEquipo(idEquipo);

        return String.format(
                "Equipo: %s\n" +
                        "Posición: %d\n" +
                        "Puntos: %d\n" +
                        "Partidos Jugados: %d\n" +
                        "Ganados: %d\n" +
                        "Empates: %d\n" +
                        "Perdidos: %d\n" +
                        "Goles a Favor: %d\n" +
                        "Goles en Contra: %d\n" +
                        "Diferencia de Goles: %d",
                posicion.getEquipo().getNombre(),
                ranking,
                posicion.getPuntos(),
                partidosJugados,
                posicion.getGanados(),
                posicion.getEmpates(),
                posicion.getPerdidos(),
                posicion.getGolesAFavor(),
                posicion.getGolesEnContra(),
                posicion.getDiferenciaGoles()
        );
    }

    /**
     * Resetea la tabla de posiciones (útil para nueva temporada)
     */
    public void resetearTabla() throws RepositoryException {
        ArrayList<TablaPosicion> tabla = tablaPosicionRepository.listarTodos();

        for (TablaPosicion posicion : tabla) {
            posicion.setGanados(0);
            posicion.setEmpates(0);
            posicion.setPerdidos(0);
            posicion.setGolesAFavor(0);
            posicion.setGolesEnContra(0);
            posicion.setDiferenciaGoles(0);
            posicion.setPuntos(0);

            tablaPosicionRepository.actualizar(posicion);
        }
    }

    /**
     * Obtiene los mejores goleadores del torneo
     */
    public String obtenerMejoresGoleadores() throws RepositoryException {
        ArrayList<TablaPosicion> tabla = obtenerTablaPosiciones();

        tabla.sort(Comparator.comparingInt(TablaPosicion::getGolesAFavor).reversed());

        StringBuilder resultado = new StringBuilder("Top 5 Mejores Ofensivas:\n");
        int limite = Math.min(5, tabla.size());

        for (int i = 0; i < limite; i++) {
            TablaPosicion pos = tabla.get(i);
            resultado.append(String.format("%d. %s - %d goles\n",
                    i + 1,
                    pos.getEquipo().getNombre(),
                    pos.getGolesAFavor()
            ));
        }

        return resultado.toString();
    }

    /**
     * Obtiene las mejores defensas del torneo
     */
    public String obtenerMejoresDefensas() throws RepositoryException {
        ArrayList<TablaPosicion> tabla = tablaPosicionRepository.listarTodos();

        tabla.sort(Comparator.comparingInt(TablaPosicion::getGolesEnContra));

        StringBuilder resultado = new StringBuilder("Top 5 Mejores Defensas:\n");
        int limite = Math.min(5, tabla.size());

        for (int i = 0; i < limite; i++) {
            TablaPosicion pos = tabla.get(i);
            resultado.append(String.format("%d. %s - %d goles en contra\n",
                    i + 1,
                    pos.getEquipo().getNombre(),
                    pos.getGolesEnContra()
            ));
        }

        return resultado.toString();
    }
}