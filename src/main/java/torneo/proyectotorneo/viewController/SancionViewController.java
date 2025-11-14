package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SancionViewController {

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoSancion;

    @FXML
    private ComboBox<?> cmbEquipo;

    @FXML
    private ComboBox<?> cmbTipo;

    @FXML
    private TableColumn<?, ?> colAcciones;

    @FXML
    private TableColumn<?, ?> colDuracion;

    @FXML
    private TableColumn<?, ?> colFecha;

    @FXML
    private TableColumn<?, ?> colMotivo;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colNumero;

    @FXML
    private TableColumn<?, ?> colTipo;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<?> tableSancion;

    @FXML
    private TextField txtBuscar;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoSancion(ActionEvent event) {

    }

}
