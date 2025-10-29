package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.EquipoEstadio;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class EquipoEstadioRepository {

    public ArrayList<EquipoEstadio> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM EQUIPO_ESTADIO";
        ArrayList<EquipoEstadio> equiposEstadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EquipoEstadio ee = new EquipoEstadio();
                // Cargar datos según necesidad
                equiposEstadios.add(ee);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar equipos-estadios: " + e.getMessage());
        }

        return equiposEstadios;
    }

    public ArrayList<EquipoEstadio> buscarPorEquipo(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM EQUIPO_ESTADIO WHERE ID_EQUIPO = ?";
        ArrayList<EquipoEstadio> equiposEstadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EquipoEstadio ee = new EquipoEstadio();
                    // Cargar datos según necesidad
                    equiposEstadios.add(ee);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estadios del equipo: " + e.getMessage());
        }

        return equiposEstadios;
    }

    public void guardar(EquipoEstadio equipoEstadio) throws RepositoryException {
        String sql = "INSERT INTO EQUIPO_ESTADIO (ID_EQUIPO, ID_ESTADIO, SEDE) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipoEstadio.getEquipo().getId());
            ps.setInt(2, equipoEstadio.getEstadio().getIdEstadio());
            ps.setString(3, equipoEstadio.getSede().name());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar equipo-estadio: " + e.getMessage());
        }
    }

    public void eliminar(int idEquipo, int idEstadio) throws RepositoryException {
        String sql = "DELETE FROM EQUIPO_ESTADIO WHERE ID_EQUIPO = ? AND ID_ESTADIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            ps.setInt(2, idEstadio);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar equipo-estadio: " + e.getMessage());
        }
    }
}
