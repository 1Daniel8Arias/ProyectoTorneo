package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import torneo.proyectotorneo.controller.HomeController;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.model.TablaPosicion;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

public class HomeViewController {

    private ModelFactoryController modelFactory;
    private HomeController homeController;

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

    @FXML private TableView<TablaPosicion> tablaPosiciones;
    @FXML private TableColumn<TablaPosicion, Integer> colPosicion;
    @FXML private TableColumn<TablaPosicion, String> colEquipo;
    @FXML private TableColumn<TablaPosicion, Integer> colPartidosJugados;
    @FXML private TableColumn<TablaPosicion, Integer> colPartidosGanados;
    @FXML private TableColumn<TablaPosicion, Integer> colPerdidos;
    @FXML private TableColumn<TablaPosicion, Integer> colPuntos;
    @FXML private TableColumn<TablaPosicion, Integer> colDiderencia;
    @FXML private TableColumn<TablaPosicion, Integer> colEmpates;
    @FXML private TableColumn<TablaPosicion, Integer> colEncontra;
    @FXML private TableColumn<TablaPosicion, Integer> colGolesAFavor;
    @FXML private VBox proximosPartidosContainer;
    @FXML private HBox resultadosRecientesContainer;

    private ObservableList<TablaPosicion> tablaPosicion;




    @FXML
    public void initialize() {
        homeController = new HomeController();

        configurarTabla();
        cargarDatos();
    }

    // ================== CONFIGURACIÓN TABLA ==================
    private void configurarTabla() {
        tablaPosicion = FXCollections.observableArrayList();
        this.tablaPosiciones.setItems(tablaPosicion);

        colPosicion.setCellValueFactory(cellData -> {
            TablaPosicion posicion = cellData.getValue();
            int index = tablaPosiciones.getItems().indexOf(posicion) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });
        this.colEquipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEquipo().getNombre()));
        colPartidosJugados.setCellValueFactory(cellData -> {
            TablaPosicion t = cellData.getValue();
            int total = t.getGanados() + t.getEmpates() + t.getPerdidos();
            return new javafx.beans.property.SimpleIntegerProperty(total).asObject();
        });
        this.colPartidosGanados.setCellValueFactory(new PropertyValueFactory<>("ganados"));
        this.colEmpates.setCellValueFactory(new PropertyValueFactory<>("empates"));
        this.colPerdidos.setCellValueFactory(new PropertyValueFactory<>("perdidos"));
        this.colGolesAFavor.setCellValueFactory(new PropertyValueFactory<>("golesAFavor"));
        this.colEncontra.setCellValueFactory(new PropertyValueFactory<>("golesEnContra"));
        this.colDiderencia.setCellValueFactory(new PropertyValueFactory<>("diferenciaGoles"));
        this.colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos"));

    }

    // ================== CARGA DE DATOS ==================
    private void cargarDatos() {
        cargarEstadisticas();
        cargarTablaPosiciones();

    }

    // ================== ESTADÍSTICAS ==================
    private void cargarEstadisticas() {
        LabelNumeroDeEquipos.setText(String.valueOf(homeController.obtenerNumeroEquipos()));
        LabelNumeroDeJugadores.setText(String.valueOf(homeController.obtenerNumeroJugadores()));
        LabelNumeroDePartidosJugados.setText(String.valueOf(homeController.obtenerNumeroPartidosJugados()));
        LabelNumeroDeJornadas.setText(String.valueOf(homeController.obtenerNumeroJornadas()));
    }

    // ================== TABLA DE POSICIONES ==================
    private void cargarTablaPosiciones() {
        ArrayList<TablaPosicion> posiciones = homeController.obtenerTablaPosiciones();
        tablaPosiciones.getItems().clear();
        tablaPosiciones.getItems().addAll(posiciones);
    }


}
