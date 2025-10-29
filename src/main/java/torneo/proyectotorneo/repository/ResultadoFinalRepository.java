package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.ResultadoFinal;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class ResultadoFinalRepository implements Repository<ResultadoFinal> {

    @Override
    public ArrayList<ResultadoFinal> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM RESULTADO_FINAL";
        ArrayList<ResultadoFinal> resultados = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ResultadoFinal resultado = new ResultadoFinal();
                resultado.setIdResultadoFinal(rs.getInt("ID_RESULTADO_FINAL"));
                resultado.setGolesLocal(rs.getInt("GOLES_LOCAL"));
                resultado.setGolesVisitante(rs.getInt("GOLES_VISITANTE"));
                resultados.add(resultado);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los resultados finales: " + e.getMessage());
        }

        return resultados;
    }

    @Override
    public ResultadoFinal buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM RESULTADO_FINAL WHERE ID_RESULTADO_FINAL = ?";
        ResultadoFinal resultado = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado = new ResultadoFinal();
                    resultado.setIdResultadoFinal(rs.getInt("ID_RESULTADO_FINAL"));
                    resultado.setGolesLocal(rs.getInt("GOLES_LOCAL"));
                    resultado.setGolesVisitante(rs.getInt("GOLES_VISITANTE"));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el resultado final por ID: " + e.getMessage());
        }

        return resultado;
    }

    @Override
    public void guardar(ResultadoFinal resultado) throws RepositoryException {
        String sql = "INSERT INTO RESULTADO_FINAL (GOLES_LOCAL, GOLES_VISITANTE, ID_PARTIDO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resultado.getGolesLocal());
            ps.setInt(2, resultado.getGolesVisitante());
            ps.setInt(3, resultado.getPartido().getIdPartido());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el resultado final: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(ResultadoFinal resultado) throws RepositoryException {
        String sql = "UPDATE RESULTADO_FINAL SET GOLES_LOCAL = ?, GOLES_VISITANTE = ?, ID_PARTIDO = ? WHERE ID_RESULTADO_FINAL = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resultado.getGolesLocal());
            ps.setInt(2, resultado.getGolesVisitante());
            ps.setInt(3, resultado.getPartido().getIdPartido());
            ps.setInt(4, resultado.getIdResultadoFinal());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el resultado final: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM RESULTADO_FINAL WHERE ID_RESULTADO_FINAL = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el resultado final: " + e.getMessage());
        }
    }
}
