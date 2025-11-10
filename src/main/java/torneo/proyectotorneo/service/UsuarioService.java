package torneo.proyectotorneo.service;


import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.exeptions.UsuarioNoEncontradoException;

import torneo.proyectotorneo.model.Usuario;
import torneo.proyectotorneo.repository.UsuarioRepository;

public class UsuarioService {
    private   UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }


    public Usuario buscarUsuario(String usuario, String contrasenia) {
        try {
        Usuario usuario1 = usuarioRepository.buscarUsuario(usuario,contrasenia);
        if (usuario1 == null) {
            throw new UsuarioNoEncontradoException("no se encontro el usurio");
        }
        return usuario1;
        } catch (RepositoryException e) {
            throw new UsuarioNoEncontradoException("Error al buscar el usuario: " + e.getMessage());
        }

    }


}
