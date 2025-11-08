package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class TecnicoRepository implements Repository<Tecnico> {

    @Override
    public ArrayList<Tecnico> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM TECNICO";
        ArrayList<Tecnico> tecnicos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tecnico tecnico = new Tecnico();
                tecnico.setId(rs.getInt("ID_TECNICO"));
                tecnico.setNombre(rs.getString("NOMBRE"));
                tecnico.setApellido(rs.getString("APELLIDO"));
                tecnicos.add(tecnico);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los técnicos: " + e.getMessage());
        }

        return tecnicos;
    }

    @Override
    public Tecnico buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM TECNICO WHERE ID_TECNICO = ?";
        Tecnico tecnico = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tecnico = new Tecnico();
                    tecnico.setId(rs.getInt("ID_TECNICO"));
                    tecnico.setNombre(rs.getString("NOMBRE"));
                    tecnico.setApellido(rs.getString("APELLIDO"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el técnico por ID: " + e.getMessage());
        }

        return tecnico;
    }

    @Override
    public void guardar(Tecnico tecnico) throws RepositoryException {
        String sql = "INSERT INTO TECNICO (NOMBRE, APELLIDO, ID_EQUIPO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tecnico.getNombre());
            ps.setString(2, tecnico.getApellido());
            ps.setInt(3, tecnico.getEquipo().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el técnico: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Tecnico tecnico) throws RepositoryException {
        String sql = "UPDATE TECNICO SET NOMBRE = ?, APELLIDO = ?, ID_EQUIPO = ? WHERE ID_TECNICO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tecnico.getNombre());
            ps.setString(2, tecnico.getApellido());
            ps.setInt(3, tecnico.getEquipo().getId());
            ps.setInt(4, tecnico.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el técnico: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM TECNICO WHERE ID_TECNICO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el técnico: " + e.getMessage());
        }
    }

    /**
     * Método adicional para buscar técnico por equipo
     */
    public Tecnico buscarPorEquipo(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM TECNICO WHERE ID_EQUIPO = ?";
        Tecnico tecnico = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tecnico = new Tecnico();
                    tecnico.setId(rs.getInt("ID_TECNICO"));
                    tecnico.setNombre(rs.getString("NOMBRE"));
                    tecnico.setApellido(rs.getString("APELLIDO"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el técnico por equipo: " + e.getMessage());
        }

        return tecnico;
    }

    /**
     * Método para verificar si un equipo ya tiene un técnico asignado
     */
    public boolean equipoTieneTecnico(int idEquipo) throws RepositoryException {
        String sql = "SELECT COUNT(*) as total FROM TECNICO WHERE ID_EQUIPO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al verificar técnico del equipo: " + e.getMessage());
        }

        return false;
    }

    //avanzada 4
    public ArrayList<Tecnico> listarTecnicosSinEquipo() throws RepositoryException {
        String sql = "SELECT t.ID_TECNICO, t.NOMBRE, t.APELLIDO " +
                "FROM TECNICO t WHERE t.ID_EQUIPO NOT IN (" +
                "SELECT e.ID_EQUIPO FROM EQUIPO e)";
        ArrayList<Tecnico> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tecnico t = new Tecnico();
                t.setId(rs.getInt("ID_TECNICO"));
                t.setNombre(rs.getString("NOMBRE"));
                t.setApellido(rs.getString("APELLIDO"));
                lista.add(t);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarTecnicosSinEquipo: " + ex.getMessage());
        }
        return lista;
    }


}