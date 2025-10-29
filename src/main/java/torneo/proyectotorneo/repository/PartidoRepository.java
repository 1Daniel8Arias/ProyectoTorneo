package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class PartidoRepository implements Repository<Partido> {
    @Override
    public ArrayList<Partido> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                partido.setFecha(rs.getDate("FECHA").toLocalDate());
                partido.setHora(rs.getString("HORA"));
                partidos.add(partido);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los partidos: " + e.getMessage());
        }

        return partidos;
    }

    @Override
    public Partido buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_PARTIDO = ?";
        Partido partido = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el partido por ID: " + e.getMessage());
        }

        return partido;
    }

    @Override
    public void guardar(Partido partido) throws RepositoryException {
        String sql = "INSERT INTO PARTIDO (FECHA, HORA, ID_JORNADA, ID_EQUIPO_LOCAL, ID_EQUIPO_VISITANTE, ID_ESTADIO) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(partido.getFecha()));
            ps.setString(2, partido.getHora());
            ps.setInt(3, partido.getJornada().getIdJornada());
            ps.setInt(4, partido.getEquipoLocal().getId());
            ps.setInt(5, partido.getEquipoVisitante().getId());
            ps.setInt(6, partido.getEstadio().getIdEstadio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el partido: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Partido partido) throws RepositoryException {
        String sql = "UPDATE PARTIDO SET FECHA = ?, HORA = ?, ID_JORNADA = ?, ID_EQUIPO_LOCAL = ?, ID_EQUIPO_VISITANTE = ?, ID_ESTADIO = ? WHERE ID_PARTIDO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(partido.getFecha()));
            ps.setString(2, partido.getHora());
            ps.setInt(3, partido.getJornada().getIdJornada());
            ps.setInt(4, partido.getEquipoLocal().getId());
            ps.setInt(5, partido.getEquipoVisitante().getId());
            ps.setInt(6, partido.getEstadio().getIdEstadio());
            ps.setInt(7, partido.getIdPartido());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el partido: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM PARTIDO WHERE ID_PARTIDO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el partido: " + e.getMessage());
        }
    }
}