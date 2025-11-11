package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Contrato;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.enums.PosicionJugador;
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
        String sql = "SELECT * FROM JUGADOR WHERE ID_JUGADOR = ?";
        Jugador jugador = null;

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
                    jugador.setEquipo(equipo);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el jugador por ID: " + e.getMessage());
        }

        return jugador;
    }

    @Override
    public void guardar(Jugador jugador) throws RepositoryException {
        String sql = "INSERT INTO JUGADOR(NOMBRE,APELLIDO,POSICION,NUMERO_CAMISETA,ID_EQUIPO) VALUES(?;?;?;?,?)";
        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getApellido());
            ps.setString(3, jugador.getPosicion().name());
            ps.setString(4, jugador.getNumeroCamiseta());
            ps.setInt(5, jugador.getEquipo().getId());
            ps.executeUpdate();

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
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO " +
                "FROM JUGADOR j JOIN CONTRATO c ON c.ID_JUGADOR = j.ID_JUGADOR " +
                "WHERE c.SALARIO > (SELECT AVG(SALARIO) FROM CONTRATO)";
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
            throw new RepositoryException("Error listarJugadoresConSalarioSuperiorPromedio: " + ex.getMessage());
        }
        return lista;
    }


//consulta avanzada 2

    public ArrayList<Jugador> listarJugadoresConGolesEnMasDeUnPartido() throws RepositoryException {
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO " +
                "FROM JUGADOR j WHERE j.ID_JUGADOR IN (" +
                "SELECT g.ID_JUGADOR FROM GOL g GROUP BY g.ID_JUGADOR HAVING COUNT(DISTINCT g.ID_PARTIDO) > 1)";
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
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION " +
                "FROM JUGADOR j " +
                "WHERE j.POSICION = 'Delantero' " +
                "AND j.ID_JUGADOR NOT IN (SELECT g.ID_JUGADOR FROM GOL g)";

        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION").toUpperCase())); // si es enum
                lista.add(j);
            }

        } catch (SQLException ex) {
            throw new RepositoryException("Error listarDelanterosSinGoles: " + ex.getMessage());
        }

        return lista;
    }
//avanzada 6

    public ArrayList<Jugador> listarJugadoresConMayorSalarioPorPosicion() throws RepositoryException {
        String sql = "SELECT j.ID_JUGADOR, j.NOMBRE, j.APELLIDO, j.POSICION, " +
                "e.ID_EQUIPO, e.NOMBRE AS NOMBRE_EQUIPO, c.SALARIO " +
                "FROM JUGADOR j " +
                "JOIN CONTRATO c ON c.ID_JUGADOR = j.ID_JUGADOR " +
                "JOIN EQUIPO e ON e.ID_EQUIPO = j.ID_EQUIPO " +
                "WHERE c.SALARIO = ( " +
                "SELECT MAX(c2.SALARIO) FROM CONTRATO c2 " +
                "JOIN JUGADOR j2 ON c2.ID_JUGADOR = j2.ID_JUGADOR " +
                "WHERE j2.POSICION = j.POSICION)";

        ArrayList<Jugador> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Jugador j = new Jugador();
                j.setId(rs.getInt("ID_JUGADOR"));
                j.setNombre(rs.getString("NOMBRE"));
                j.setApellido(rs.getString("APELLIDO"));
                j.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION").toUpperCase())); // si usas enum

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


}
