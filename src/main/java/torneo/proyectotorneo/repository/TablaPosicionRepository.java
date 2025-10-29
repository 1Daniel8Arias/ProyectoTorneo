package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.TablaPosicion;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class TablaPosicionRepository implements Repository<TablaPosicion> {

    @Override
    public ArrayList<TablaPosicion> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM TABLA_POSICION ORDER BY PUNTOS DESC, DIFERENCIA_GOLES DESC";
        ArrayList<TablaPosicion> tabla = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TablaPosicion posicion = new TablaPosicion();
                posicion.setIdTabla(rs.getInt("ID_TABLA"));
                posicion.setGanados(rs.getInt("GANADOS"));
                posicion.setEmpates(rs.getInt("EMPATES"));
                posicion.setPerdidos(rs.getInt("PERDIDOS"));
                posicion.setGolesAFavor(rs.getInt("GOLES_A_FAVOR"));
                posicion.setGolesEnContra(rs.getInt("GOLES_EN_CONTRA"));
                posicion.setDiferenciaGoles(rs.getInt("DIFERENCIA_GOLES"));
                posicion.setPuntos(rs.getInt("PUNTOS"));
                tabla.add(posicion);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar la tabla de posiciones: " + e.getMessage());
        }

        return tabla;
    }

    @Override
    public TablaPosicion buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM TABLA_POSICION WHERE ID_TABLA = ?";
        TablaPosicion posicion = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    posicion = new TablaPosicion();
                    posicion.setIdTabla(rs.getInt("ID_TABLA"));
                    posicion.setGanados(rs.getInt("GANADOS"));
                    posicion.setEmpates(rs.getInt("EMPATES"));
                    posicion.setPerdidos(rs.getInt("PERDIDOS"));
                    posicion.setGolesAFavor(rs.getInt("GOLES_A_FAVOR"));
                    posicion.setGolesEnContra(rs.getInt("GOLES_EN_CONTRA"));
                    posicion.setDiferenciaGoles(rs.getInt("DIFERENCIA_GOLES"));
                    posicion.setPuntos(rs.getInt("PUNTOS"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar la posición por ID: " + e.getMessage());
        }

        return posicion;
    }

    @Override
    public void guardar(TablaPosicion posicion) throws RepositoryException {
        String sql = "INSERT INTO TABLA_POSICION (GANADOS, EMPATES, PERDIDOS, GOLES_A_FAVOR, GOLES_EN_CONTRA, DIFERENCIA_GOLES, PUNTOS, ID_EQUIPO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, posicion.getGanados());
            ps.setInt(2, posicion.getEmpates());
            ps.setInt(3, posicion.getPerdidos());
            ps.setInt(4, posicion.getGolesAFavor());
            ps.setInt(5, posicion.getGolesEnContra());
            ps.setInt(6, posicion.getDiferenciaGoles());
            ps.setInt(7, posicion.getPuntos());
            ps.setInt(8, posicion.getEquipo().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la posición: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(TablaPosicion posicion) throws RepositoryException {
        String sql = "UPDATE TABLA_POSICION SET GANADOS = ?, EMPATES = ?, PERDIDOS = ?, GOLES_A_FAVOR = ?, GOLES_EN_CONTRA = ?, DIFERENCIA_GOLES = ?, PUNTOS = ?, ID_EQUIPO = ? WHERE ID_TABLA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, posicion.getGanados());
            ps.setInt(2, posicion.getEmpates());
            ps.setInt(3, posicion.getPerdidos());
            ps.setInt(4, posicion.getGolesAFavor());
            ps.setInt(5, posicion.getGolesEnContra());
            ps.setInt(6, posicion.getDiferenciaGoles());
            ps.setInt(7, posicion.getPuntos());
            ps.setInt(8, posicion.getEquipo().getId());
            ps.setInt(9, posicion.getIdTabla());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar la posición: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM TABLA_POSICION WHERE ID_TABLA = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la posición: " + e.getMessage());
        }
    }
}
