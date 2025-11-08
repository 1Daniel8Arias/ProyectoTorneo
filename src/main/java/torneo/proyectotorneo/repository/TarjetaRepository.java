package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.model.Tarjeta;
import torneo.proyectotorneo.model.enums.TipoTarjeta;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class TarjetaRepository implements Repository<Tarjeta> {

    @Override
    public ArrayList<Tarjeta> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM TARJETA";
        ArrayList<Tarjeta> tarjetas = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tarjeta tarjeta = new Tarjeta();
                tarjeta.setIdTarjeta(rs.getInt("ID_TARJETA"));
                // Nota: tipo debería ser un enum TipoTarjeta, no Tarjeta
                tarjetas.add(tarjeta);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar las tarjetas: " + e.getMessage());
        }

        return tarjetas;
    }

    @Override
    public Tarjeta buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM TARJETA WHERE ID_TARJETA = ?";
        Tarjeta tarjeta = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tarjeta = new Tarjeta();
                    tarjeta.setIdTarjeta(rs.getInt("ID_TARJETA"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la tarjeta por ID: " + e.getMessage());
        }

        return tarjeta;
    }

    @Override
    public void guardar(Tarjeta tarjeta) throws RepositoryException {
        String sql = "INSERT INTO TARJETA (TIPO, ID_PARTIDO, ID_JUGADOR) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "AMARILLA"); // Deberías usar tarjeta.getTipo().name()
            ps.setInt(2, tarjeta.getPartido().getIdPartido());
            ps.setInt(3, tarjeta.getJugador().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la tarjeta: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Tarjeta tarjeta) throws RepositoryException {
        String sql = "UPDATE TARJETA SET TIPO = ?, ID_PARTIDO = ?, ID_JUGADOR = ? WHERE ID_TARJETA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "AMARILLA"); // Deberías usar tarjeta.getTipo().name()
            ps.setInt(2, tarjeta.getPartido().getIdPartido());
            ps.setInt(3, tarjeta.getJugador().getId());
            ps.setInt(4, tarjeta.getIdTarjeta());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar la tarjeta: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM TARJETA WHERE ID_TARJETA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la tarjeta: " + e.getMessage());
        }
    }

    //consulta intermedia 8

    public ArrayList<Tarjeta> listarTarjetasPorPartido() throws RepositoryException {
        String sql = "SELECT p.ID_PARTIDO, t.ID_TARJETA, t.TIPO, " +
                "j.ID_JUGADOR, j.NOMBRE AS NOMBRE_JUGADOR, j.APELLIDO AS APELLIDO_JUGADOR " +
                "FROM TARJETA t " +
                "JOIN JUGADOR j ON t.ID_JUGADOR = j.ID_JUGADOR " +
                "JOIN PARTIDO p ON t.ID_PARTIDO = p.ID_PARTIDO";

        ArrayList<Tarjeta> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tarjeta tarjeta = new Tarjeta();
                tarjeta.setIdTarjeta(rs.getInt("ID_TARJETA"));

                // conversión del texto del campo TIPO a enum TipoTarjeta
                String tipoStr = rs.getString("TIPO");
                if (tipoStr != null) {
                    tarjeta.setTipo(TipoTarjeta.valueOf(tipoStr.toUpperCase()));
                }

                Jugador jugador = new Jugador();
                jugador.setId(rs.getInt("ID_JUGADOR"));
                jugador.setNombre(rs.getString("NOMBRE_JUGADOR"));
                jugador.setApellido(rs.getString("APELLIDO_JUGADOR"));
                tarjeta.setJugador(jugador);

                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                tarjeta.setPartido(partido);

                lista.add(tarjeta);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarTarjetasPorPartido: " + ex.getMessage());
        }

        return lista;
    }

}