package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.model.Usuario;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

public class LoginController {
    private  final ModelFactoryController modelFactory;

    public LoginController() {
        this.modelFactory = ModelFactoryController.getInstance();
    }

    public  Usuario verificarUsuario(String usuario, String contrasenia) {

        Usuario usuario1=modelFactory.obtenerUsuario(usuario,contrasenia);
        if (usuario1!=null){
            return usuario1;
        }

        return null;
    }
}
