package torneo.proyectotorneo.viewController;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import torneo.proyectotorneo.model.Gol;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.model.Tarjeta;

public class DetalleJugadorViewController {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnVolver;

    @FXML private TableColumn<Gol, Integer> colCantidadGoles;
    @FXML private TableColumn<Gol, String> colGolFecha;
    @FXML private TableColumn<Gol, String> colGolPartido;

    @FXML private TableColumn<Tarjeta, String> colTarjetaPartido;
    @FXML private TableColumn<Tarjeta, String> colTarjetaFecha;
    @FXML private TableColumn<Tarjeta, String> colTipoTarjeta;

    @FXML private TableColumn<Sancion, String> colSancionFecha;
    @FXML private TableColumn<Sancion, String> colSancionMotivo;
    @FXML private TableColumn<Sancion, String> colSancionTipo;
    @FXML private TableColumn<Sancion, Integer> colSancionDuracion;

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

    @FXML private TableView<Gol> tablaGoles;

    @FXML private TableView<Sancion> tablaSanciones;

    @FXML private TableView<Tarjeta> tablaTarjetas;

    @FXML
    void handleVolver(ActionEvent event) {

    }

    @FXML
    void initialize() {


    }
    public void mostrarDetalles(Jugador jugador) {
        lblNombreJugador.setText(jugador.getNombreCompleto());
        lblNombreCompleto.setText(jugador.getNombre() + " " + jugador.getApellido());
        lblIdJugador.setText(String.valueOf(jugador.getId()));
        lblEquipo.setText(jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : "-");
        lblPosicion.setText(jugador.getPosicion() != null ? jugador.getPosicion().name() : "-");
        lblNumero.setText(jugador.getNumeroCamiseta());
        // si el jugador tiene contrato:
        if (jugador.getListaContratos() != null && !jugador.getListaContratos().isEmpty()) {
            var contrato = jugador.getListaContratos().get(0);
            lblFechaInicio.setText(contrato.getFechaInicio().toString());
            lblFechaFin.setText(contrato.getFechaFin().toString());
            lblSalario.setText(String.valueOf(contrato.getSalario()));
        }
    }


}
