package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Municipio;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class MunicipioRepository implements Repository<Municipio> {

    @Override
    public ArrayList<Municipio> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM MUNICIPIO";
        ArrayList<Municipio> municipios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Municipio mun = new Municipio();
                mun.setIdMunicipio(rs.getInt("ID_MUNICIPIO"));
                mun.setNombre(rs.getString("NOMBRE"));
                municipios.add(mun);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los municipios: " + e.getMessage());
        }

        return municipios;
    }

    @Override
    public Municipio buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM MUNICIPIO WHERE ID_MUNICIPIO = ?";
        Municipio mun = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mun = new Municipio();
                    mun.setIdMunicipio(rs.getInt("ID_MUNICIPIO"));
                    mun.setNombre(rs.getString("NOMBRE"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el municipio por ID: " + e.getMessage());
        }

        return mun;
    }

    @Override
    public void guardar(Municipio mun) throws RepositoryException {
        String sql = "INSERT INTO MUNICIPIO (NOMBRE, ID_DEPARTAMENTO) VALUES (?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mun.getNombre());
            ps.setInt(2, mun.getDepartamento().getIdDepartamento());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el municipio: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Municipio mun) throws RepositoryException {
        String sql = "UPDATE MUNICIPIO SET NOMBRE = ?, ID_DEPARTAMENTO = ? WHERE ID_MUNICIPIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mun.getNombre());
            ps.setInt(2, mun.getDepartamento().getIdDepartamento());
            ps.setInt(3, mun.getIdMunicipio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el municipio: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM MUNICIPIO WHERE ID_MUNICIPIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el municipio: " + e.getMessage());
        }
    }
}
