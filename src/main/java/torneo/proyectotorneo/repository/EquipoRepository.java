package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
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
}
