package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.model.enums.TipoTarjeta;

public class DetalleJugadorViewController {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML private Button btnVolver;

    // ---- Tabla Goles ----
    @FXML private TableView<Gol> tablaGoles;
    @FXML private TableColumn<Gol, String> colGolPartido;
    @FXML private TableColumn<Gol, String> colGolFecha;
    @FXML private TableColumn<Gol, Integer> colCantidadGoles;

    // ---- Tabla Tarjetas ----
    @FXML private TableView<Tarjeta> tablaTarjetas;
    @FXML private TableColumn<Tarjeta, String> colTarjetaPartido;
    @FXML private TableColumn<Tarjeta, String> colTarjetaFecha;
    @FXML private TableColumn<Tarjeta, String> colTipoTarjeta;

    // ---- Tabla Sanciones ----
    @FXML private TableView<Sancion> tablaSanciones;
    @FXML private TableColumn<Sancion, String> colSancionFecha;
    @FXML private TableColumn<Sancion, String> colSancionMotivo;
    @FXML private TableColumn<Sancion, String> colSancionTipo;
    @FXML private TableColumn<Sancion, Integer> colSancionDuracion;

    // ---- Labels ----
    @FXML private Label lblEquipo;
    @FXML private Label lblFechaFin;
    @FXML private Label lblFechaInicio;
    @FXML private Label lblIdJugador;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblNombreJugador;
    @FXML private Label lblNumero;
    @FXML private Label lblPosicion;
    @FXML private Label lblSalario;

    @FXML
    void handleVolver(ActionEvent event) {
        // Cierra la ventana actual
        ((Stage) btnVolver.getScene().getWindow()).close();
    }

    @FXML
    void initialize() {
        configurarTablas();
    }

    /**
     * Configura las columnas de las tablas para mostrar los datos de cada relación.
     */
    private void configurarTablas() {
        // Tabla de goles
        colGolPartido.setCellValueFactory(cellData -> {
            Partido p = cellData.getValue().getPartido();
            return new javafx.beans.property.SimpleStringProperty(
                    p != null ? "Partido #" + p.getIdPartido() : "-"
            );
        });
        colGolFecha.setCellValueFactory(cellData -> {
            Partido p = cellData.getValue().getPartido();
            return new javafx.beans.property.SimpleStringProperty(
                    p != null && p.getFecha() != null ? dateFormatter.format(p.getFecha()) : "-"
            );
        });
        colCantidadGoles.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("numeroGoles"));

        // Tabla de tarjetas
        colTarjetaPartido.setCellValueFactory(cellData -> {
            Partido p = cellData.getValue().getPartido();
            return new javafx.beans.property.SimpleStringProperty(
                    p != null ? "Partido #" + p.getIdPartido() : "-"
            );
        });
        colTarjetaFecha.setCellValueFactory(cellData -> {
            Partido p = cellData.getValue().getPartido();
            return new javafx.beans.property.SimpleStringProperty(
                    p != null && p.getFecha() != null ? dateFormatter.format(p.getFecha()) : "-"
            );
        });
        colTipoTarjeta.setCellValueFactory(cellData -> {
            TipoTarjeta tipo = cellData.getValue().getTipo();
            return new javafx.beans.property.SimpleStringProperty(
                    tipo != null ? tipo.name() : "-"
            );
        });

        // Tabla de sanciones
        colSancionFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFecha() != null ? dateFormatter.format(cellData.getValue().getFecha()) : "-"
        ));
        colSancionMotivo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("motivo"));
        colSancionTipo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tipo"));
        colSancionDuracion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("duracion"));
    }

    /**
     * Muestra en pantalla todos los datos del jugador y sus relaciones.
     */
    public void mostrarDetalles(Jugador jugador) {
        // ---- Datos básicos ----
        lblNombreJugador.setText(jugador.getNombreCompleto());
        lblNombreCompleto.setText(jugador.getNombre() + " " + jugador.getApellido());
        lblIdJugador.setText(String.valueOf(jugador.getId()));
        lblEquipo.setText(jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : "-");
        lblPosicion.setText(jugador.getPosicion() != null ? jugador.getPosicion().name() : "-");
        lblNumero.setText(jugador.getNumeroCamiseta());

        // ---- Contrato ----
        if (jugador.getListaContratos() != null && !jugador.getListaContratos().isEmpty()) {
            Contrato contrato = jugador.getListaContratos().get(0);
            lblFechaInicio.setText(contrato.getFechaInicio() != null ? dateFormatter.format(contrato.getFechaInicio()) : "-");
            lblFechaFin.setText(contrato.getFechaFin() != null ? dateFormatter.format(contrato.getFechaFin()) : "-");
            lblSalario.setText(String.format("$%,.2f", contrato.getSalario()));
        } else {
            lblFechaInicio.setText("-");
            lblFechaFin.setText("-");
            lblSalario.setText("-");
        }

        // ---- Listas ----
        if (jugador.getListaGoles() != null)
            tablaGoles.setItems(FXCollections.observableArrayList(jugador.getListaGoles()));
        if (jugador.getListaTarjetas() != null)
            tablaTarjetas.setItems(FXCollections.observableArrayList(jugador.getListaTarjetas()));
        if (jugador.getListaSanciones() != null)
            tablaSanciones.setItems(FXCollections.observableArrayList(jugador.getListaSanciones()));
    }
}
