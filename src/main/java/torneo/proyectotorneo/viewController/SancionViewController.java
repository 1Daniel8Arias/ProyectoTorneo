package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.model.enums.TipoSancion;

import java.time.LocalDate;

public class SancionViewController {

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoSancion;

    @FXML
    private ComboBox<String> cmbEquipo;

    @FXML
    private ComboBox<TipoSancion> cmbTipo;

    @FXML
    private TableColumn<Sancion, Void> colAcciones;

    @FXML
    private TableColumn<Sancion, Integer> colDuracion;

    @FXML
    private TableColumn<Sancion, LocalDate> colFecha;

    @FXML
    private TableColumn<Sancion, String> colMotivo;

    @FXML
    private TableColumn<Sancion, String> colNombre;

    @FXML
    private TableColumn<Sancion, Integer> colNumero;

    @FXML
    private TableColumn<Sancion, String> colTipo;

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
