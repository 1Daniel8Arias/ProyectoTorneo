package torneo.proyectotorneo.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TablaViewController {

    @FXML
    private TableColumn<?, ?> colDiderencia;

    @FXML
    private TableColumn<?, ?> colEmpates;

    @FXML
    private TableColumn<?, ?> colEncontra;

    @FXML
    private TableColumn<?, ?> colEquipo;

    @FXML
    private TableColumn<?, ?> colGolesAFavor;

    @FXML
    private TableColumn<?, ?> colPartidosGanados;

    @FXML
    private TableColumn<?, ?> colPartidosJugados;

    @FXML
    private TableColumn<?, ?> colPerdidos;

    @FXML
    private TableColumn<?, ?> colPosicion;

    @FXML
    private TableColumn<?, ?> colPuntos;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<?> tablaPosiciones;

}
