package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class JugadorViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoJugador;

    @FXML
    private ComboBox<?> cmbEquipo;

    @FXML
    private ComboBox<?> cmbPosicion;

    @FXML
    private TableColumn<?, ?> colAcciones;

    @FXML
    private TableColumn<?, ?> colContrato;

    @FXML
    private TableColumn<?, ?> colEdad;

    @FXML
    private TableColumn<?, ?> colEquipo;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colNumCamiseta;

    @FXML
    private TableColumn<?, ?> colNumero;

    @FXML
    private TableColumn<?, ?> colPosicion;

    @FXML
    private Label lblContador;

    @FXML
    private MenuItem menuCapitanes;

    @FXML
    private MenuItem menuDelanterosSinGoles;

    @FXML
    private MenuItem menuGolesVariosPartidos;

    @FXML
    private MenuItem menuJugadoresConContrato;

    @FXML
    private MenuItem menuJugadoresConEquipo;

    @FXML
    private MenuItem menuMayorSalarioPosicion;

    @FXML
    private MenuItem menuMostrarTodos;

    @FXML
    private MenuItem menuSalarioSuperior;

    @FXML
    private MenuItem menuSinContratoActivo;

    @FXML
    private TableView<?> tableJugadores;

    @FXML
    private TextField txtBuscar;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleCapitanes(ActionEvent event) {

    }

    @FXML
    void handleDelanterosSinGoles(ActionEvent event) {

    }

    @FXML
    void handleGolesVariosPartidos(ActionEvent event) {

    }

    @FXML
    void handleJugadoresConContrato(ActionEvent event) {

    }

    @FXML
    void handleJugadoresConEquipo(ActionEvent event) {

    }

    @FXML
    void handleMayorSalarioPosicion(ActionEvent event) {

    }

    @FXML
    void handleMostrarTodos(ActionEvent event) {

    }

    @FXML
    void handleNuevoJugador(ActionEvent event) {

    }

    @FXML
    void handleSalarioSuperior(ActionEvent event) {

    }

    @FXML
    void handleSinContratoActivo(ActionEvent event) {

    }

    @FXML
    void initialize() {


    }

}
