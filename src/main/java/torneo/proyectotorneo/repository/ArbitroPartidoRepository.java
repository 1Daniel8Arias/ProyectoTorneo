package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.model.ArbitroPartido;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArbitroPartidoRepository {

    public ArrayList<ArbitroPartido> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM ARBITRO_PARTIDO";
        ArrayList<ArbitroPartido> arbitrosPartidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ArbitroPartido ap = new ArbitroPartido();

                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));

                Arbitro arbitro = new Arbitro();
                arbitro.setIdArbitro(rs.getInt("ID_ARBITRO"));
                ap.setTipo(rs.getString("TIPO"));


                ap.setArbitro(arbitro);
                ap.setPartido(partido);

                arbitrosPartidos.add(ap);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar árbitros por partido: " + e.getMessage());
        }

        return arbitrosPartidos;
    }

    public ArrayList<ArbitroPartido> buscarPorPartido(int idPartido) throws RepositoryException {
        String sql = "SELECT * FROM ARBITRO_PARTIDO WHERE ID_PARTIDO = ?";
        ArrayList<ArbitroPartido> arbitrosPartidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPartido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ArbitroPartido ap = new ArbitroPartido();
                    ap.setTipo(rs.getString("TIPO"));
                    arbitrosPartidos.add(ap);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar árbitros del partido: " + e.getMessage());
        }

        return arbitrosPartidos;
    }

    public void guardar(ArbitroPartido arbitroPartido) throws RepositoryException {
        String sql = "INSERT INTO ARBITRO_PARTIDO (ID_PARTIDO, ID_ARBITRO, TIPO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, arbitroPartido.getPartido().getIdPartido());
            ps.setInt(2, arbitroPartido.getArbitro().getIdArbitro());
            ps.setString(3, arbitroPartido.getTipo());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar árbitro del partido: " + e.getMessage());
        }
    }

    public void eliminar(int idPartido, int idArbitro) throws RepositoryException {
        String sql = "DELETE FROM ARBITRO_PARTIDO WHERE ID_PARTIDO = ? AND ID_ARBITRO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPartido);
            ps.setInt(2, idArbitro);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar árbitro del partido: " + e.getMessage());
        }
    }

    public ArbitroPartido buscarPorPartidoYArbitro(int idPartido, int idArbitro) throws RepositoryException {
        String sql = """
        SELECT ap.*,
               a.NOMBRE AS NOMBRE_ARBITRO, a.APELLIDO AS APELLIDO_ARBITRO,
               p.FECHA, p.HORA,
               el.ID_EQUIPO AS ID_LOCAL, el.NOMBRE AS NOMBRE_LOCAL,
               ev.ID_EQUIPO AS ID_VISITANTE, ev.NOMBRE AS NOMBRE_VISITANTE
        FROM ARBITRO_PARTIDO ap
        JOIN ARBITRO a ON ap.ID_ARBITRO = a.ID_ARBITRO
        JOIN PARTIDO p ON ap.ID_PARTIDO = p.ID_PARTIDO
        JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO
        JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO
        WHERE ap.ID_PARTIDO = ? AND ap.ID_ARBITRO = ?
    """;
        ArbitroPartido arbitroPartido = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPartido);
            ps.setInt(2, idArbitro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    arbitroPartido = new ArbitroPartido();
                    arbitroPartido.setTipo(rs.getString("TIPO"));

                    // Cargar Arbitro
                    Arbitro arbitro = new Arbitro();
                    arbitro.setIdArbitro(idArbitro);
                    arbitro.setNombre(rs.getString("NOMBRE_ARBITRO"));
                    arbitro.setApellido(rs.getString("APELLIDO_ARBITRO"));
                    arbitroPartido.setArbitro(arbitro);

                    // Cargar Partido
                    Partido partido = new Partido();
                    partido.setIdPartido(idPartido);
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));

                    Equipo local = new Equipo();
                    local.setId(rs.getInt("ID_LOCAL"));
                    local.setNombre(rs.getString("NOMBRE_LOCAL"));
                    partido.setEquipoLocal(local);

                    Equipo visitante = new Equipo();
                    visitante.setId(rs.getInt("ID_VISITANTE"));
                    visitante.setNombre(rs.getString("NOMBRE_VISITANTE"));
                    partido.setEquipoVisitante(visitante);

                    arbitroPartido.setPartido(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar arbitro-partido: " + e.getMessage());
        }

        return arbitroPartido;
    }

}
