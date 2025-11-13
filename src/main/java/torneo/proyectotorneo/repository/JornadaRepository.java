package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.TipoTarjeta;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JornadaRepository implements Repository<Jornada> {

    @Override
    public ArrayList<Jornada> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM JORNADA ORDER BY JORNADA ASC";
        ArrayList<Jornada> jornadas = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jornada jornada = new Jornada();
                jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                jornada.setNumeroJornada(rs.getInt("JORNADA"));
                jornadas.add(jornada);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar las jornadas: " + e.getMessage());
        }

        return jornadas;
    }

    @Override
    public Jornada buscarPorId(int id) throws RepositoryException {
        Jornada jornada = null;
        String sql = "SELECT * FROM JORNADA WHERE ID_JORNADA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                jornada = new Jornada();
                jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                jornada.setNumeroJornada(rs.getInt("JORNADA"));
                jornada.setListaPartidos(listarPartidosPorJornada(id));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener jornada completa: " + e.getMessage());
        }

        return jornada;
    }

    @Override
    public void guardar(Jornada jornada) throws RepositoryException {
        String sql = "INSERT INTO JORNADA (JORNADA) VALUES (?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jornada.getNumeroJornada());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la jornada: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Jornada jornada) throws RepositoryException {
        String sql = "UPDATE JORNADA SET JORNADA = ? WHERE ID_JORNADA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jornada.getNumeroJornada());
            ps.setInt(2, jornada.getIdJornada());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar la jornada: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM JORNADA WHERE ID_JORNADA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la jornada: " + e.getMessage());
        }
    }

    /**
     * Método adicional para buscar jornada por número
     */
    public Jornada buscarPorNumero(int numeroJornada) throws RepositoryException {
        String sql = "SELECT * FROM JORNADA WHERE JORNADA = ?";
        Jornada jornada = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroJornada);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jornada = new Jornada();
                    jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                    jornada.setNumeroJornada(rs.getInt("JORNADA"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la jornada por número: " + e.getMessage());
        }

        return jornada;
    }

    /**
     * Método para obtener la última jornada registrada
     */
    public Jornada obtenerUltimaJornada() throws RepositoryException {
        String sql = "SELECT * FROM JORNADA ORDER BY JORNADA DESC FETCH FIRST 1 ROW ONLY";
        Jornada jornada = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                jornada = new Jornada();
                jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                jornada.setNumeroJornada(rs.getInt("JORNADA"));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener la última jornada: " + e.getMessage());
        }

        return jornada;
    }

    private ArrayList<Partido> listarPartidosPorJornada(int idJornada) throws SQLException {
        ArrayList<Partido> lista = new ArrayList<>();
        String sql = "SELECT * FROM PARTIDO WHERE ID_JORNADA = ? ORDER BY FECHA, HORA";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idJornada);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                partido.setFecha(rs.getDate("FECHA").toLocalDate());
                partido.setHora(rs.getString("HORA"));

                // Relaciones principales
                partido.setEquipoLocal(buscarEquipoBasico(rs.getInt("ID_EQUIPO_LOCAL")));
                partido.setEquipoVisitante(buscarEquipoBasico(rs.getInt("ID_EQUIPO_VISITANTE")));
                partido.setEstadio(buscarEstadioPorId(rs.getInt("ID_ESTADIO")));
                partido.setJornada(null); // Evitar recursión circular

                // Relaciones 1:N
                partido.setListaGoles(listarGolesPorPartido(partido.getIdPartido()));
                partido.setListaTarjetas(listarTarjetasPorPartido(partido.getIdPartido()));
                partido.setListaSustituciones(listarSustitucionesPorPartido(partido.getIdPartido()));
                partido.setListaArbitros(listarArbitrosPorPartido(partido.getIdPartido()));

                partido.setResultadoFinal(buscarResultadoFinalPorPartido(partido.getIdPartido()));

                lista.add(partido);
            }

        }
        return lista;
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


}
