package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Departamento;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstadioRepository implements Repository<Estadio> {

    @Override
    public ArrayList<Estadio> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM ESTADIO";
        ArrayList<Estadio> estadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estadio estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                estadio.setNombre(rs.getString("NOMBRE"));
                estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                estadios.add(estadio);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los estadios: " + e.getMessage());
        }

        return estadios;
    }

    @Override
    public Estadio buscarPorId(int id) throws RepositoryException {
        String sql = """
        SELECT est.*, d.ID_DEPARTAMENTO, d.NOMBRE AS NOMBRE_DEPARTAMENTO
        FROM ESTADIO est
        JOIN DEPARTAMENTO d ON est.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO
        WHERE est.ID_ESTADIO = ?
    """;
        Estadio estadio = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadio = new Estadio();
                    estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                    estadio.setNombre(rs.getString("NOMBRE"));
                    estadio.setCapacidad(rs.getInt("CAPACIDAD"));

                    // Cargar Departamento
                    Departamento departamento = new Departamento();
                    departamento.setIdDepartamento(rs.getInt("ID_DEPARTAMENTO"));
                    departamento.setNombre(rs.getString("NOMBRE_DEPARTAMENTO"));
                    estadio.setDepartamento(departamento);

                    // Cargar relaciones con equipos
                    estadio.setEquipoEstadios(listarEquiposEstadio(id));

                    // Cargar lista de partidos jugados en este estadio
                    estadio.setListaPartidos(listarPartidosPorEstadio(id));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el estadio por ID: " + e.getMessage());
        }

        return estadio;
    }

    @Override
    public void guardar(Estadio estadio) throws RepositoryException {
        String sql = "INSERT INTO ESTADIO (NOMBRE, CAPACIDAD, ID_DEPARTAMENTO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getDepartamento().getIdDepartamento());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el estadio: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Estadio estadio) throws RepositoryException {
        String sql = "UPDATE ESTADIO SET NOMBRE = ?, CAPACIDAD = ?, ID_DEPARTAMENTO = ? WHERE ID_ESTADIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getDepartamento().getIdDepartamento());
            ps.setInt(4, estadio.getIdEstadio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el estadio: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM ESTADIO WHERE ID_ESTADIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el estadio: " + e.getMessage());
        }
    }

    /**
     * Método adicional para buscar estadios por departamento
     */
    public ArrayList<Estadio> buscarPorDepartamento(int idDepartamento) throws RepositoryException {
        String sql = "SELECT * FROM ESTADIO WHERE ID_DEPARTAMENTO = ?";
        ArrayList<Estadio> estadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estadio estadio = new Estadio();
                    estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                    estadio.setNombre(rs.getString("NOMBRE"));
                    estadio.setCapacidad(rs.getInt("CAPACIDAD"));
                    estadios.add(estadio);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estadios por departamento: " + e.getMessage());
        }

        return estadios;
    }

    private ArrayList<EquipoEstadio> listarEquiposEstadio(int idEstadio) throws SQLException {
        ArrayList<EquipoEstadio> lista = new ArrayList<>();
        String sql = """
        SELECT ee.SEDE, e.ID_EQUIPO, e.NOMBRE
        FROM EQUIPO_ESTADIO ee
        JOIN EQUIPO e ON ee.ID_EQUIPO = e.ID_EQUIPO
        WHERE ee.ID_ESTADIO = ?
    """;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstadio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EquipoEstadio ee = new EquipoEstadio();
                ee.setSede(TipoSede.valueOf(rs.getString("SEDE")));

                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("NOMBRE"));
                ee.setEquipo(equipo);

                lista.add(ee);
            }
        }

        return lista;
    }

    private ArrayList<Partido> listarPartidosPorEstadio(int idEstadio) throws SQLException {
        ArrayList<Partido> lista = new ArrayList<>();
        String sql = """
        SELECT p.ID_PARTIDO, p.FECHA, p.HORA,
               el.ID_EQUIPO AS ID_LOCAL, el.NOMBRE AS NOMBRE_LOCAL,
               ev.ID_EQUIPO AS ID_VISITANTE, ev.NOMBRE AS NOMBRE_VISITANTE
        FROM PARTIDO p
        JOIN EQUIPO el ON p.ID_EQUIPO_LOCAL = el.ID_EQUIPO
        JOIN EQUIPO ev ON p.ID_EQUIPO_VISITANTE = ev.ID_EQUIPO
        WHERE p.ID_ESTADIO = ?
        ORDER BY p.FECHA DESC
    """;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstadio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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

                lista.add(partido);
            }
        }

        return lista;
    }

}
