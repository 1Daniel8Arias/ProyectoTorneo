package torneo.proyectotorneo.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.LoginController;
import torneo.proyectotorneo.model.Usuario;
import torneo.proyectotorneo.utils.MensajeUtil;

public class LoginviewController {

private LoginController loginController;

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtUsuario;

    @FXML
    void initialize() {
        loginController=new LoginController();
    }

    @FXML
    void iniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contrasenia = txtContrasena.getText();

        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            MensajeUtil.mostrarAdvertencia("Debe completar todos los campos");
            return;
        }

        Usuario usuario1 = loginController.verificarUsuario(usuario, contrasenia);

        if (usuario1 == null) {
            MensajeUtil.mostrarError("Usuario o contraseña incorrectos");
            return;
        }


        String rol = String.valueOf(usuario1.getRol());

        if (rol.equalsIgnoreCase("admin")) {
            MensajeUtil.mostrarConfirmacion("Bienvenido Administrador " + usuario1.getNombreUsuario());
            // Aquí puedes abrir la ventana de administrador
            abrirVistaAdmin();
        } else if (rol.equalsIgnoreCase("user")) {
            MensajeUtil.mostrarConfirmacion("Bienvenido Usuario " + usuario1.getNombreUsuario());
            // Aquí puedes abrir la ventana de usuario normal
            abrirVistaUsuario();
        } else {
            MensajeUtil.mostrarAdvertencia("Rol desconocido: " + rol);
        }


    }

    private void abrirVistaUsuario() {
    }

    private void abrirVistaAdmin() {
        try {
            // Cargar la vista HomeView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/HOME.fxml"));
            Parent root = loader.load();

            HomeViewController controller = loader.getController();


            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Torneo de futbol colombianao " );
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            MensajeUtil.mostrarError("no se pudo abrir la vista");
        }
    }

}
