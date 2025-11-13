package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import torneo.proyectotorneo.controller.PartidoController;
import torneo.proyectotorneo.model.Partido;

import java.time.format.DateTimeFormatter;

public class CardPartidoViewController {

    private Partido partido;
    private PartidoViewController partidoViewController;
    private PartidoController partidoController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Button btnDetalles;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private HBox hboxMarcador;
    @FXML private Label lblArbitro;
    @FXML private Label lblEquipoLocal;
    @FXML private Label lblEquipoVisitante;
    @FXML private Label lblEstadio;
    @FXML private Label lblEstado;
    @FXML private Label lblFecha;
    @FXML private Label lblGolesLocal;
    @FXML private Label lblGolesVisitante;
    @FXML private Label lblJornada;
    @FXML private Label lblPosicionLocal;
    @FXML private Label lblPosicionVisitante;
    @FXML private Label lblResultadoLocal;
    @FXML private Label lblResultadoVisitante;
    @FXML private Label lblVS;

    @FXML
    void initialize() {
        partidoController = new PartidoController();
    }

    /**
     * Configura la tarjeta con los datos del partido
     */
    public void setPartido(Partido partido) {
        this.partido = partido;
        cargarDatos();
    }

    /**
     * Establece la referencia al controlador principal
     */
    public void setPartidoViewController(PartidoViewController controller) {
        this.partidoViewController = controller;
    }

    /**
     * Carga los datos del partido en la tarjeta
     */
    private void cargarDatos() {
        if (partido == null) return;

        // Jornada
        if (partido.getJornada() != null) {
            lblJornada.setText("Jornada " + partido.getJornada().getNumeroJornada());
        }

        // Equipos
        if (partido.getEquipoLocal() != null) {
            lblEquipoLocal.setText(partido.getEquipoLocal().getNombre());
        }
        if (partido.getEquipoVisitante() != null) {
            lblEquipoVisitante.setText(partido.getEquipoVisitante().getNombre());
        }

        // Estado
        String estado = partidoController.obtenerEstadoPartido(partido);
        lblEstado.setText(estado);
        aplicarEstiloEstado(estado);

        // Marcador o VS
        if (partido.getResultadoFinal() != null) {
            // Mostrar marcador
            hboxMarcador.setVisible(true);
            hboxMarcador.setManaged(true);
            lblVS.setVisible(false);
            lblVS.setManaged(false);

            lblGolesLocal.setText(String.valueOf(partido.getResultadoFinal().getGolesLocal()));
            lblGolesVisitante.setText(String.valueOf(partido.getResultadoFinal().getGolesVisitante()));

            lblResultadoLocal.setText(String.valueOf(partido.getResultadoFinal().getGolesLocal()));
            lblResultadoVisitante.setText(String.valueOf(partido.getResultadoFinal().getGolesVisitante()));
        } else {
            // Mostrar VS
            hboxMarcador.setVisible(false);
            hboxMarcador.setManaged(false);
            lblVS.setVisible(true);
            lblVS.setManaged(true);

            lblResultadoLocal.setText("-");
            lblResultadoVisitante.setText("-");
        }

        // Posiciones
        lblPosicionLocal.setText("Local");
        lblPosicionVisitante.setText("Visitante");

        // Fecha y hora
        if (partido.getFecha() != null) {
            String fechaTexto = dateFormatter.format(partido.getFecha()) +
                    " - " + partido.getHora();
            lblFecha.setText(fechaTexto);
        }

        // Estadio
        if (partido.getEstadio() != null) {
            lblEstadio.setText(partido.getEstadio().getNombre());
        }

        // Árbitro
        String arbitro = partidoController.obtenerArbitroPrincipal(partido);
        lblArbitro.setText(arbitro);
    }

    /**
     * Aplica estilo según el estado del partido
     */
    private void aplicarEstiloEstado(String estado) {
        lblEstado.getStyleClass().removeAll("estado-programado", "estado-hoy", "estado-en-curso", "estado-finalizado");

        switch (estado.toLowerCase()) {
            case "programado":
                lblEstado.getStyleClass().add("estado-programado");
                lblEstado.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1e40af;");
                break;
            case "hoy":
                lblEstado.getStyleClass().add("estado-hoy");
                lblEstado.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e;");
                break;
            case "en curso":
                lblEstado.getStyleClass().add("estado-en-curso");
                lblEstado.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46;");
                break;
            case "finalizado":
                lblEstado.getStyleClass().add("estado-finalizado");
                lblEstado.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #374151;");
                break;
        }
    }

    @FXML
    void handleVerDetalles(ActionEvent event) {
        // TODO: Implementar vista de detalles del partido
        System.out.println("Ver detalles del partido: " + partido.getIdPartido());
    }

    @FXML
    void handleEditar(ActionEvent event) {
        if (partidoViewController != null) {
            partidoViewController.editarPartido(partido);
        }
    }

    @FXML
    void handleEliminar(ActionEvent event) {
        if (partidoViewController != null) {
            partidoViewController.eliminarPartido(partido);
        }
    }
}