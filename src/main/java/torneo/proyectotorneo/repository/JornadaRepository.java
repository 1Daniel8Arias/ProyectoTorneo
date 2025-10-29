package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jornada;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
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
        String sql = "SELECT * FROM JORNADA WHERE ID_JORNADA = ?";
        Jornada jornada = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jornada = new Jornada();
                    jornada.setIdJornada(rs.getInt("ID_JORNADA"));
                    jornada.setNumeroJornada(rs.getInt("JORNADA"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la jornada por ID: " + e.getMessage());
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
}
