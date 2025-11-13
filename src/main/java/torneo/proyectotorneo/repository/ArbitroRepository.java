package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.model.ArbitroPartido;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArbitroRepository implements Repository<Arbitro> {
    @Override
    public ArrayList<Arbitro> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM ARBITRO";
        ArrayList<Arbitro> arbitros = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Arbitro arbitro = new Arbitro();
                arbitro.setIdArbitro(rs.getInt("ID_ARBITRO"));
                arbitro.setNombre(rs.getString("NOMBRE"));
                arbitro.setApellido(rs.getString("APELLIDO"));
                arbitros.add(arbitro);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los árbitros: " + e.getMessage());
        }

        return arbitros;
    }

    @Override
    public Arbitro buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM ARBITRO WHERE ID_ARBITRO = ?";
        Arbitro arbitro = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    arbitro = new Arbitro();
                    arbitro.setIdArbitro(rs.getInt("ID_ARBITRO"));
                    arbitro.setNombre(rs.getString("NOMBRE"));
                    arbitro.setApellido(rs.getString("APELLIDO"));

                    // Cargar la lista de partidos arbitrados
                    arbitro.setArbitrosPartidos(listarArbitrosPartidosPorArbitro(id));

                    // Calcular partidos arbitrados
                    arbitro.setPartidosArbitrados(arbitro.getArbitrosPartidos() != null ?
                            arbitro.getArbitrosPartidos().size() : 0);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el árbitro por ID: " + e.getMessage());
        }

        return arbitro;
    }


    @Override
    public void guardar(Arbitro arbitro) throws RepositoryException {
        String sql = "INSERT INTO ARBITRO (NOMBRE, APELLIDO) VALUES (?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, arbitro.getNombre());
            ps.setString(2, arbitro.getApellido());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el árbitro: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Arbitro arbitro) throws RepositoryException {
        String sql = "UPDATE ARBITRO SET NOMBRE = ?, APELLIDO = ? WHERE ID_ARBITRO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, arbitro.getNombre());
            ps.setString(2, arbitro.getApellido());
            ps.setInt(3, arbitro.getIdArbitro());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el árbitro: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM ARBITRO WHERE ID_ARBITRO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el árbitro: " + e.getMessage());
        }
    }

    //consulta avanzada 7

    public ArrayList<Arbitro> listarArbitrosConConteoDePartidos() throws RepositoryException {
        String sql = "SELECT a.ID_ARBITRO, a.NOMBRE, a.APELLIDO, " +
                "(SELECT COUNT(*) FROM ARBITRO_PARTIDO ap WHERE ap.ID_ARBITRO = a.ID_ARBITRO) AS PARTIDOS_ARBITRADOS " +
                "FROM ARBITRO a ORDER BY PARTIDOS_ARBITRADOS DESC";

        ArrayList<Arbitro> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Arbitro a = new Arbitro();
                a.setIdArbitro(rs.getInt("ID_ARBITRO"));
                a.setNombre(rs.getString("NOMBRE"));
                a.setApellido(rs.getString("APELLIDO"));
                a.setPartidosArbitrados(rs.getInt("PARTIDOS_ARBITRADOS"));
                lista.add(a);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarArbitrosConConteoDePartidos: " + ex.getMessage());
        }

        return lista;
    }

    private ArrayList<ArbitroPartido> listarArbitrosPartidosPorArbitro(int idArbitro) throws SQLException {
        ArrayList<ArbitroPartido> lista = new ArrayList<>();
        String sql = """
        SELECT ap.TIPO, 
               p.ID_PARTIDO, p.FECHA, p.HORA,
               el.ID_EQUIPO AS ID_LOCAL, el.NOMBRE AS NOMBRE_LOCAL,
               ev.ID_EQUIPO AS ID_VISITANTE, ev.NOMBRE AS NOMBRE_VISITANTE
        FROM ARBITRO_PARTIDO ap
        JOIN PARTIDO p ON ap.ID_PARTIDO = p.ID_PARTIDO
        JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO
        JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO
        WHERE ap.ID_ARBITRO = ?
    """;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idArbitro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ArbitroPartido ap = new ArbitroPartido();
                ap.setTipo(rs.getString("TIPO"));

                // Crear partido con información básica
                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                partido.setFecha(rs.getDate("FECHA").toLocalDate());
                partido.setHora(rs.getString("HORA"));

                // Equipos básicos
                Equipo local = new Equipo();
                local.setId(rs.getInt("ID_LOCAL"));
                local.setNombre(rs.getString("NOMBRE_LOCAL"));
                partido.setEquipoLocal(local);

                Equipo visitante = new Equipo();
                visitante.setId(rs.getInt("ID_VISITANTE"));
                visitante.setNombre(rs.getString("NOMBRE_VISITANTE"));
                partido.setEquipoVisitante(visitante);

                ap.setPartido(partido);
                lista.add(ap);
            }
        }

        return lista;
    }

}