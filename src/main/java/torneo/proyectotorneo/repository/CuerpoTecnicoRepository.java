package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.CuerpoTecnico;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CuerpoTecnicoRepository implements Repository<CuerpoTecnico> {

    @Override
    public ArrayList<CuerpoTecnico> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM CUERPO_TECNICO";
        ArrayList<CuerpoTecnico> cuerposTecnicos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CuerpoTecnico ct = new CuerpoTecnico();
                ct.setId(rs.getInt("ID_CUERPO_TECNICO"));
                ct.setNombre(rs.getString("NOMBRE"));
                ct.setApellido(rs.getString("APELLIDO"));
                ct.setEspecialidad(rs.getString("ESPECIALIDAD"));
                cuerposTecnicos.add(ct);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los cuerpos técnicos: " + e.getMessage());
        }

        return cuerposTecnicos;
    }

    @Override
    public CuerpoTecnico buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM CUERPO_TECNICO WHERE ID_CUERPO_TECNICO = ?";
        CuerpoTecnico ct = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ct = new CuerpoTecnico();
                    ct.setId(rs.getInt("ID_CUERPO_TECNICO"));
                    ct.setNombre(rs.getString("NOMBRE"));
                    ct.setApellido(rs.getString("APELLIDO"));
                    ct.setEspecialidad(rs.getString("ESPECIALIDAD"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el cuerpo técnico por ID: " + e.getMessage());
        }

        return ct;
    }

    @Override
    public void guardar(CuerpoTecnico ct) throws RepositoryException {
        String sql = "INSERT INTO CUERPO_TECNICO (NOMBRE, APELLIDO, ESPECIALIDAD, ID_EQUIPO) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ct.getNombre());
            ps.setString(2, ct.getApellido());
            ps.setString(3, ct.getEspecialidad());
            ps.setInt(4, ct.getEquipo().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el cuerpo técnico: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(CuerpoTecnico ct) throws RepositoryException {
        String sql = "UPDATE CUERPO_TECNICO SET NOMBRE = ?, APELLIDO = ?, ESPECIALIDAD = ?, ID_EQUIPO = ? WHERE ID_CUERPO_TECNICO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ct.getNombre());
            ps.setString(2, ct.getApellido());
            ps.setString(3, ct.getEspecialidad());
            ps.setInt(4, ct.getEquipo().getId());
            ps.setInt(5, ct.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el cuerpo técnico: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM CUERPO_TECNICO WHERE ID_CUERPO_TECNICO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el cuerpo técnico: " + e.getMessage());
        }
    }
}

