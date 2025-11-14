package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.EquipoEstadio;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.model.enums.TipoSede;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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


    public EquipoEstadio buscarPorId(int id) throws RepositoryException {
        String sql = """
        SELECT ee.*, 
               e.ID_EQUIPO, e.NOMBRE AS NOMBRE_EQUIPO,
               est.ID_ESTADIO, est.NOMBRE AS NOMBRE_ESTADIO, est.CAPACIDAD
        FROM EQUIPO_ESTADIO ee
        JOIN EQUIPO e ON ee.ID_EQUIPO = e.ID_EQUIPO
        JOIN ESTADIO est ON ee.ID_ESTADIO = est.ID_ESTADIO
        WHERE ee.ID_EQUIPO_ESTADIO = ?
    """;
        EquipoEstadio equipoEstadio = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    equipoEstadio = new EquipoEstadio();
                    equipoEstadio.setSede(TipoSede.valueOf(rs.getString("SEDE")));

                    // Cargar Equipo
                    Equipo equipo = new Equipo();
                    equipo.setId(rs.getInt("ID_EQUIPO"));
                    equipo.setNombre(rs.getString("NOMBRE_EQUIPO"));
                    equipoEstadio.setEquipo(equipo);

                    // Cargar Estadio
                    Estadio estadio = new Estadio();
                    estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                    estadio.setNombre(rs.getString("NOMBRE_ESTADIO"));
                    estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                    equipoEstadio.setEstadio(estadio);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar equipo-estadio por ID: " + e.getMessage());
        }

        return equipoEstadio;
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
