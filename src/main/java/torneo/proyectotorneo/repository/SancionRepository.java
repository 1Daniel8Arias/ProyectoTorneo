package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.model.enums.PosicionJugador;
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
        String sql = """
        SELECT s.*,
               j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION, j.NUMERO_CAMISETA,
               e.ID_EQUIPO, e.NOMBRE AS NOMBRE_EQUIPO
        FROM SANCION s
        JOIN JUGADOR j ON s.ID_JUGADOR = j.ID_JUGADOR
        LEFT JOIN EQUIPO e ON j.ID_EQUIPO = e.ID_EQUIPO
        WHERE s.ID_SANCION = ?
    """;
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

                    // Cargar Jugador
                    Jugador jugador = new Jugador();
                    jugador.setId(rs.getInt("ID_JUGADOR"));
                    jugador.setNombre(rs.getString("NOMBRE"));
                    jugador.setApellido(rs.getString("APELLIDO"));
                    jugador.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                    jugador.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));

                    // Equipo si existe
                    if (rs.getObject("ID_EQUIPO") != null) {
                        Equipo equipo = new Equipo();
                        equipo.setId(rs.getInt("ID_EQUIPO"));
                        equipo.setNombre(rs.getString("NOMBRE_EQUIPO"));
                        jugador.setEquipo(equipo);
                    }

                    sancion.setJugador(jugador);
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