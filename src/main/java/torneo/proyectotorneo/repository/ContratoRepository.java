package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Contrato;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class ContratoRepository implements Repository<Contrato> {

    @Override
    public ArrayList<Contrato> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM CONTRATO";
        ArrayList<Contrato> contratos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contrato contrato = new Contrato();
                contrato.setIdContrato(rs.getInt("ID_CONTRATO"));
                contrato.setFechaInicio(rs.getDate("FECHA_INICIO").toLocalDate());
                contrato.setFechaFin(rs.getDate("FECHA_FIN").toLocalDate());
                contrato.setSalario(rs.getDouble("SALARIO"));
                contratos.add(contrato);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los contratos: " + e.getMessage());
        }

        return contratos;
    }

    @Override
    public Contrato buscarPorId(int id) throws RepositoryException {
        String sql = """
        SELECT c.*, 
               j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION, j.NUMERO_CAMISETA,
               e.ID_EQUIPO, e.NOMBRE AS NOMBRE_EQUIPO
        FROM CONTRATO c
        JOIN JUGADOR j ON c.ID_JUGADOR = j.ID_JUGADOR
        LEFT JOIN EQUIPO e ON j.ID_EQUIPO = e.ID_EQUIPO
        WHERE c.ID_CONTRATO = ?
    """;
        Contrato contrato = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    contrato = new Contrato();
                    contrato.setIdContrato(rs.getInt("ID_CONTRATO"));
                    contrato.setFechaInicio(rs.getDate("FECHA_INICIO").toLocalDate());
                    contrato.setFechaFin(rs.getDate("FECHA_FIN").toLocalDate());
                    contrato.setSalario(rs.getDouble("SALARIO"));

                    // Cargar Jugador con información básica
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

                    contrato.setJugador(jugador);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el contrato por ID: " + e.getMessage());
        }

        return contrato;
    }

    @Override
    public void guardar(Contrato contrato) throws RepositoryException {
        String sql = "INSERT INTO CONTRATO (FECHA_INICIO, FECHA_FIN, SALARIO, ID_JUGADOR) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID_CONTRATO"})) {

            // Convertir LocalDate a java.sql.Date
            ps.setDate(1, java.sql.Date.valueOf(contrato.getFechaInicio()));
            ps.setDate(2, java.sql.Date.valueOf(contrato.getFechaFin()));
            ps.setDouble(3, contrato.getSalario());
            ps.setInt(4, contrato.getJugador().getId());

            int filasAfectadas = ps.executeUpdate();

            // Obtener el ID generado automáticamente (opcional, pero buena práctica)
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contrato.setIdContrato(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el contrato: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Contrato contrato) throws RepositoryException {
        String sql = "UPDATE CONTRATO SET FECHA_INICIO = ?, FECHA_FIN = ?, SALARIO = ?, ID_JUGADOR = ? WHERE ID_CONTRATO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(contrato.getFechaInicio()));
            ps.setDate(2, Date.valueOf(contrato.getFechaFin()));
            ps.setDouble(3, contrato.getSalario());
            ps.setInt(4, contrato.getJugador().getId());
            ps.setInt(5, contrato.getIdContrato());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el contrato: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM CONTRATO WHERE ID_CONTRATO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el contrato: " + e.getMessage());
        }
    }
}