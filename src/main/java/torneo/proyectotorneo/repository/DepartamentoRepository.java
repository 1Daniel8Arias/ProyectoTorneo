package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Departamento;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DepartamentoRepository implements Repository<Departamento> {

    @Override
    public ArrayList<Departamento> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM DEPARTAMENTO";
        ArrayList<Departamento> departamentos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Departamento depto = new Departamento();
                depto.setIdDepartamento(rs.getInt("ID_DEPARTAMENTO"));
                depto.setNombre(rs.getString("NOMBRE"));
                departamentos.add(depto);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los departamentos: " + e.getMessage());
        }

        return departamentos;
    }

    @Override
    public Departamento buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM DEPARTAMENTO WHERE ID_DEPARTAMENTO = ?";
        Departamento depto = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    depto = new Departamento();
                    depto.setIdDepartamento(rs.getInt("ID_DEPARTAMENTO"));
                    depto.setNombre(rs.getString("NOMBRE"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el departamento por ID: " + e.getMessage());
        }

        return depto;
    }

    @Override
    public void guardar(Departamento depto) throws RepositoryException {
        String sql = "INSERT INTO DEPARTAMENTO (NOMBRE) VALUES (?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, depto.getNombre());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el departamento: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Departamento depto) throws RepositoryException {
        String sql = "UPDATE DEPARTAMENTO SET NOMBRE = ? WHERE ID_DEPARTAMENTO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, depto.getNombre());
            ps.setInt(2, depto.getIdDepartamento());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el departamento: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM DEPARTAMENTO WHERE ID_DEPARTAMENTO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el departamento: " + e.getMessage());
        }
    }
}