package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Gol;
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
}
