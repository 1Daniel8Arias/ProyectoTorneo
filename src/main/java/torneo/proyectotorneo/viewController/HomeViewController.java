package torneo.proyectotorneo.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HomeViewController {

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
    private Button arbitros;

    @FXML
    private Button btnEquipos;

    @FXML
    private Button btnEstadisticas;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnJugadores;

    @FXML
    private Button btnPartidos;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnSanciones;

    @FXML
    private Button btnTabla;

    @FXML
    private TableColumn<?, ?> colEquipo;

    @FXML
    private TableColumn<?, ?> colPartidosGanados;

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

}
