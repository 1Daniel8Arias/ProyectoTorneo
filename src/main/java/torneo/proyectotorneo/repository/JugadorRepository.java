package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JugadorRepository implements Repository<Jugador> {
    @Override
    public ArrayList<Jugador> listarTodos() throws RepositoryException {
        String sql="SELECT * FROM JUGADOR";
        ArrayList<Jugador>jugadores = new ArrayList<>();

        try(Connection conn=Conexion.getInstance();
        PreparedStatement ps= conn.prepareStatement(sql);
            ResultSet rs= ps.executeQuery();){
            while (rs.next()){
                Jugador jugador=new Jugador();
                jugador.setId(rs.getInt("ID_JUGADOR"));
                jugador.setNombre(rs.getString("NOMBRE"));
                jugador.setApellido(rs.getString("APELLIDO"));
                jugador.setPosicion(PosicionJugador.valueOf(rs.getString("POSICION")));
                jugador.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));
                jugadores.add(jugador);


            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el jugadores : " + e.getMessage());
        }

        return null;
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
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el jugador por ID: " + e.getMessage());
        }

        return jugador;
    }

    @Override
    public void guardar(Jugador jugador) throws RepositoryException {
        String sql="INSERT INTO JUGADOR(NOMBRE,APELLIDO,POSICION,NUMERO_CAMISETA,ID_EQUIPO) VALUES(?;?;?;?,?)";
        try (Connection conn =Conexion.getInstance();
        PreparedStatement ps=conn.prepareStatement(sql)){

            ps.setString(1,jugador.getNombre());
            ps.setString(2, jugador.getApellido());
            ps.setString(3,jugador.getPosicion().name());
            ps.setString(4, jugador.getNumeroCamiseta());
            ps.setInt(5,jugador.getEquipo().getId());
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
        String sql ="DELETE FROM JUGADOR WHERE ID_JUGADOR=?";
        try (Connection conn=Conexion.getInstance();
        PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el jugador: " + e.getMessage());

        }

    }
}
