package torneo.proyectotorneo.repository;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Usuario;
import torneo.proyectotorneo.repository.service.Repository;
import torneo.proyectotorneo.utils.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioRepository implements Repository<Usuario> {

    @Override
    public ArrayList<Usuario> listarTodos() throws RepositoryException {
        String sql = "SELECT * FROM USUARIO";
        ArrayList<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                // Nota: Usuario no tiene getters/setters porque no usa @Data
                // Necesitarías agregarlos o usar reflexión
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al listar los usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    @Override
    public Usuario buscarPorId(int id) throws RepositoryException {
        String sql = "SELECT * FROM USUARIO WHERE ID_USUARIO = ?";
        Usuario usuario = null;

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    // Asignar valores cuando la clase Usuario tenga getters/setters
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar el usuario por ID: " + e.getMessage());
        }

        return usuario;
    }

    @Override
    public void guardar(Usuario usuario) throws RepositoryException {
        String sql = "INSERT INTO USUARIO (NOMBRE_USUARIO, CONTRASENIA, TIPO) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Implementar cuando Usuario tenga getters
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar el usuario: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws RepositoryException {
        String sql = "UPDATE USUARIO SET NOMBRE_USUARIO = ?, CONTRASENIA = ?, TIPO = ? WHERE ID_USUARIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Implementar cuando Usuario tenga getters
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws RepositoryException {
        String sql = "DELETE FROM USUARIO WHERE ID_USUARIO = ?";

        try (Connection conn = Conexion.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el usuario: " + e.getMessage());
        }
    }
}