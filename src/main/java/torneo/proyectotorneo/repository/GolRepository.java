package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Gol;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GolRepository implements Repository<Gol> {

    @Override
    public ArrayList<Gol> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM GOL";
        ArrayList<Gol> goles = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Gol gol = new Gol();
                gol.setIdGol(rs.getInt("ID_GOL"));
                gol.setNumeroGoles(rs.getInt("NUMERO_GOLES"));
                goles.add(gol);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los goles: " + e.getMessage());
        }

        return goles;
    }

    @Override
    public Gol buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM GOL WHERE ID_GOL = ?";
        Gol gol = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    gol = new Gol();
                    gol.setIdGol(rs.getInt("ID_GOL"));
                    gol.setNumeroGoles(rs.getInt("NUMERO_GOLES"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el gol por ID: " + e.getMessage());
        }

        return gol;
    }

    @Override
    public void guardar(Gol gol) throws RepositoryException {
        String sql = "INSERT INTO GOL (NUMERO_GOLES, ID_PARTIDO, ID_JUGADOR) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gol.getNumeroGoles());
            ps.setInt(2, gol.getPartido().getIdPartido());
            ps.setInt(3, gol.getJugador().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el gol: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Gol gol) throws RepositoryException {
        String sql = "UPDATE GOL SET NUMERO_GOLES = ?, ID_PARTIDO = ?, ID_JUGADOR = ? WHERE ID_GOL = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gol.getNumeroGoles());
            ps.setInt(2, gol.getPartido().getIdPartido());
            ps.setInt(3, gol.getJugador().getId());
            ps.setInt(4, gol.getIdGol());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el gol: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM GOL WHERE ID_GOL = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el gol: " + e.getMessage());
        }
    }

    //consulta intermedia 6

    public ArrayList<Gol> listarGolesConJugadorYPartido() throws RepositoryException {
        String sql = "SELECT g.ID_GOL, g.NUMERO_GOLES, " +
                "j.ID_JUGADOR, j.NOMBRE AS J_NOMBRE, j.APELLIDO AS J_APELLIDO, " +
                "p.ID_PARTIDO, p.FECHA, p.HORA " +
                "FROM GOL g JOIN JUGADOR j ON g.ID_JUGADOR = j.ID_JUGADOR " +
                "JOIN PARTIDO p ON g.ID_PARTIDO = p.ID_PARTIDO";
        ArrayList<Gol> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Gol gol = new Gol();
                gol.setIdGol(rs.getInt("ID_GOL"));
                gol.setNumeroGoles(rs.getInt("NUMERO_GOLES"));

                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("J_NOMBRE"));
                j.setApellido(rs.getString("J_APELLIDO"));
                gol.setJugador(j);

                Partido p = new Partido();
                p.setIdPartido(rs.getInt("ID_PARTIDO"));
                p.setFecha(rs.getDate("FECHA").toLocalDate());
                p.setHora(rs.getString("HORA"));
                gol.setPartido(p);

                lista.add(gol);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarGolesConJugadorYPartido: " + ex.getMessage());
        }
        return lista;
    }

}
