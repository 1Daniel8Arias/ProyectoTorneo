package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DetalleJugadorViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnVolver;

    @FXML
    private TableColumn<?, ?> colCantidadGoles;

    @FXML
    private TableColumn<?, ?> colGolFecha;

    @FXML
    private TableColumn<?, ?> colGolPartido;

    @FXML
    private TableColumn<?, ?> colSancionDuracion;

    @FXML
    private TableColumn<?, ?> colSancionFecha;

    @FXML
    private TableColumn<?, ?> colSancionMotivo;

    @FXML
    private TableColumn<?, ?> colSancionTipo;

    @FXML
    private TableColumn<?, ?> colTarjetaFecha;

    @FXML
    private TableColumn<?, ?> colTarjetaPartido;

    @FXML
    private TableColumn<?, ?> colTipoTarjeta;

    @FXML
    private Label lblEquipo;

    @FXML
    private Label lblFechaFin;

    @FXML
    private Label lblFechaInicio;

    @FXML
    private Label lblIdJugador;

    @FXML
    private Label lblNombreCompleto;

    @FXML
    private Label lblNombreJugador;

    @FXML
    private Label lblNumero;

    @FXML
    private Label lblPosicion;

    @FXML
    private Label lblSalario;

    @FXML
    private TableView<?> tablaGoles;

    @FXML
    private TableView<?> tablaSanciones;

    @FXML
    private TableView<?> tablaTarjetas;

    @FXML
    void handleVolver(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert btnVolver != null : "fx:id=\"btnVolver\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colCantidadGoles != null : "fx:id=\"colCantidadGoles\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colGolFecha != null : "fx:id=\"colGolFecha\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colGolPartido != null : "fx:id=\"colGolPartido\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colSancionDuracion != null : "fx:id=\"colSancionDuracion\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colSancionFecha != null : "fx:id=\"colSancionFecha\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colSancionMotivo != null : "fx:id=\"colSancionMotivo\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colSancionTipo != null : "fx:id=\"colSancionTipo\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colTarjetaFecha != null : "fx:id=\"colTarjetaFecha\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colTarjetaPartido != null : "fx:id=\"colTarjetaPartido\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert colTipoTarjeta != null : "fx:id=\"colTipoTarjeta\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblEquipo != null : "fx:id=\"lblEquipo\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblFechaFin != null : "fx:id=\"lblFechaFin\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblFechaInicio != null : "fx:id=\"lblFechaInicio\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblIdJugador != null : "fx:id=\"lblIdJugador\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblNombreCompleto != null : "fx:id=\"lblNombreCompleto\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblNombreJugador != null : "fx:id=\"lblNombreJugador\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblNumero != null : "fx:id=\"lblNumero\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblPosicion != null : "fx:id=\"lblPosicion\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert lblSalario != null : "fx:id=\"lblSalario\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert tablaGoles != null : "fx:id=\"tablaGoles\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert tablaSanciones != null : "fx:id=\"tablaSanciones\" was not injected: check your FXML file 'DetalleJugador.fxml'.";
        assert tablaTarjetas != null : "fx:id=\"tablaTarjetas\" was not injected: check your FXML file 'DetalleJugador.fxml'.";

    }

}
