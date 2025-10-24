package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jugador;
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
        ArrayList<Jugador>jugadores;

        try(Connection conn=Conexion.getInstance();
        PreparedStatement ps= conn.prepareStatement(sql);
            ResultSet rs= ps.executeQuery();){
            while (rs.next()){
                Jugador jugador=new Jugador();
                jugador.setId(rs.getInt("ID_JUGADOR"));
                jugador.setNombre(rs.getString("NOMBRE"));
                jugador.setApellido(rs.getString("APELLIDO"));
                jugador.setPosicion(rs.getC("POSICION"));
                jugador.setNumeroCamiseta(rs.getString("NUMERO_CAMISETA"));


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Jugador buscarPorId(int id) throws RepositoryException {
        return null;
    }

    @Override
    public void guardar(Jugador jugador) throws RepositoryException {

    }

    @Override
    public void actualizar(Jugador jugador) throws RepositoryException {

    }

    @Override
    public void eliminar(int t) throws RepositoryException {

    }
}
