package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.TipoSede;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstadioRepository implements Repository<Estadio> {

    private final EquipoEstadioRepository equipoEstadioRepository;
    private final PartidoRepository partidoRepository;

    public EstadioRepository() {
        this.equipoEstadioRepository = new EquipoEstadioRepository();
        this.partidoRepository = new PartidoRepository();
    }

    @Override
    public ArrayList<Estadio> listarTodos() throws RepositoryException {
        // ✅ JOIN con MUNICIPIO y DEPARTAMENTO
        String sql = """
            SELECT 
                e.ID_ESTADIO, 
                e.NOMBRE, 
                e.CAPACIDAD,
                m.ID_MUNICIPIO,
                m.NOMBRE AS NOMBRE_MUNICIPIO,
                d.ID_DEPARTAMENTO, 
                d.NOMBRE AS NOMBRE_DEPARTAMENTO
            FROM ESTADIO e
            JOIN MUNICIPIO m ON e.ID_MUNICIPIO = m.ID_MUNICIPIO
            JOIN DEPARTAMENTO d ON m.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO
        """;

        ArrayList<Estadio> estadios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estadio estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                estadio.setNombre(rs.getString("NOMBRE"));
                estadio.setCapacidad(rs.getInt("CAPACIDAD"));

                // ✅ Cargar la jerarquía: Municipio → Departamento
                Municipio municipio = new Municipio();
                municipio.setIdMunicipio(rs.getInt("ID_MUNICIPIO"));
                municipio.setNombre(rs.getString("NOMBRE_MUNICIPIO"));

                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(rs.getInt("ID_DEPARTAMENTO"));
                departamento.setNombre(rs.getString("NOMBRE_DEPARTAMENTO"));

                municipio.setDepartamento(departamento);
                estadio.setMunicipio(municipio);

                estadios.add(estadio);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los estadios: " + e.getMessage());
        }

        return estadios;
    }

    @Override
    public Estadio buscarPorId(int id) throws RepositoryException {
        // ✅ JOIN con MUNICIPIO y DEPARTAMENTO
        String sql = """
            SELECT 
                e.ID_ESTADIO, 
                e.NOMBRE, 
                e.CAPACIDAD,
                m.ID_MUNICIPIO,
                m.NOMBRE AS NOMBRE_MUNICIPIO,
                d.ID_DEPARTAMENTO, 
                d.NOMBRE AS NOMBRE_DEPARTAMENTO
            FROM ESTADIO e
            JOIN MUNICIPIO m ON e.ID_MUNICIPIO = m.ID_MUNICIPIO
            JOIN DEPARTAMENTO d ON m.ID_DEPARTAMENTO = d.ID_DEPARTAMENTO
            WHERE e.ID_ESTADIO = ?
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

                    // ✅ Cargar la jerarquía: Municipio → Departamento
                    Municipio municipio = new Municipio();
                    municipio.setIdMunicipio(rs.getInt("ID_MUNICIPIO"));
                    municipio.setNombre(rs.getString("NOMBRE_MUNICIPIO"));

                    Departamento departamento = new Departamento();
                    departamento.setIdDepartamento(rs.getInt("ID_DEPARTAMENTO"));
                    departamento.setNombre(rs.getString("NOMBRE_DEPARTAMENTO"));

                    municipio.setDepartamento(departamento);
                    estadio.setMunicipio(municipio);

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
        // ✅ Inserta ID_MUNICIPIO
        String sql = "INSERT INTO ESTADIO (NOMBRE, CAPACIDAD, ID_MUNICIPIO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getMunicipio().getIdMunicipio());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el estadio: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Estadio estadio) throws RepositoryException {
        // ✅ Actualiza ID_MUNICIPIO
        String sql = "UPDATE ESTADIO SET NOMBRE = ?, CAPACIDAD = ?, ID_MUNICIPIO = ? WHERE ID_ESTADIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNombre());
            ps.setInt(2, estadio.getCapacidad());
            ps.setInt(3, estadio.getMunicipio().getIdMunicipio());
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
