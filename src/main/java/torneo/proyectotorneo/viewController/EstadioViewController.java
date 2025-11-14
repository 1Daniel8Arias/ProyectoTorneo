package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class EstadioViewController {

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoEstadio;

    @FXML
    private ComboBox<?> cmbEquipo;

    @FXML
    private ComboBox<?> cmbTipo;

    @FXML
    private TableColumn<?, ?> colAcciones;

    @FXML
    private TableColumn<?, ?> colCapacidad;

    @FXML
    private TableColumn<?, ?> colEquipo;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colNumero;

    @FXML
    private TableColumn<?, ?> colTipo;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<?> tableEstadio;

    @FXML
    private TextField txtBuscar;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoEstadio(ActionEvent event) {

    }

}
