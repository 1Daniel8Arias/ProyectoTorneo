package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.model.enums.TipoTarjeta;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JugadorRepository implements Repository<Jugador> {
    @Override
    public ArrayList<Jugador> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM JUGADOR";
        ArrayList<Jugador> jugadores = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setId(rs.getInt("ID_JUGADOR"));
                jugador.setNombre(rs.getString("NOMBRE"));
                jugador.setApellido(rs.getString("APELLIDO"));
                jugador.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                jugador.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));

                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                jugador.setEquipo(equipo);

                jugadores.add(jugador);


            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el jugadores : " + e.getMessage());
        }

        return jugadores;
    }

    @Override
    public Jugador buscarPorId(int id) throws RepositoryException {

        Jugador jugador = null;

        String sql = """
        SELECT j.*, e.ID_EQUIPO, e.NOMBRE AS NOMBRE_EQUIPO
        FROM JUGADOR j
        LEFT JOIN EQUIPO e ON j.ID_EQUIPO = e.ID_EQUIPO
        WHERE j.ID_JUGADOR = ?
    """;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jugador = new Jugador();
                    jugador.setId(rs.getInt("ID_JUGADOR"));
                    jugador.setNombre(rs.getString("NOMBRE"));
                    jugador.setApellido(rs.getString("APELLIDO"));
                    jugador.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                    jugador.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));

                    Equipo equipo = new Equipo();
                    equipo.setId(rs.getInt("ID_EQUIPO"));
                    equipo.setNombre(rs.getString("NOMBRE_EQUIPO"));
                    jugador.setEquipo(equipo);
                }
            }

            if (jugador != null) {
                jugador.setListaContratos(listarContratosPorJugador(id));
                jugador.setListaGoles(listarGolesPorJugador(id));
                jugador.setListaTarjetas(listarTarjetasPorJugador(id));
                jugador.setListaSanciones(listarSancionesPorJugador(id));
                jugador.setListaSustitucionesEntradas(listarSustitucionesPorJugador(id, true));
                jugador.setListaSustitucionesSalidas(listarSustitucionesPorJugador(id, false));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener jugador completo: " + e.getMessage());
        }

        return jugador;
    }

    @Override
    public void guardar(Jugador jugador) throws RepositoryException {
        String sql = "INSERT INTO JUGADOR (NOMBRE, APELLIDO, POSICION, NUMERO_CAMISETA, ID_EQUIPO) " +
                "VALUES (?, ?, ?, ?, ?)";

        //   Agregar el segundo parámetro para obtener las claves generadas
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID_JUGADOR"})) {

            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getApellido());
            ps.setString(3, jugador.getPosicion().name());
            ps.setString(4, jugador.getNumeroCamiseta());
            ps.setInt(5, jugador.getEquipo().getId());

            int filasAfectadas = ps.executeUpdate();

            // Obtener el ID generado automáticamente
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Asignar el ID generado al objeto jugador
                        jugador.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el jugador: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Jugador jugador) throws RepositoryException {
        String sql = "UPDATE JUGADOR SET NOMBRE = ?, APELLIDO = ?, POSICION = ?, NUMERO_CAMISETA = ?, ID_EQUIPO = ? WHERE ID_JUGADOR = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getApellido());
            ps.setString(3, jugador.getPosicion().name());
            ps.setString(4, jugador.getNumeroCamiseta());
            ps.setInt(5, jugador.getEquipo().getId());
            ps.setInt(6, jugador.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el jugador: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM JUGADOR WHERE ID_JUGADOR=?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el jugador: " + e.getMessage());

        }

    }

    public ArrayList<Jugador> listarJugadoresPorEquipo(int id) throws RepositoryException {
        String sql = """
        SELECT J.ID_JUGADOR, J.NOMBRE AS J_NOMBRE, J.APELLIDO AS J_APELLIDO,
               E.ID_EQUIPO, E.NOMBRE AS E_NOMBRE
        FROM JUGADOR J
        JOIN EQUIPO E ON J.ID_EQUIPO = E.ID_EQUIPO
        WHERE E.ID_EQUIPO = ?
        """;

        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // ✅ Establecer parámetro

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Jugador j = new Jugador();
                    j.setId(rs.getInt("ID_JUGADOR"));
                    j.setNombre(rs.getString("J_NOMBRE"));
                    j.setApellido(rs.getString("J_APELLIDO"));

                    Equipo e = new Equipo();
                    e.setId(rs.getInt("ID_EQUIPO"));
                    e.setNombre(rs.getString("E_NOMBRE"));
                    j.setEquipo(e);

                    lista.add(j);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error listarJugadoresPorEquipo: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<Jugador> listarJugadoresPorEquipoYPosicion(int idEquipo, String posicion) throws RepositoryException {
        String sql = """
        SELECT J.ID_JUGADOR, J.NOMBRE, J.APELLIDO, J.NUMERO_CAMISETA,J.POSICION,
               E.ID_EQUIPO, E.NOMBRE AS E_NOMBRE
        FROM JUGADOR J
        JOIN EQUIPO E ON J.ID_EQUIPO = E.ID_EQUIPO
        WHERE E.ID_EQUIPO = ? AND J.POSICION = ?
        """;

        ArrayList<Jugador> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setString(2, posicion);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));

                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("E_NOMBRE"));
                j.setEquipo(e);

                lista.add(j);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error listarJugadoresPorEquipoYPosicion: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<Jugador> listarJugadoresPorPosicion(String posicion) throws RepositoryException {
        String sql = """
        SELECT J.ID_JUGADOR, J.NOMBRE, J.APELLIDO, J.NUMERO_CAMISETA,J.POSICION,
               E.ID_EQUIPO, E.NOMBRE AS E_NOMBRE
        FROM JUGADOR J
        JOIN EQUIPO E ON J.ID_EQUIPO = E.ID_EQUIPO 
        WHERE POSICION = ?
        """;

        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, posicion);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Jugador j = new Jugador();
                    j.setId(rs.getInt("ID_JUGADOR"));
                    j.setNombre(rs.getString("NOMBRE"));
                    j.setApellido(rs.getString("APELLIDO"));
                    j.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));
                    j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));

                    Equipo e = new Equipo();
                    e.setId(rs.getInt("ID_EQUIPO"));
                    e.setNombre(rs.getString("E_NOMBRE"));
                    j.setEquipo(e);

                    lista.add(j);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error listarJugadoresPorPosicion: " + e.getMessage());
        }
        return lista;
    }

    //consulta intermedia 1

    public ArrayList<Jugador> listarJugadoresConEquipo() throws RepositoryException {
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE AS J_NOMBRE, j.APELLIDO AS J_APELLIDO, " +
                "j.POSICION, j.NUMERO_CAMISETA, e.ID_EQUIPO, e.NOMBRE AS E_NOMBRE " +
                "FROM JUGADOR j JOIN EQUIPO e ON j.ID_EQUIPO = e.ID_EQUIPO";
        ArrayList<Jugador> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("J_NOMBRE"));
                j.setApellido(rs.getString("J_APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                j.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));

                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("E_NOMBRE"));
                j.setEquipo(e);

                lista.add(j);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error listarJugadoresConEquipo: " + e.getMessage());
        }
        return lista;
    }

    //consulta intermedia 4
    public ArrayList<Jugador> listarJugadoresConContratoYEquipo() throws RepositoryException {
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE AS J_NOMBRE, j.APELLIDO AS J_APELLIDO, " +
                "e.ID_EQUIPO, e.NOMBRE AS E_NOMBRE, " +
                "c.ID_CONTRATO, c.SALARIO, c.FECHA_INICIO, c.FECHA_FIN " +
                "FROM JUGADOR j JOIN CONTRATO c ON c.ID_JUGADOR = j.ID_JUGADOR " +
                "JOIN EQUIPO e ON j.ID_EQUIPO = e.ID_EQUIPO";
        ArrayList<Jugador> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("J_NOMBRE"));
                j.setApellido(rs.getString("J_APELLIDO"));

                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("E_NOMBRE"));
                j.setEquipo(e);

                Contrato c = new Contrato();
                c.setIdContrato(rs.getInt("ID_CONTRATO"));
                c.setSalario(rs.getDouble("SALARIO"));
                c.setFechaInicio(rs.getDate("FECHA_INICIO").toLocalDate());
                c.setFechaFin(rs.getDate("FECHA_FIN").toLocalDate());
                c.setJugador(j);

                ArrayList<Contrato> contratos = new ArrayList<>();
                contratos.add(c);
                j.setListaContratos(contratos);

                lista.add(j);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarJugadoresConContratoYEquipo: " + ex.getMessage());
        }
        return lista;
    }

    //consulta intermedia 7
    public ArrayList<Jugador> listarCapitanes() throws RepositoryException {
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, e.ID_EQUIPO, e.NOMBRE AS E_NOMBRE " +
                "FROM JUGADOR j JOIN EQUIPO e ON e.ID_JUGADOR_CAPITAN = j.ID_JUGADOR";
        ArrayList<Jugador> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));

                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("E_NOMBRE"));
                j.setEquipo(e);

                lista.add(j);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarCapitanes: " + ex.getMessage());
        }
        return lista;
    }


