package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.EquipoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.utils.MensajeUtil;

public class NuevoEquipoViewController {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtNombre;

    private Equipo equipoCreado;

    private final EquipoController equipoController = new EquipoController();

    public Equipo getEquipoCreado() {
        return equipoCreado;
    }

    public void initAttributes() {
        lblTitulo.setText("Registrar Nuevo Equipo");
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    void handleGuardar(ActionEvent event) {
        try {
            // Validación
            String nombre = txtNombre.getText().trim();



            // Crear objeto Equipo
            Equipo eq = new Equipo();
            eq.setNombre(nombre);

            // Guardar en BD
            equipoController.guardarEquipo(eq);

            if (eq.getId() == null) {
                MensajeUtil.mostrarError("Error: el equipo no obtuvo un ID al guardarse.");
                return;
            }

            // Guardamos la instancia para retornarla al ViewController principal
            this.equipoCreado = eq;

            MensajeUtil.mostrarConfirmacion("Equipo registrado exitosamente.");

            cerrarVentana();

        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al guardar el equipo: " + e.getMessage());
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
