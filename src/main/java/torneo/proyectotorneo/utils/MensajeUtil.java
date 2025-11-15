package torneo.proyectotorneo.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

import java.util.Optional;


public class MensajeUtil {

    private static final String TITULO_APP = "Sistema de Gestión del Torneo";
    private static final String ICONO_PATH = "/images/ball.png"; // cambia según tu ruta de icono

    /** 🔹 Mensaje genérico */
    private static void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo != null ? titulo : TITULO_APP);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initStyle(StageStyle.UTILITY);
        configurarIcono(alert);
        alert.showAndWait();
    }

    /** ✅ Éxito */
    public static void mostrarExito(String mensaje) {
        mostrarMensaje("✅ Éxito", mensaje, Alert.AlertType.INFORMATION);
    }

    /** ⚠️ Advertencia */
    public static void mostrarAdvertencia(String mensaje) {
        mostrarMensaje("⚠️ Advertencia", mensaje, Alert.AlertType.WARNING);
    }

    /** ❌ Error */
    public static void mostrarError(String mensaje) {
        mostrarMensaje("❌ Error", mensaje, Alert.AlertType.ERROR);
    }

    /** ❓ Confirmación */
    public static boolean mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TITULO_APP);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initStyle(StageStyle.UTILITY);
        configurarIcono(alert);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    public static void mostrarInfo(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.INFORMATION);
    }

    /** Cargar icono en las alertas */
    private static void configurarIcono(Alert alert) {
        try {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(MensajeUtil.class.getResourceAsStream(ICONO_PATH)));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono: " + e.getMessage());
        }
    }
}
