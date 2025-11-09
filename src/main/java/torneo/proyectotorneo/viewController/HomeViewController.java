package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HomeViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label LabelNumeroDeEquipos;

    @FXML
    private Label LabelNumeroDeJornadas;

    @FXML
    private Label LabelNumeroDeJugadores;

    @FXML
    private Label LabelNumeroDePartidosJugados;

    @FXML
    private VBox VBoxContenido;

    @FXML
    private TableColumn<?, ?> colEquipo;

    @FXML
    private TableColumn<?, ?> colPartidosJugados;

    @FXML
    private TableColumn<?, ?> colPosicion;

    @FXML
    private TableColumn<?, ?> colPuntos;

    @FXML
    private VBox proximosPartidosContainer;

    @FXML
    private HBox resultadosRecientesContainer;

    @FXML
    private TableView<?> tablaPosiciones;

    @FXML
    void initialize() {

    }

}
