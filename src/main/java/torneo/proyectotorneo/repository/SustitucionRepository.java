package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Sustitucion;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class SustitucionRepository implements Repository<Sustitucion> {

    @Override
    public ArrayList<Sustitucion> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM SUSTITUCION";
        ArrayList<Sustitucion> sustituciones = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sustitucion sustitucion = new Sustitucion();
                sustitucion.setIdSustitucion(rs.getInt("ID_SUSTITUCION"));
                sustituciones.add(sustitucion);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar las sustituciones: " + e.getMessage());
        }

        return sustituciones;
    }

    @Override
    public Sustitucion buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM SUSTITUCION WHERE ID_SUSTITUCION = ?";
        Sustitucion sustitucion = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sustitucion = new Sustitucion();
                    sustitucion.setIdSustitucion(rs.getInt("ID_SUSTITUCION"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la sustitución por ID: " + e.getMessage());
        }

        return sustitucion;
    }

    @Override
    public void guardar(Sustitucion sustitucion) throws RepositoryException {
        String sql = "INSERT INTO SUSTITUCION (ID_PARTIDO, ID_JUGADOR_ENTRA, ID_JUGADOR_SALE) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sustitucion.getPartido().getIdPartido());
            ps.setInt(2, sustitucion.getJugadorEntra().getId());
            ps.setInt(3, sustitucion.getJugadorSale().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la sustitución: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Sustitucion sustitucion) throws RepositoryException {
        String sql = "UPDATE SUSTITUCION SET ID_PARTIDO = ?, ID_JUGADOR_ENTRA = ?, ID_JUGADOR_SALE = ? WHERE ID_SUSTITUCION = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sustitucion.getPartido().getIdPartido());
            ps.setInt(2, sustitucion.getJugadorEntra().getId());
            ps.setInt(3, sustitucion.getJugadorSale().getId());
            ps.setInt(4, sustitucion.getIdSustitucion());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar la sustitución: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM SUSTITUCION WHERE ID_SUSTITUCION = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la sustitución: " + e.getMessage());
        }
    }
}