package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class EstadioRepository implements Repository<Estadio> {

    @Override
    public ArrayList<Estadio> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM ESTADIO";
        ArrayList<Estadio> estadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estadio estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                estadio.setNombre(rs.getString("NOMBRE"));
                estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                estadios.add(estadio);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los estadios: " + e.getMessage());
        }

        return estadios;
    }

    @Override
    public Estadio buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM ESTADIO WHERE ID_ESTADIO = ?";
        Estadio estadio = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadio = new Estadio();
                    estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                    estadio.setNombre(rs.getString("NOMBRE"));
                    estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el estadio por ID: " + e.getMessage());
        }

        return estadio;
    }

    @Override
    public void guardar(Estadio estadio) throws RepositoryException {
        String sql = "INSERT INTO ESTADIO (NOMBRE, CAPACIDAD, ID_DEPARTAMENTO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getDepartamento().getIdDepartamento());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el estadio: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Estadio estadio) throws RepositoryException {
        String sql = "UPDATE ESTADIO SET NOMBRE = ?, CAPACIDAD = ?, ID_DEPARTAMENTO = ? WHERE ID_ESTADIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getDepartamento().getIdDepartamento());
            ps.setInt(4, estadio.getIdEstadio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el estadio: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM ESTADIO WHERE ID_ESTADIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el estadio: " + e.getMessage());
        }
    }

    /**
     * Método adicional para buscar estadios por departamento
     */
    public ArrayList<Estadio> buscarPorDepartamento(int idDepartamento) throws RepositoryException {
        String sql = "SELECT * FROM ESTADIO WHERE ID_DEPARTAMENTO = ?";
        ArrayList<Estadio> estadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estadio estadio = new Estadio();
                    estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                    estadio.setNombre(rs.getString("NOMBRE"));
                    estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                    estadios.add(estadio);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estadios por departamento: " + e.getMessage());
        }

        return estadios;
    }
}
