package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

/**
 * Controlador de la vista de gestión de partidos.
 * Diseño compacto y moderno.
 */
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
    @FXML private Label limpiarFiltrosLink;
    @FXML private ScrollPane partidosScrollPane;
    @FXML private VBox partidosContainer;

    /**
     * Inicializa el controlador después de cargar el FXML
     */
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
     * Carga los datos de los ComboBox de filtros
     */
    private void cargarFiltros() {
        try {
            // Cargar equipos
            List<String> equipos = new ArrayList<>();
            equipos.add("Todos los equipos");
            equipos.addAll(partidoController.obtenerNombresEquipos());
            equipoComboBox.setItems(FXCollections.observableArrayList(equipos));
            equipoComboBox.getSelectionModel().selectFirst();

            // Cargar tipos de equipo
            tipoEquipoComboBox.setItems(FXCollections.observableArrayList(
                    "Ambos", "Local", "Visitante"
            ));
            tipoEquipoComboBox.getSelectionModel().selectFirst();

            // Cargar jornadas
            List<String> jornadas = new ArrayList<>();
            jornadas.add("Todas las jornadas");
            jornadas.addAll(partidoController.obtenerJornadas());
            jornadaComboBox.setItems(FXCollections.observableArrayList(jornadas));
            jornadaComboBox.getSelectionModel().selectFirst();

            // Cargar estadios
            List<String> estadios = new ArrayList<>();
            estadios.add("Todos los estadios");
            estadios.addAll(partidoController.obtenerEstadios());
            estadioComboBox.setItems(FXCollections.observableArrayList(estadios));
            estadioComboBox.getSelectionModel().selectFirst();

        } catch (Exception e) {
            MensajeUtil.mostrarError("Error al cargar filtros: " + e.getMessage());
        }
    }

    /**
     * Carga todos los partidos desde la base de datos
     */
    private void cargarPartidos() {
        try {
            List<Partido> partidos = partidoController.listarPartidosConEquiposYEstadio();
            partidosFiltrados = new ArrayList<>(partidos);
            mostrarPartidos(partidosFiltrados);
            actualizarContadoresEstado(partidos);
        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al cargar partidos: " + e.getMessage());
            partidosFiltrados = new ArrayList<>();
            mostrarPartidos(partidosFiltrados);
        }
    }

    /**
     * Actualiza los contadores en los botones de estado
     */
    private void actualizarContadoresEstado(List<Partido> partidos) {
        int todos = partidos.size();
        int programados = (int) partidos.stream()
                .filter(p -> "programados".equals(partidoController.obtenerEstadoPartido(p).toLowerCase()))
                .count();
        int enCurso = (int) partidos.stream()
                .filter(p -> "en curso".equals(partidoController.obtenerEstadoPartido(p).toLowerCase()))
                .count();
        int finalizados = (int) partidos.stream()
                .filter(p -> "finalizado".equals(partidoController.obtenerEstadoPartido(p).toLowerCase()))
                .count();

        todosToggle.setText("Todos (" + todos + ")");
        programadosToggle.setText("Programados (" + programados + ")");
        enCursoToggle.setText("En Curso (" + enCurso + ")");
        finalizadosToggle.setText("Finalizados (" + finalizados + ")");
    }

    /**
     * Muestra los partidos en el contenedor
     */
    private void mostrarPartidos(List<Partido> partidos) {
        partidosContainer.getChildren().clear();

        if (partidos.isEmpty()) {
            // Mensaje cuando no hay partidos
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(javafx.geometry.Pos.CENTER);
            emptyState.setStyle("-fx-padding: 50px;");

            Label emptyIcon = new Label("📅");
            emptyIcon.setStyle("-fx-font-size: 48px;");

            Label emptyMessage = new Label("No hay partidos para mostrar");
            emptyMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280; -fx-font-weight: bold;");

            Label emptySubMessage = new Label("Ajusta los filtros o crea un nuevo partido");
            emptySubMessage.setStyle("-fx-font-size: 13px; -fx-text-fill: #9ca3af;");

            emptyState.getChildren().addAll(emptyIcon, emptyMessage, emptySubMessage);
            partidosContainer.getChildren().add(emptyState);
        } else {
            for (Partido partido : partidos) {
                try {
                    VBox card = cargarCardPartido(partido);
                    partidosContainer.getChildren().add(card);
                } catch (IOException e) {
                    System.err.println("Error al cargar card de partido: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Carga una tarjeta de partido desde cardPartido.fxml
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
     * Maneja cambios en los filtros
     */
    @FXML
    void handleFiltroChanged(ActionEvent event) {
        aplicarFiltros();
    }

    /**
     * Maneja cambios en el estado seleccionado
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
                String numeroStr = jornadaSeleccionada.replace("Jornada ", "");
                idJornada = Integer.parseInt(numeroStr);
            }

            // Filtro por estadio
            String estadioSeleccionado = estadioComboBox.getValue();
            Integer idEstadio = null;

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
     * Limpia todos los filtros (para ActionEvent y MouseEvent)
     */
    @FXML
    void handleLimpiarFiltros(ActionEvent event) {
        limpiarFiltros();
    }



    /**
     * Método interno para limpiar filtros
     */
    private void limpiarFiltros() {
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
     * Elimina un partido después de confirmación
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
     * Abre ventana para editar un partido
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