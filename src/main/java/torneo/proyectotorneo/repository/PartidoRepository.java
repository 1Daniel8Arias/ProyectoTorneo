package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.TipoTarjeta;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class PartidoRepository implements Repository<Partido> {
    @Override
    public ArrayList<Partido> listarTodos() throws RepositoryException {
        String sql = """
            SELECT 
                p.ID_PARTIDO,
                p.FECHA,
                p.HORA,
                p.ID_JORNADA,
                j.JORNADA AS NUMERO_JORNADA,
                el.ID_EQUIPO AS ID_LOCAL,
                el.NOMBRE AS NOMBRE_LOCAL,
                ev.ID_EQUIPO AS ID_VISITANTE,
                ev.NOMBRE AS NOMBRE_VISITANTE,
                e.ID_ESTADIO,
                e.NOMBRE AS NOMBRE_ESTADIO,
                e.CAPACIDAD,
                rf.ID_RESULTADO_FINAL,
                rf.GOLES_LOCAL,
                rf.GOLES_VISITANTE
            FROM PARTIDO p
            INNER JOIN JORNADA j ON p.ID_JORNADA = j.ID_JORNADA
            INNER JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO
            INNER JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO
            INNER JOIN ESTADIO e ON p.ID_ESTADIO = e.ID_ESTADIO
            LEFT JOIN RESULTADO_FINAL rf ON p.ID_PARTIDO = rf.ID_PARTIDO
            ORDER BY p.FECHA DESC, p.HORA DESC
            """;

        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));

                // Manejo seguro de fecha y hora
                Date fechaSQL = rs.getDate("FECHA");
                if (fechaSQL != null) partido.setFecha(fechaSQL.toLocalDate());
                partido.setHora(rs.getString("HORA"));

                // Jornada
                Jornada jornada = new Jornada();
                jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                jornada.setNumeroJornada(rs.getInt("NUMERO_JORNADA"));
                partido.setJornada(jornada);

                // Equipo local
                Equipo local = new Equipo();
                local.setId(rs.getInt("ID_LOCAL"));
                local.setNombre(rs.getString("NOMBRE_LOCAL"));
                partido.setEquipoLocal(local);

                // Equipo visitante
                Equipo visitante = new Equipo();
                visitante.setId(rs.getInt("ID_VISITANTE"));
                visitante.setNombre(rs.getString("NOMBRE_VISITANTE"));
                partido.setEquipoVisitante(visitante);

                // Estadio
                Estadio estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                estadio.setNombre(rs.getString("NOMBRE_ESTADIO"));
                estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                partido.setEstadio(estadio);

                // Resultado final (puede ser null si no hay registro)
                if (rs.getObject("ID_RESULTADO_FINAL") != null) {
                    ResultadoFinal rf = new ResultadoFinal();
                    rf.setIdResultadoFinal(rs.getInt("ID_RESULTADO_FINAL"));
                    rf.setGolesLocal(rs.getInt("GOLES_LOCAL"));
                    rf.setGolesVisitante(rs.getInt("GOLES_VISITANTE"));
                    partido.setResultadoFinal(rf);
                }

                partidos.add(partido);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los partidos: " + e.getMessage());
        }

        return partidos;
    }


    @Override
    public Partido buscarPorId(int id) throws RepositoryException {
        Partido partido = null;
        String sql = "SELECT * FROM PARTIDO WHERE ID_PARTIDO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                partido.setFecha(rs.getDate("FECHA").toLocalDate());
                partido.setHora(rs.getString("HORA"));

                // Relaciones principales
                partido.setJornada(buscarJornadaPorId(rs.getInt("ID_JORNADA")));
                partido.setEquipoLocal(buscarEquipoBasico(rs.getInt("ID_EQUIPO_LOCAL")));
                partido.setEquipoVisitante(buscarEquipoBasico(rs.getInt("ID_EQUIPO_VISITANTE")));
                partido.setEstadio(buscarEstadioPorId(rs.getInt("ID_ESTADIO")));

                // Relaciones 1:N
                partido.setListaGoles(listarGolesPorPartido(id));
                partido.setListaTarjetas(listarTarjetasPorPartido(id));
                partido.setListaSustituciones(listarSustitucionesPorPartido(id));
                partido.setListaArbitros(listarArbitrosPorPartido(id));

                // Resultado final
                partido.setResultadoFinal(buscarResultadoFinalPorPartido(id));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener partido completo: " + e.getMessage());
        }

        return partido;
    }

    @Override
    public void guardar(Partido partido) throws RepositoryException {
        String sql = "INSERT INTO PARTIDO (FECHA, HORA, ID_JORNADA, ID_EQUIPO_LOCAL, ID_EQUIPO_VISITANTE, ID_ESTADIO) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(partido.getFecha()));
            ps.setString(2, partido.getHora());
            ps.setInt(3, partido.getJornada().getIdJornada());
            ps.setInt(4, partido.getEquipoLocal().getId());
            ps.setInt(5, partido.getEquipoVisitante().getId());
            ps.setInt(6, partido.getEstadio().getIdEstadio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el partido: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Partido partido) throws RepositoryException {
        String sql = "UPDATE PARTIDO SET FECHA = ?, HORA = ?, ID_JORNADA = ?, ID_EQUIPO_LOCAL = ?, ID_EQUIPO_VISITANTE = ?, ID_ESTADIO = ? WHERE ID_PARTIDO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(partido.getFecha()));
            ps.setString(2, partido.getHora());
            ps.setInt(3, partido.getJornada().getIdJornada());
            ps.setInt(4, partido.getEquipoLocal().getId());
            ps.setInt(5, partido.getEquipoVisitante().getId());
            ps.setInt(6, partido.getEstadio().getIdEstadio());
            ps.setInt(7, partido.getIdPartido());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el partido: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM PARTIDO WHERE ID_PARTIDO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el partido: " + e.getMessage());
        }
    }

    /**
     * Busca todos los partidos de un equipo (como local o visitante)
     */
    public ArrayList<Partido> buscarPartidosPorEquipo(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_EQUIPO_LOCAL = ? OR ID_EQUIPO_VISITANTE = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            ps.setInt(2, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos por equipo: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos donde el equipo juega como local
     */
    public ArrayList<Partido> buscarPartidosComoLocal(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_EQUIPO_LOCAL = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos como local: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos donde el equipo juega como visitante
     */
    public ArrayList<Partido> buscarPartidosComoVisitante(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_EQUIPO_VISITANTE = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos como visitante: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos de una jornada específica
     */
    public ArrayList<Partido> buscarPartidosPorJornada(int idJornada) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_JORNADA = ? ORDER BY FECHA, HORA";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idJornada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos por jornada: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos jugados en un estadio específico
     */
    public ArrayList<Partido> buscarPartidosPorEstadio(int idEstadio) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_ESTADIO = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstadio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos por estadio: " + e.getMessage());
        }

        return partidos;
    }

    //consulta intermedia 5

    public ArrayList<Partido> listarPartidosConEquiposYEstadio() throws RepositoryException {
        String sql = "SELECT p.ID_PARTIDO, p.FECHA, p.HORA, " +
                "el.ID_EQUIPO AS ID_LOCAL, el.NOMBRE AS LOCAL_NOMBRE, " +
                "ev.ID_EQUIPO AS ID_VISITANTE, ev.NOMBRE AS VISITANTE_NOMBRE, " +
                "s.ID_ESTADIO, s.NOMBRE AS ESTADIO_NOMBRE " +
                "FROM PARTIDO p " +
                "JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO " +
                "JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO " +
                "JOIN ESTADIO s ON p.ID_ESTADIO = s.ID_ESTADIO";
        ArrayList<Partido> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Partido p = new Partido();
                p.setIdPartido(rs.getInt("ID_PARTIDO"));
                p.setFecha(rs.getDate("FECHA").toLocalDate());
                p.setHora(rs.getString("HORA"));

                Equipo local = new Equipo();
                local.setId(rs.getInt("ID_LOCAL"));
                local.setNombre(rs.getString("LOCAL_NOMBRE"));
                p.setEquipoLocal(local);

                Equipo visitante = new Equipo();
                visitante.setId(rs.getInt("ID_VISITANTE"));
                visitante.setNombre(rs.getString("VISITANTE_NOMBRE"));
                p.setEquipoVisitante(visitante);

                Estadio s = new Estadio();
                s.setIdEstadio(rs.getInt("ID_ESTADIO"));
                s.setNombre(rs.getString("ESTADIO_NOMBRE"));
                p.setEstadio(s);

                lista.add(p);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarPartidosConEquiposYEstadio: " + ex.getMessage());
        }
        return lista;
    }

    private ArrayList<Gol> listarGolesPorPartido(int idPartido) throws SQLException {
        ArrayList<Gol> lista = new ArrayList<>();
        String sql = """
        SELECT g.*, j.NOMBRE, j.APELLIDO
        FROM GOL g
        JOIN JUGADOR j ON g.ID_JUGADOR = j.ID_JUGADOR
        WHERE g.ID_PARTIDO = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setId(rs.getInt("ID_JUGADOR"));
                jugador.setNombre(rs.getString("NOMBRE"));
                jugador.setApellido(rs.getString("APELLIDO"));

                Gol gol = new Gol();
                gol.setIdGol(rs.getInt("ID_GOL"));
                gol.setNumeroGoles(rs.getInt("NUMERO_GOLES"));
                gol.setJugador(jugador);
                lista.add(gol);
            }
        }
        return lista;
    }

    private ArrayList<Tarjeta> listarTarjetasPorPartido(int idPartido) throws SQLException {
        ArrayList<Tarjeta> lista = new ArrayList<>();
        String sql = """
        SELECT t.*, j.NOMBRE, j.APELLIDO
        FROM TARJETA t
        JOIN JUGADOR j ON t.ID_JUGADOR = j.ID_JUGADOR
        WHERE t.ID_PARTIDO = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setId(rs.getInt("ID_JUGADOR"));
                jugador.setNombre(rs.getString("NOMBRE"));
                jugador.setApellido(rs.getString("APELLIDO"));

                Tarjeta tarjeta = new Tarjeta();
                tarjeta.setIdTarjeta(rs.getInt("ID_TARJETA"));
                tarjeta.setTipo(TipoTarjeta.valueOf(rs.getString("TIPO")));
                tarjeta.setJugador(jugador);
                lista.add(tarjeta);
            }
        }
        return lista;
    }

    private ArrayList<Sustitucion> listarSustitucionesPorPartido(int idPartido) throws SQLException {
        ArrayList<Sustitucion> lista = new ArrayList<>();
        String sql = """
        SELECT s.*, 
               j1.NOMBRE AS NOMBRE_ENTRA, j1.APELLIDO AS APELLIDO_ENTRA,
               j2.NOMBRE AS NOMBRE_SALE, j2.APELLIDO AS APELLIDO_SALE
        FROM SUSTITUCION s
        JOIN JUGADOR j1 ON s.ID_JUGADOR_ENTRA = j1.ID_JUGADOR
        JOIN JUGADOR j2 ON s.ID_JUGADOR_SALE = j2.ID_JUGADOR
        WHERE s.ID_PARTIDO = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Jugador entra = new Jugador(rs.getInt("ID_JUGADOR_ENTRA"), rs.getString("NOMBRE_ENTRA"), rs.getString("APELLIDO_ENTRA"), null, null, null, null, null, null, null, null, null);
                Jugador sale = new Jugador(rs.getInt("ID_JUGADOR_SALE"), rs.getString("NOMBRE_SALE"), rs.getString("APELLIDO_SALE"), null, null, null, null, null, null, null, null, null);

                Sustitucion s = new Sustitucion();
                s.setIdSustitucion(rs.getInt("ID_SUSTITUCION"));
                s.setJugadorEntra(entra);
                s.setJugadorSale(sale);
                lista.add(s);
            }
        }
        return lista;
    }

    private ArrayList<ArbitroPartido> listarArbitrosPorPartido(int idPartido) throws SQLException {
        ArrayList<ArbitroPartido> lista = new ArrayList<>();
        String sql = """
        SELECT ap.*, a.NOMBRE, a.APELLIDO
        FROM ARBITRO_PARTIDO ap
        JOIN ARBITRO a ON ap.ID_ARBITRO = a.ID_ARBITRO
        WHERE ap.ID_PARTIDO = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Arbitro arbitro = new Arbitro();
                arbitro.setIdArbitro(rs.getInt("ID_ARBITRO"));
                arbitro.setNombre(rs.getString("NOMBRE"));
                arbitro.setApellido(rs.getString("APELLIDO"));

                ArbitroPartido ap = new ArbitroPartido();
                ap.setArbitro(arbitro);
                ap.setTipo(rs.getString("TIPO"));
                lista.add(ap);
            }
        }
        return lista;
    }

    private ResultadoFinal buscarResultadoFinalPorPartido(int idPartido) throws SQLException {
        ResultadoFinal rf = null;
        String sql = "SELECT * FROM RESULTADO_FINAL WHERE ID_PARTIDO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rf = new ResultadoFinal();
                rf.setIdResultadoFinal(rs.getInt("ID_RESULTADO_FINAL"));
                rf.setGolesLocal(rs.getInt("GOLES_LOCAL"));
                rf.setGolesVisitante(rs.getInt("GOLES_VISITANTE"));
            }
        }
        return rf;
    }
    private Estadio buscarEstadioPorId(int idEstadio) throws SQLException {
        Estadio estadio = null;
        String sql = "SELECT * FROM ESTADIO WHERE ID_ESTADIO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstadio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                estadio.setNombre(rs.getString("NOMBRE"));
                estadio.setCapacidad(rs.getInt("CAPACIDAD"));
            }
        }
        return estadio;
    }

    private Jornada buscarJornadaPorId(int idJornada) throws SQLException {
        Jornada jornada = null;
        String sql = "SELECT * FROM JORNADA WHERE ID_JORNADA = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJornada);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                jornada = new Jornada();
                jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                jornada.setNumeroJornada(rs.getInt("JORNADA"));
            }
        }
        return jornada;
    }
    private Equipo buscarEquipoBasico(int idEquipo) throws SQLException {
        Equipo equipo = null;
        String sql = "SELECT * FROM EQUIPO WHERE ID_EQUIPO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("NOMBRE"));
            }
        }
        return equipo;
    }


}