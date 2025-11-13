package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.PartidoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Partido;
import torneo.proyectotorneo.utils.MensajeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartidoViewController {

    private PartidoController partidoController;
    private List<Partido> partidosFiltrados;
    private ToggleGroup estadoToggleGroup;

    @FXML private FlowPane activeFiltrosPane;
    @FXML private Button btnGestionarJornadas;
    @FXML private Button btnNuevoPartido;
    @FXML private ComboBox<String> equipoComboBox;
    @FXML private ComboBox<String> estadioComboBox;
    @FXML private ComboBox<String> jornadaComboBox;
    @FXML private ComboBox<String> tipoEquipoComboBox;
    @FXML private ToggleButton enCursoToggle;
    @FXML private ToggleButton finalizadosToggle;
    @FXML private ToggleButton programadosToggle;
    @FXML private ToggleButton todosToggle;
    @FXML private Label resultadosLabel;
    @FXML private ScrollPane partidosScrollPane;
    @FXML private VBox partidosContainer;

    @FXML
    void initialize() {
        partidoController = new PartidoController();
        configurarToggleGroup();
        cargarFiltros();
        cargarPartidos();
    }

    /**
     * Configura el grupo de toggles para estado
     */
    private void configurarToggleGroup() {
        estadoToggleGroup = new ToggleGroup();
        todosToggle.setToggleGroup(estadoToggleGroup);
        programadosToggle.setToggleGroup(estadoToggleGroup);
        enCursoToggle.setToggleGroup(estadoToggleGroup);
        finalizadosToggle.setToggleGroup(estadoToggleGroup);
        todosToggle.setSelected(true);
    }

    /**
     * Carga los datos de los filtros
     */
    private void cargarFiltros() {
        // Cargar equipos
        List<String> equipos = partidoController.obtenerNombresEquipos();
        equipos.add(0, "Todos los equipos");
        equipoComboBox.setItems(FXCollections.observableArrayList(equipos));
        equipoComboBox.getSelectionModel().selectFirst();

        // Cargar tipo de equipo
        tipoEquipoComboBox.setItems(FXCollections.observableArrayList(
                "Ambos", "Local", "Visitante"
        ));
        tipoEquipoComboBox.getSelectionModel().selectFirst();

        // Cargar jornadas
        List<String> jornadas = partidoController.obtenerJornadas();
        jornadas.add(0, "Todas las jornadas");
        jornadaComboBox.setItems(FXCollections.observableArrayList(jornadas));
        jornadaComboBox.getSelectionModel().selectFirst();

        // Cargar estadios
        List<String> estadios = partidoController.obtenerEstadios();
        estadios.add(0, "Todos los estadios");
        estadioComboBox.setItems(FXCollections.observableArrayList(estadios));
        estadioComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Carga todos los partidos
     */
    private void cargarPartidos() {
        try {
            ArrayList<Partido> partidos = partidoController.listarPartidosConEquiposYEstadio();
            partidosFiltrados = new ArrayList<>(partidos);
            mostrarPartidos(partidosFiltrados);
        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al cargar partidos: " + e.getMessage());
        }
    }

    /**
     * Muestra los partidos en el contenedor
     */
    private void mostrarPartidos(List<Partido> partidos) {
        partidosContainer.getChildren().clear();

        if (partidos.isEmpty()) {
            Label sinPartidos = new Label("No hay partidos para mostrar");
            sinPartidos.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280; -fx-padding: 50px;");
            partidosContainer.getChildren().add(sinPartidos);
        } else {
            for (Partido partido : partidos) {
                try {
                    VBox card = cargarCardPartido(partido);
                    partidosContainer.getChildren().add(card);
                    VBox.setMargin(card, new Insets(0, 0, 15, 0));
                } catch (IOException e) {
                    System.err.println("Error al cargar card de partido: " + e.getMessage());
                }
            }
        }

        resultadosLabel.setText("Mostrando " + partidos.size() + " partido(s)");
    }

    /**
     * Carga una tarjeta de partido
     */
    private VBox cargarCardPartido(Partido partido) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/cardPartido.fxml"));
        VBox card = loader.load();

        CardPartidoViewController controller = loader.getController();
        controller.setPartido(partido);
        controller.setPartidoViewController(this);

        return card;
    }

    /**
     * Aplica los filtros seleccionados
     */
    @FXML
    void handleFiltroChanged(ActionEvent event) {
        aplicarFiltros();
    }

    /**
     * Cambia el estado del filtro
     */
    @FXML
    void handleEstadoChanged(ActionEvent event) {
        aplicarFiltros();
    }

    /**
     * Aplica todos los filtros activos
     */
    private void aplicarFiltros() {
        try {
            List<Partido> resultado = new ArrayList<>(partidoController.listarPartidosConEquiposYEstadio());

            // Filtro por equipo
            String equipoSeleccionado = equipoComboBox.getValue();
            Integer idEquipo = null;
            if (equipoSeleccionado != null && !equipoSeleccionado.equals("Todos los equipos")) {
                idEquipo = partidoController.obtenerIdEquipoPorNombre(equipoSeleccionado);
            }

            // Filtro por tipo de equipo
            String tipoEquipo = tipoEquipoComboBox.getValue();
            if (tipoEquipo == null) tipoEquipo = "Ambos";

            // Filtro por jornada
            String jornadaSeleccionada = jornadaComboBox.getValue();
            Integer idJornada = null;
            if (jornadaSeleccionada != null && !jornadaSeleccionada.equals("Todas las jornadas")) {
                idJornada = Integer.parseInt(jornadaSeleccionada.replace("Jornada ", ""));
            }

            // Filtro por estadio
            String estadioSeleccionado = estadioComboBox.getValue();
            Integer idEstadio = null;
            // TODO: implementar obtenerIdEstadioPorNombre si es necesario

            // Aplicar filtros
            resultado = partidoController.filtrarPartidos(
                    resultado, idEquipo, tipoEquipo, idJornada, idEstadio, null
            );

            // Filtro por estado
            String estadoSeleccionado = obtenerEstadoSeleccionado();
            resultado = partidoController.filtrarPorEstado(resultado, estadoSeleccionado);

            partidosFiltrados = resultado;
            mostrarPartidos(partidosFiltrados);

        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al aplicar filtros: " + e.getMessage());
        }
    }

    /**
     * Obtiene el estado seleccionado en los toggles
     */
    private String obtenerEstadoSeleccionado() {
        if (programadosToggle.isSelected()) return "programados";
        if (enCursoToggle.isSelected()) return "en curso";
        if (finalizadosToggle.isSelected()) return "finalizados";
        return "todos";
    }

    /**
     * Limpia todos los filtros
     */
    @FXML
    void handleLimpiarFiltros(ActionEvent event) {
        equipoComboBox.getSelectionModel().selectFirst();
        tipoEquipoComboBox.getSelectionModel().selectFirst();
        jornadaComboBox.getSelectionModel().selectFirst();
        estadioComboBox.getSelectionModel().selectFirst();
        todosToggle.setSelected(true);
        cargarPartidos();
    }

    /**
     * Abre ventana para crear nuevo partido
     */
    @FXML
    void handleNuevoPartido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/NuevoPartido.fxml"));
            Parent root = loader.load();

            NuevoPartidoViewController controller = loader.getController();
            controller.setPartidoViewController(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Partido");
            stage.setScene(scene);
            stage.showAndWait();

            // Recargar partidos después de crear uno nuevo
            cargarPartidos();
        } catch (IOException e) {
            MensajeUtil.mostrarError("Error al abrir formulario: " + e.getMessage());
        }
    }

    /**
     * Abre ventana para gestionar jornadas
     */
    @FXML
    void handleGestionarJornadas(ActionEvent event) {
        MensajeUtil.mostrarAdvertencia("Funcionalidad en desarrollo");
    }

    /**
     * Recarga la lista de partidos
     */
    public void recargarPartidos() {
        cargarPartidos();
    }

    /**
     * Elimina un partido
     */
    public void eliminarPartido(Partido partido) {
        if (MensajeUtil.mostrarConfirmacion(
                "¿Está seguro de eliminar el partido entre " +
                        partido.getEquipoLocal().getNombre() + " y " +
                        partido.getEquipoVisitante().getNombre() + "?")) {
            try {
                partidoController.eliminarPartido(partido.getIdPartido());
                MensajeUtil.mostrarExito("Partido eliminado correctamente");
                cargarPartidos();
            } catch (RepositoryException e) {
                MensajeUtil.mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    /**
     * Edita un partido
     */
    public void editarPartido(Partido partido) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/NuevoPartido.fxml"));
            Parent root = loader.load();

            NuevoPartidoViewController controller = loader.getController();
            controller.setPartidoViewController(this);
            controller.cargarPartido(partido);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Partido");
            stage.setScene(scene);
            stage.showAndWait();

            cargarPartidos();
        } catch (IOException e) {
            MensajeUtil.mostrarError("Error al abrir formulario: " + e.getMessage());
        }
    }
}