package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.model.enums.TipoSede;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class EquipoRepository implements Repository<Equipo> {

    @Override
    public ArrayList<Equipo> listarTodos() throws RepositoryException {
        String sql = """
        
                SELECT
           E.ID_EQUIPO,
           E.NOMBRE AS NOMBRE_EQUIPO,
           ES.NOMBRE AS NOMBRE_ESTADIO,
           M.NOMBRE AS NOMBRE_MUNICIPIO,
         S.SEDE
         FROM EQUIPO E
        JOIN EQUIPO_ESTADIO S ON S.ID_EQUIPO = E.ID_EQUIPO
        JOIN ESTADIO ES ON ES.ID_ESTADIO = S.ID_ESTADIO
        JOIN MUNICIPIO M ON ES.ID_MUNICIPIO = M.ID_MUNICIPIO
        WHERE S.SEDE = 'Local' OR S.SEDE IS NULL
        """;

        ArrayList<Equipo> equipos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("NOMBRE_EQUIPO"));

                // --- Estadio ---
                Municipio municipio = new Municipio();
                municipio.setNombre(rs.getString("NOMBRE_MUNICIPIO"));

                Estadio estadio = new Estadio();
                estadio.setNombre(rs.getString("NOMBRE_ESTADIO"));
                estadio.setMunicipio(municipio);

                EquipoEstadio equipoEstadio = new EquipoEstadio();
                equipoEstadio.setEquipo(equipo);
                equipoEstadio.setEstadio(estadio);
                equipoEstadio.setSede(TipoSede.valueOf(rs.getString("SEDE")));



                ArrayList<EquipoEstadio> equiposEstadio = new ArrayList<>();
                equiposEstadio.add(equipoEstadio);

                // Guardar relación en Equipo
                equipo.setEstadios(equiposEstadio);

                equipos.add(equipo);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los equipos: " + e.getMessage());
        }

        return equipos;
    }


    @Override
    public Equipo buscarPorId(int id) throws RepositoryException {
        Equipo equipo = null;

        String sql = "SELECT * FROM EQUIPO WHERE ID_EQUIPO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    equipo = new Equipo();
                    equipo.setId(rs.getInt("ID_EQUIPO"));
                    equipo.setNombre(rs.getString("NOMBRE"));
                }
            }

            if (equipo != null) {
                equipo.setCapitan(buscarCapitan(equipo.getId()));
                equipo.setTecnico(buscarTecnicoPorEquipo(equipo.getId()));
                equipo.setListaJugadoresJugadores(listarJugadoresPorEquipo(equipo.getId()));
                equipo.setListaCuerpoTecnicos(listarCuerpoTecnicoPorEquipo(equipo.getId()));
                equipo.setEstadios(listarEstadiosPorEquipo(equipo.getId()));
                equipo.setListaPartidosLocal(listarPartidosPorEquipo(equipo.getId(), true));
                equipo.setListaPartidosVisitante(listarPartidosPorEquipo(equipo.getId(), false));
                equipo.setTablaPosicion(buscarTablaPosicionPorEquipo(equipo.getId()));
                equipo.setCantidadJugadores(equipo.getListaJugadoresJugadores().size());
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener equipo completo: " + e.getMessage());
        }

        return equipo;
    }



    public int buscarPorNombre(String nombreEquipo) throws RepositoryException {
        String sql = """
            SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE = ?
            """;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_EQUIPO");
                } else {
                    throw new RepositoryException("No se encontró el equipo con nombre: " + nombreEquipo);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el equipo por nombre: " + e.getMessage());
        }
    }

    @Override
    public void guardar(Equipo equipo) throws RepositoryException {
        String sql = "INSERT INTO EQUIPO (NOMBRE, ID_JUGADOR_CAPITAN) VALUES (?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombre());
            if (equipo.getCapitan() != null) {
                ps.setInt(2, equipo.getCapitan().getId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el equipo " + e.getMessage());
        }

    }

    @Override
    public void actualizar(Equipo equipo) throws RepositoryException {
        String sql = "UPDATE EQUIPO SET NOMBRE = ?, ID_JUGADOR_CAPITAN = ? WHERE ID_EQUIPO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombre());
            if (equipo.getCapitan() != null) {
                ps.setInt(2, equipo.getCapitan().getId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, equipo.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el equipo " + e.getMessage());
        }
    }


    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM EQUIPO WHERE ID_EQUIPO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el equipo " + e.getMessage());
        }
    }

    //consulta intermedia 2

    public ArrayList<Equipo> listarEquiposConTecnico() throws RepositoryException {
        String sql = "SELECT e.ID_EQUIPO, e.NOMBRE AS E_NOMBRE, " +
                "t.ID_TECNICO, t.NOMBRE AS T_NOMBRE, t.APELLIDO AS T_APELLIDO " +
                "FROM EQUIPO e LEFT JOIN TECNICO t ON t.ID_EQUIPO = e.ID_EQUIPO";
        ArrayList<Equipo> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("E_NOMBRE"));

                int idTec = rs.getInt("ID_TECNICO");
                if (!rs.wasNull()) {
                    Tecnico t = new Tecnico();
                    t.setId(idTec);
                    t.setNombre(rs.getString("T_NOMBRE"));
                    t.setApellido(rs.getString("T_APELLIDO"));
                    e.setTecnico(t);
                }
                lista.add(e);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarEquiposConTecnico: " + ex.getMessage());
        }
        return lista;
    }

    //intermedia 3
    public ArrayList<Equipo> listarEquiposConCantidadDeJugadores() throws RepositoryException {
        String sql = "SELECT e.ID_EQUIPO, e.NOMBRE AS E_NOMBRE, COUNT(j.ID_JUGADOR) AS CANTIDAD_JUGADORES " +
                "FROM EQUIPO e LEFT JOIN JUGADOR j ON j.ID_EQUIPO = e.ID_EQUIPO " +
                "GROUP BY e.ID_EQUIPO, e.NOMBRE ORDER BY e.NOMBRE";

        ArrayList<Equipo> lista = new ArrayList<>();


        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("E_NOMBRE"));
                equipo.setCantidadJugadores(rs.getInt("CANTIDAD_JUGADORES"));

                lista.add(equipo);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarEquiposConCantidadDeJugadores: " + ex.getMessage());
        }

        return lista;
    }

    //avanzada 7
    public ArrayList<Equipo> listarEquiposConSanciones() throws RepositoryException {
        String sql = "SELECT e.ID_EQUIPO, e.NOMBRE FROM EQUIPO e WHERE e.ID_EQUIPO IN (" +
                "SELECT j.ID_EQUIPO FROM JUGADOR j JOIN SANCION s ON s.ID_JUGADOR = j.ID_JUGADOR)";
        ArrayList<Equipo> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("NOMBRE"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarEquiposConSanciones: " + ex.getMessage());
        }
        return lista;
    }

    private Jugador buscarCapitan(int idEquipo) throws SQLException {
        Jugador capitan = null;
        String sql = """
        SELECT j.* FROM EQUIPO e
        JOIN JUGADOR j ON e.ID_JUGADOR_CAPITAN = j.ID_JUGADOR
        WHERE e.ID_EQUIPO = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                capitan = new Jugador();
                capitan.setId(rs.getInt("ID_JUGADOR"));
                capitan.setNombre(rs.getString("NOMBRE"));
                capitan.setApellido(rs.getString("APELLIDO"));
            }
        }
        return capitan;
    }

    private Tecnico buscarTecnicoPorEquipo(int idEquipo) throws SQLException {
        Tecnico tecnico = null;
        String sql = "SELECT * FROM TECNICO WHERE ID_EQUIPO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tecnico = new Tecnico();
                tecnico.setId(rs.getInt("ID_TECNICO"));
                tecnico.setNombre(rs.getString("NOMBRE"));
                tecnico.setApellido(rs.getString("APELLIDO"));
            }
        }
        return tecnico;
    }

    private ArrayList<Jugador> listarJugadoresPorEquipo(int idEquipo) throws SQLException {
        ArrayList<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM JUGADOR WHERE ID_EQUIPO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                j.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));
                jugadores.add(j);
            }
        }
        return jugadores;
    }

    private ArrayList<CuerpoTecnico> listarCuerpoTecnicoPorEquipo(int idEquipo) throws SQLException {
        ArrayList<CuerpoTecnico> lista = new ArrayList<>();
        String sql = "SELECT * FROM CUERPO_TECNICO WHERE ID_EQUIPO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CuerpoTecnico ct = new CuerpoTecnico();
                ct.setId(rs.getInt("ID_CUERPO_TECNICO"));
                ct.setNombre(rs.getString("NOMBRE"));
                ct.setApellido(rs.getString("APELLIDO"));
                ct.setEspecialidad(rs.getString("ESPECIALIDAD"));
                lista.add(ct);
            }
        }
        return lista;
    }

    private ArrayList<EquipoEstadio> listarEstadiosPorEquipo(int idEquipo) throws SQLException {
        ArrayList<EquipoEstadio> lista = new ArrayList<>();
        String sql = """
        SELECT ee.*, es.ID_ESTADIO, es.NOMBRE, es.CAPACIDAD
        FROM EQUIPO_ESTADIO ee
        JOIN ESTADIO es ON ee.ID_ESTADIO = es.ID_ESTADIO
        WHERE ee.ID_EQUIPO = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Estadio estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("ID_ESTADIO"));
                estadio.setNombre(rs.getString("NOMBRE"));
                estadio.setCapacidad(rs.getInt("CAPACIDAD"));

                EquipoEstadio ee = new EquipoEstadio();
                ee.setEstadio(estadio);
                ee.setSede(TipoSede.valueOf(rs.getString("SEDE").toUpperCase()));
                lista.add(ee);
            }
        }
        return lista;
    }

    private ArrayList<Partido> listarPartidosPorEquipo(int idEquipo, boolean local) throws SQLException {
        ArrayList<Partido> lista = new ArrayList<>();
        String columna = local ? "ID_EQUIPO_LOCAL" : "ID_EQUIPO_VISITANTE";
        String sql = "SELECT * FROM PARTIDO WHERE " + columna + " = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Partido p = new Partido();
                p.setIdPartido(rs.getInt("ID_PARTIDO"));
                p.setFecha(rs.getDate("FECHA").toLocalDate());
                p.setHora(rs.getString("HORA"));
                lista.add(p);
            }
        }
        return lista;
    }

    private TablaPosicion buscarTablaPosicionPorEquipo(int idEquipo) throws SQLException {
        TablaPosicion tp = null;
        String sql = "SELECT * FROM TABLA_POSICION WHERE ID_EQUIPO = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tp = new TablaPosicion();
                tp.setGanados(rs.getInt("GANADOS"));
                tp.setEmpates(rs.getInt("EMPATES"));
                tp.setPerdidos(rs.getInt("PERDIDOS"));
                tp.setPuntos(rs.getInt("PUNTOS"));
            }
        }
        return tp;
    }

}