//consulta avanzada 1

    public ArrayList<Jugador> listarJugadoresConSalarioSuperiorPromedio() throws RepositoryException {
        String sql = """
                SELECT j.ID_JUGADOR,
                   j.NOMBRE AS J_NOMBRE,
                   j.APELLIDO AS J_APELLIDO,
                   c.SALARIO,
                   e.NOMBRE AS E_NOMBRE
            FROM JUGADOR j
            JOIN EQUIPO e ON e.ID_EQUIPO = j.ID_EQUIPO
            JOIN CONTRATO c ON c.ID_JUGADOR = j.ID_JUGADOR
            WHERE c.SALARIO > (SELECT AVG(SALARIO) FROM CONTRATO)
              """;
        ArrayList<Jugador> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("J_NOMBRE"));
                j.setApellido(rs.getString("J_APELLIDO"));

                Equipo e = new Equipo();
                e.setNombre(rs.getString("E_NOMBRE"));
                j.setEquipo(e);

                Contrato c = new Contrato();
                c.setSalario(rs.getDouble("SALARIO"));

                if (j.getListaContratos() == null) {
                    j.setListaContratos(new ArrayList<>());
                }
                j.getListaContratos().add(c);

                lista.add(j);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarJugadoresConSalarioSuperiorPromedio: " + ex.getMessage());
        }
        return lista;
    }


