package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.model.ResultadoFinal;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = """
        SELECT rf.*,
               p.ID_PARTIDO, p.FECHA, p.HORA,
               el.ID_EQUIPO AS ID_LOCAL, el.NOMBRE AS NOMBRE_LOCAL,
               ev.ID_EQUIPO AS ID_VISITANTE, ev.NOMBRE AS NOMBRE_VISITANTE
        FROM RESULTADO_FINAL rf
        JOIN PARTIDO p ON rf.ID_PARTIDO = p.ID_PARTIDO
        JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO
        JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO
        WHERE rf.ID_RESULTADO_FINAL = ?
    """;
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

                    // Cargar Partido
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

                    resultado.setPartido(partido);
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
