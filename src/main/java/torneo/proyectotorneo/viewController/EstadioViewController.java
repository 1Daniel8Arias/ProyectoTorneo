package torneo.proyectotorneo.viewController;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import torneo.proyectotorneo.controller.EstadioController;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.model.enums.TipoSede;

public class EstadioViewController {
    EstadioController controller;
    private ObservableList<Estadio> estadiosObservable;


    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoEstadio;

    @FXML
    private ComboBox<String> cmbEquipo;

    @FXML
    private ComboBox<TipoSede> cmbTipo;

    @FXML
    private TableColumn<Estadio, Void> colAcciones;

    @FXML
    private TableColumn<Estadio, Integer> colCapacidad;

    @FXML
    private TableColumn<Estadio, String> colCiudad;

    @FXML
    private TableColumn<Estadio, String> colDepartamento;

    @FXML
    private TableColumn<Estadio, String> colEquipo;

    @FXML
    private TableColumn<Estadio, String> colMunicipio;

    @FXML
    private TableColumn<Estadio, String> colNombre;

    @FXML
    private TableColumn<Estadio, String> colNumero;

    @FXML
    private TableColumn<Estadio,String> colTipo;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<Estadio> tableEstadio;

    @FXML
    private TextField txtBuscar;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoEstadio(ActionEvent event) {

    }

}
