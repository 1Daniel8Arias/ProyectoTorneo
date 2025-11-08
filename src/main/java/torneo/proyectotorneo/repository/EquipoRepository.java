package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.*;
import java.util.ArrayList;

public class EquipoRepository implements Repository<Equipo> {

    @Override
    public ArrayList listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM EQUIPO";
        ArrayList<Equipo> equipos = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo equipo = new Equipo();
                equipo.setId(rs.getInt("ID_EQUIPO"));
                equipo.setNombre(rs.getString("NOMBRE"));
                equipos.add(equipo);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los equipos "+ e.getMessage());
        }

        return equipos;
    }

    @Override
    public Equipo buscarPorId(int id) throws RepositoryException {
        String sql="SELECT * FROM EQUIPO WHERE ID_EQUIPO =?";
        Equipo equipo=null;

        try (Connection conn= Conexion.getInstance();
        PreparedStatement ps= conn.prepareStatement(sql);
        ){
            ps.setInt(1,id);

            try (ResultSet rs= ps.executeQuery();){
                if (rs.next()){
                    equipo=new Equipo();
                    equipo.setId(rs.getInt("ID_EQUIPO"));
                    equipo.setNombre(rs.getString("NOMBRE"));


                }
            }


        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el equipo por ID "+ e.getMessage());
        }
        return equipo;
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
            throw new RepositoryException("Error al guardar el equipo "+e.getMessage());
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
            throw new RepositoryException("Error al actualizar el equipo "+ e.getMessage());
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
            throw new RepositoryException("Error al eliminar el equipo "+ e.getMessage());
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

//avanzada 3
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


}