//consulta avanzada 2

    public ArrayList<Jugador> listarJugadoresConGolesEnMasDeUnPartido() throws RepositoryException {
        String sql = """
        SELECT DISTINCT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION, j.NUMERO_CAMISETA,
               e.ID_EQUIPO, e.NOMBRE AS E_NOMBRE,
               COUNT(DISTINCT g.ID_PARTIDO) AS PARTIDOS_CON_GOLES
        FROM JUGADOR j
        JOIN EQUIPO e ON j.ID_EQUIPO = e.ID_EQUIPO
        JOIN GOL g ON g.ID_JUGADOR = j.ID_JUGADOR
        GROUP BY j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION, j.NUMERO_CAMISETA, e.ID_EQUIPO, e.NOMBRE
        HAVING COUNT(DISTINCT g.ID_PARTIDO) > 1
        ORDER BY PARTIDOS_CON_GOLES DESC
    """;

        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                j.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));

                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("E_NOMBRE"));
                j.setEquipo(equipo);

                lista.add(j);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarJugadoresConGolesEnMasDeUnPartido: " + ex.getMessage());
        }

        return lista;
    }

    //avanzada 3

    public ArrayList<Jugador> listarJugadoresSinContratoActivo() throws RepositoryException {
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO FROM JUGADOR j WHERE j.ID_JUGADOR NOT IN (" +
                "SELECT c.ID_JUGADOR FROM CONTRATO c WHERE SYSDATE BETWEEN c.FECHA_INICIO AND c.FECHA_FIN)";
        ArrayList<Jugador> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                lista.add(j);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error listarJugadoresSinContratoActivo: " + ex.getMessage());
        }
        return lista;
    }
//avanzado 5

    public ArrayList<Jugador> listarDelanterosSinGoles() throws RepositoryException {
        String sql = """
                SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION,e.ID_EQUIPO,e.NOMBRE AS EQUIPO
                FROM JUGADOR j
                JOIN EQUIPO e ON e.ID_EQUIPO = j.ID_EQUIPO
                WHERE j.POSICION = 'Delantero'
                AND j.ID_JUGADOR NOT IN (SELECT g.ID_JUGADOR FROM GOL g)
""";

        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));

                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("EQUIPO"));

                j.setEquipo(equipo);
                lista.add(j);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarDelanterosSinGoles: " + ex.getMessage());
        }

        return lista;
    }
