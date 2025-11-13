package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.model.Sustitucion;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = """
        SELECT s.*,
               je.ID_JUGADOR AS ID_ENTRA, je.NOMBRE AS NOMBRE_ENTRA, je.APELLIDO AS APELLIDO_ENTRA,
               js.ID_JUGADOR AS ID_SALE, js.NOMBRE AS NOMBRE_SALE, js.APELLIDO AS APELLIDO_SALE,
               p.ID_PARTIDO, p.FECHA, p.HORA,
               el.ID_EQUIPO AS ID_LOCAL, el.NOMBRE AS NOMBRE_LOCAL,
               ev.ID_EQUIPO AS ID_VISITANTE, ev.NOMBRE AS NOMBRE_VISITANTE
        FROM SUSTITUCION s
        JOIN JUGADOR je ON s.ID_JUGADOR_ENTRA = je.ID_JUGADOR
        JOIN JUGADOR js ON s.ID_JUGADOR_SALE = js.ID_JUGADOR
        JOIN PARTIDO p ON s.ID_PARTIDO = p.ID_PARTIDO
        JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO
        JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO
        WHERE s.ID_SUSTITUCION = ?
    """;
        Sustitucion sustitucion = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sustitucion = new Sustitucion();
                    sustitucion.setIdSustitucion(rs.getInt("ID_SUSTITUCION"));

                    // Jugador que entra
                    Jugador jugadorEntra = new Jugador();
                    jugadorEntra.setId(rs.getInt("ID_ENTRA"));
                    jugadorEntra.setNombre(rs.getString("NOMBRE_ENTRA"));
                    jugadorEntra.setApellido(rs.getString("APELLIDO_ENTRA"));
                    sustitucion.setJugadorEntra(jugadorEntra);

                    // Jugador que sale
                    Jugador jugadorSale = new Jugador();
                    jugadorSale.setId(rs.getInt("ID_SALE"));
                    jugadorSale.setNombre(rs.getString("NOMBRE_SALE"));
                    jugadorSale.setApellido(rs.getString("APELLIDO_SALE"));
                    sustitucion.setJugadorSale(jugadorSale);

                    // Partido
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));

                    Equipo local = new Equipo();
                    local.setId(rs.getInt("ID_LOCAL"));
                    local.setNombre(rs.getString("NOMBRE_LOCAL"));
                    partido.setEquipoLocal(local);

                    Equipo visitante = new Equipo();
                    visitante.setId(rs.getInt("ID_VISITANTE"));
                    visitante.setNombre(rs.getString("NOMBRE_VISITANTE"));
                    partido.setEquipoVisitante(visitante);

                    sustitucion.setPartido(partido);
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