package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

public class PartidoViewController {



    @FXML
    private TableColumn<?, ?> accionesColumn;

    @FXML
    private FlowPane activeFiltrosPane;

    @FXML
    private HBox adminActions;

    @FXML
    private TableColumn<?, ?> arbitroColumn;

    @FXML
    private ToggleButton enCursoToggle;

    @FXML
    private ComboBox<?> equipoComboBox;

    @FXML
    private TableColumn<?, ?> equipoLocalColumn;

    @FXML
    private TableColumn<?, ?> equipoVisitanteColumn;

    @FXML
    private TableColumn<?, ?> estadioColumn;

    @FXML
    private ComboBox<?> estadioComboBox;

    @FXML
    private TableColumn<?, ?> estadoColumn;

    @FXML
    private TableColumn<?, ?> fechaHoraColumn;

    @FXML
    private ToggleButton finalizadosToggle;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> jornadaColumn;

    @FXML
    private ComboBox<?> jornadaComboBox;

    @FXML
    private TableColumn<?, ?> marcadorColumn;

    @FXML
    private TableView<?> partidosTable;

    @FXML
    private ToggleButton programadosToggle;

    @FXML
    private Label resultadosLabel;

    @FXML
    private ComboBox<?> tipoEquipoComboBox;

    @FXML
    private ToggleButton todosToggle;

    @FXML
    void handleEstadoChanged(ActionEvent event) {

    }

    @FXML
    void handleFiltroChanged(ActionEvent event) {

    }

    @FXML
    void handleGestionarJornadas(ActionEvent event) {

    }

    @FXML
    void handleLimpiarFiltros(ActionEvent event) {

    }

    @FXML
    void handleNuevoPartido(ActionEvent event) {

    }

    @FXML
    void initialize() {


    }

}