//avanzada 6

    public ArrayList<Jugador> listarJugadoresConMayorSalarioPorPosicion() throws RepositoryException {
        String sql = """ 
                SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION,
                e.ID_EQUIPO, e.NOMBRE AS NOMBRE_EQUIPO, c.SALARIO
                FROM JUGADOR j
                JOIN CONTRATO c ON c.ID_JUGADOR = j.ID_JUGADOR
                JOIN EQUIPO e ON e.ID_EQUIPO = j.ID_EQUIPO
                WHERE c.SALARIO = (
                SELECT MAX(c2.SALARIO) FROM CONTRATO c2
                JOIN JUGADOR j2 ON c2.ID_JUGADOR = j2.ID_JUGADOR
                WHERE j2.POSICION = j.POSICION)
""";
        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));

                Contrato c = new Contrato();
                c.setSalario(rs.getDouble("SALARIO"));
                j.setListaContratos(new ArrayList<>(List.of(c)));

                Equipo e = new Equipo();
                e.setId(rs.getInt("ID_EQUIPO"));
                e.setNombre(rs.getString("NOMBRE_EQUIPO"));
                j.setEquipo(e);

                lista.add(j);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarJugadoresConMayorSalarioPorPosicion: " + ex.getMessage());
        }

        return lista;
    }

    //auxiliares de buscar por id

    private ArrayList<Contrato> listarContratosPorJugador(int idJugador) throws SQLException {
        ArrayList<Contrato> lista = new ArrayList<>();
        String sql = "SELECT * FROM CONTRATO WHERE ID_JUGADOR = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contrato contrato = new Contrato();
                    contrato.setIdContrato(rs.getInt("ID_CONTRATO"));
                    contrato.setFechaInicio(rs.getDate("FECHA_INICIO").toLocalDate());
                    contrato.setFechaFin(rs.getDate("FECHA_FIN").toLocalDate());
                    contrato.setSalario(rs.getDouble("SALARIO"));
                    lista.add(contrato);
                }
            }
        }
        return lista;
    }


    private ArrayList<Gol> listarGolesPorJugador(int idJugador) throws SQLException {
        ArrayList<Gol> lista = new ArrayList<>();
        String sql = """
        SELECT g.ID_GOL, g.NUMERO_GOLES, p.ID_PARTIDO, p.FECHA, p.HORA
        FROM GOL g
        JOIN PARTIDO p ON g.ID_PARTIDO = p.ID_PARTIDO
        WHERE g.ID_JUGADOR = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));

                    Gol gol = new Gol();
                    gol.setIdGol(rs.getInt("ID_GOL"));
                    gol.setNumeroGoles(rs.getInt("NUMERO_GOLES"));
                    gol.setPartido(partido);
                    lista.add(gol);
                }
            }
        }
        return lista;
    }


    private ArrayList<Tarjeta> listarTarjetasPorJugador(int idJugador) throws SQLException {
        ArrayList<Tarjeta> lista = new ArrayList<>();
        String sql = """
        SELECT t.ID_TARJETA, t.TIPO, p.ID_PARTIDO, p.FECHA, p.HORA
        FROM TARJETA t
        JOIN PARTIDO p ON t.ID_PARTIDO = p.ID_PARTIDO
        WHERE t.ID_JUGADOR = ?
    """;
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));

                    Tarjeta tarjeta = new Tarjeta();
                    tarjeta.setIdTarjeta(rs.getInt("ID_TARJETA"));
                    tarjeta.setTipo(TipoTarjeta.valueOf(rs.getString("TIPO")));
                    tarjeta.setPartido(partido);
                    lista.add(tarjeta);
                }
            }
        }
        return lista;
    }

    private ArrayList<Sancion> listarSancionesPorJugador(int idJugador) throws SQLException {
        ArrayList<Sancion> lista = new ArrayList<>();
        String sql = "SELECT * FROM SANCION WHERE ID_JUGADOR = ?";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sancion sancion = new Sancion();
                    sancion.setIdSancion(rs.getInt("ID_SANCION"));
                    sancion.setFecha(rs.getDate("FECHA").toLocalDate());
                    sancion.setMotivo(rs.getString("MOTIVO"));
                    sancion.setDuracion(rs.getInt("DURACION"));
                    sancion.setTipo(rs.getString("TIPO"));
                    lista.add(sancion);
                }
            }
        }
        return lista;
    }

    private ArrayList<Sustitucion> listarSustitucionesPorJugador(int idJugador, boolean entra) throws SQLException {
        ArrayList<Sustitucion> lista = new ArrayList<>();
        String columna = entra ? "ID_JUGADOR_ENTRA" : "ID_JUGADOR_SALE";
        String sql = """
        SELECT s.ID_SUSTITUCION, s.ID_PARTIDO, s.ID_JUGADOR_ENTRA, s.ID_JUGADOR_SALE,
               p.FECHA, p.HORA
        FROM SUSTITUCION s
        JOIN PARTIDO p ON s.ID_PARTIDO = p.ID_PARTIDO
        WHERE s.%s = ?
    """.formatted(columna);

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Partido partido = new Partido();
                    partido.setIdPartido(rs.getInt("ID_PARTIDO"));
                    partido.setFecha(rs.getDate("FECHA").toLocalDate());
                    partido.setHora(rs.getString("HORA"));

                    Sustitucion sust = new Sustitucion();
                    sust.setIdSustitucion(rs.getInt("ID_SUSTITUCION"));
                    sust.setPartido(partido);
                    lista.add(sust);
                }
            }
        }
        return lista;
    }




}
