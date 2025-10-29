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

    /**
     * Busca todos los partidos de un equipo (como local o visitante)
     */
    public ArrayList<Partido> buscarPartidosPorEquipo(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_EQUIPO_LOCAL = ? OR ID_EQUIPO_VISITANTE = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);
            ps.setInt(2, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos por equipo: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos donde el equipo juega como local
     */
    public ArrayList<Partido> buscarPartidosComoLocal(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_EQUIPO_LOCAL = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos como local: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos donde el equipo juega como visitante
     */
    public ArrayList<Partido> buscarPartidosComoVisitante(int idEquipo) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_EQUIPO_VISITANTE = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos como visitante: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos de una jornada específica
     */
    public ArrayList<Partido> buscarPartidosPorJornada(int idJornada) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_JORNADA = ? ORDER BY FECHA, HORA";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idJornada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos por jornada: " + e.getMessage());
        }

        return partidos;
    }

    /**
     * Busca todos los partidos jugados en un estadio específico
     */
    public ArrayList<Partido> buscarPartidosPorEstadio(int idEstadio) throws RepositoryException {
        String sql = "SELECT * FROM PARTIDO WHERE ID_ESTADIO = ? ORDER BY FECHA DESC";
        ArrayList<Partido> partidos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstadio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));
                    partidos.add(partido);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar partidos por estadio: " + e.getMessage());
        }

        return partidos;
    }
}