package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class SancionRepository implements Repository<Sancion> {

    @Override
    public ArrayList<Sancion> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM SANCION";
        ArrayList<Sancion> sanciones = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sancion sancion = new Sancion();
                sancion.setIdSancion(rs.getInt("ID_SANCION"));
                sancion.setFecha(rs.getDate("FECHA").toLocalDate());
                sancion.setMotivo(rs.getString("MOTIVO"));
                sancion.setDuracion(rs.getInt("DURACION"));
                sancion.setTipo(rs.getString("TIPO"));
                sanciones.add(sancion);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar las sanciones: " + e.getMessage());
        }

        return sanciones;
    }

    @Override
    public Sancion buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM SANCION WHERE ID_SANCION = ?";
        Sancion sancion = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sancion = new Sancion();
                    sancion.setIdSancion(rs.getInt("ID_SANCION"));
                    sancion.setFecha(rs.getDate("FECHA").toLocalDate());
                    sancion.setMotivo(rs.getString("MOTIVO"));
                    sancion.setDuracion(rs.getInt("DURACION"));
                    sancion.setTipo(rs.getString("TIPO"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la sanción por ID: " + e.getMessage());
        }

        return sancion;
    }

    @Override
    public void guardar(Sancion sancion) throws RepositoryException {
        String sql = "INSERT INTO SANCION (FECHA, MOTIVO, DURACION, TIPO, ID_JUGADOR) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(sancion.getFecha()));
            ps.setString(2, sancion.getMotivo());
            ps.setInt(3, sancion.getDuracion());
            ps.setString(4, sancion.getTipo());
            ps.setInt(5, sancion.getJugador().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la sanción: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Sancion sancion) throws RepositoryException {
        String sql = "UPDATE SANCION SET FECHA = ?, MOTIVO = ?, DURACION = ?, TIPO = ?, ID_JUGADOR = ? WHERE ID_SANCION = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(sancion.getFecha()));
            ps.setString(2, sancion.getMotivo());
            ps.setInt(3, sancion.getDuracion());
            ps.setString(4, sancion.getTipo());
            ps.setInt(5, sancion.getJugador().getId());
            ps.setInt(6, sancion.getIdSancion());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar la sanción: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM SANCION WHERE ID_SANCION = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la sanción: " + e.getMessage());
        }
    }
}