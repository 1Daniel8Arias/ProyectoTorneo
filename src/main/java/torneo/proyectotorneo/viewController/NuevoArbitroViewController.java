package torneo.proyectotorneo.viewController;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.ArbitroController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.utils.MensajeUtil;

public class NuevoArbitroViewController {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;

    private ObservableList<Arbitro> arbitros;
    private Arbitro arbitro;

    private final ArbitroController arbitroController = new ArbitroController();

    public Arbitro getArbitro() {
        return arbitro;
    }

    public void initAttributtes(ObservableList<Arbitro> lista) {
        this.arbitros = lista;
        this.lblTitulo.setText("Registrar Nuevo Árbitro");
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        cerrar();
    }

    @FXML
    void handleGuardar(ActionEvent event) {
        try {
            // Validación
            if (txtNombre.getText().trim().isEmpty() ||
                    txtApellido.getText().trim().isEmpty()) {

                MensajeUtil.mostrarError("Debe llenar todos los campos.");
                return;
            }

            // Crear el árbitro
            Arbitro nuevo = new Arbitro();
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setApellido(txtApellido.getText().trim());

            // Guardar en BD
            arbitroController.guardarArbitro(nuevo);

            // Validar ID


            // Devuelvo el árbitro a la ventana anterior
            this.arbitro = nuevo;

            MensajeUtil.mostrarConfirmacion("Árbitro registrado correctamente.");
            cerrar();

        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al guardar árbitro: " + e.getMessage());
        }
    }

    private void cerrar() {
        Stage st = (Stage) btnCancelar.getScene().getWindow();
        st.close();
    }
}
