package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NuevoJugadorViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private CheckBox chkEsCapitan;

    @FXML
    private ComboBox<?> cmbEquipo;

    @FXML
    private ComboBox<?> cmbPosicion;

    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNumeroCamiseta;

    @FXML
    private TextField txtSalario;

    @FXML
    void handleCancelar(ActionEvent event) {

    }

    @FXML
    void handleGuardar(ActionEvent event) {

    }

    @FXML
    void initialize() {


    }

}
