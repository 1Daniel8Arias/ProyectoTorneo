package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Ciudad;
import torneo.proyectotorneo.model.Departamento;
import torneo.proyectotorneo.model.Municipio;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CiudadRepository implements Repository<Ciudad> {

    @Override
    public ArrayList<Ciudad> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM CIUDAD";
        ArrayList<Ciudad> ciudades = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ciudad ciudad = new Ciudad();
                ciudad.setIdCiudad(rs.getInt("ID_CIUDAD"));
                ciudad.setNombre(rs.getString("NOMBRE"));
                ciudades.add(ciudad);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar las ciudades: " + e.getMessage());
        }

        return ciudades;
    }

    @Override
    public Ciudad buscarPorId(int id) throws RepositoryException {
        String sql = """
        SELECT c.*, m.ID_MUNICIPIO, m.NOMBRE AS NOMBRE_MUNICIPIO,
               d.ID_DEPARTAMENTO, d.NOMBRE AS NOMBRE_DEPARTAMENTO
        FROM CIUDAD c
        JOIN MUNICIPIO m ON c.ID_MUNICIPIO = m.ID_MUNICIPIO
        JOIN DEPARTAMENTO d ON m.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO
        WHERE c.ID_CIUDAD = ?
    """;
        Ciudad ciudad = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ciudad = new Ciudad();
                    ciudad.setIdCiudad(rs.getInt("ID_CIUDAD"));
                    ciudad.setNombre(rs.getString("NOMBRE"));

                    // Cargar Municipio con su Departamento
                    Municipio municipio = new Municipio();
                    municipio.setIdMunicipio(rs.getInt("ID_MUNICIPIO"));
                    municipio.setNombre(rs.getString("NOMBRE_MUNICIPIO"));

                    Departamento departamento = new Departamento();
                    departamento.setIdDepartamento(rs.getInt("ID_DEPARTAMENTO"));
                    departamento.setNombre(rs.getString("NOMBRE_DEPARTAMENTO"));

                    municipio.setDepartamento(departamento);
                    ciudad.setMunicipio(municipio);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la ciudad por ID: " + e.getMessage());
        }

        return ciudad;
    }

    @Override
    public void guardar(Ciudad ciudad) throws RepositoryException {
        String sql = "INSERT INTO CIUDAD (NOMBRE, ID_MUNICIPIO) VALUES (?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ciudad.getNombre());
            ps.setInt(2, ciudad.getMunicipio().getIdMunicipio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la ciudad: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Ciudad ciudad) throws RepositoryException {
        String sql = "UPDATE CIUDAD SET NOMBRE = ?, ID_MUNICIPIO = ? WHERE ID_CIUDAD = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ciudad.getNombre());
            ps.setInt(2, ciudad.getMunicipio().getIdMunicipio());
            ps.setInt(3, ciudad.getIdCiudad());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar la ciudad: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM CIUDAD WHERE ID_CIUDAD = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la ciudad: " + e.getMessage());
        }
    }
}

