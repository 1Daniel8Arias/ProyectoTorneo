package torneo.proyectotorneo.viewController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.SancionController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.model.enums.TipoSancion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SancionViewController {

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoSancion;

    @FXML
    private ComboBox<String> cmbEquipo;

    @FXML
    private ComboBox<TipoSancion> cmbTipo;

    @FXML
    private TableColumn<Sancion, Void> colAcciones;

    @FXML
    private TableColumn<Sancion, Integer> colDuracion;

    @FXML
    private TableColumn<Sancion, LocalDate> colFecha;

    @FXML
    private TableColumn<Sancion, String> colMotivo;

    @FXML
    private TableColumn<Sancion, String> colNombre;

    @FXML
    private TableColumn<Sancion, Integer> colNumero;

    @FXML
    private TableColumn<Sancion, String> colTipo;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<Sancion> tableSancion;

    @FXML
    private TextField txtBuscar;

    private SancionController sancionController;
    private ObservableList<Sancion> sancionesObservable;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        sancionController = new SancionController();
        sancionesObservable = FXCollections.observableArrayList();

        configurarTabla();
        configurarComboBoxes();
        cargarSanciones();
        configurarFiltros();
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        // Columna Número (contador de fila - muestra el conteo de jugadores con sanción)
        colNumero.setCellValueFactory(cellData -> {
            int index = tableSancion.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });

        // Columna Nombre (nombre completo del jugador)
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreJugador"));

        // Columna Tipo (tipo de sanción: Tarjeta_Roja, Suspension, Multa)
        colTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipo();
            return new SimpleStringProperty(tipo != null ? tipo : "-");
        });

        // Columna Fecha (fecha en que se aplicó la sanción)
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(column -> new TableCell<Sancion, LocalDate>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(fecha));
                }
            }
        });

        // Columna Motivo (motivo por el cual fue sancionado)
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        // Columna Duración (cuántas fechas tiene de sanción)
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colDuracion.setCellFactory(column -> new TableCell<Sancion, Integer>() {
            @Override
            protected void updateItem(Integer duracion, boolean empty) {
                super.updateItem(duracion, empty);
                if (empty || duracion == null) {
                    setText(null);
                } else {
                    setText(duracion + (duracion == 1 ? " fecha" : " fechas"));
                }
            }
        });

        // Columna Acciones (botones de editar/eliminar)
        configurarColumnaAcciones();

        tableSancion.setItems(sancionesObservable);
    }

    /**
     * Configura la columna de acciones con botones
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️");
            private final Button btnEliminar = new Button("🗑️");

            {
                btnEditar.getStyleClass().add("btn-edit");
                btnEliminar.getStyleClass().add("btn-delete");

                btnEditar.setOnAction(event -> {
                    Sancion sancion = getTableView().getItems().get(getIndex());
                    handleEditarSancion(sancion);
                });

                btnEliminar.setOnAction(event -> {
                    Sancion sancion = getTableView().getItems().get(getIndex());
                    handleEliminarSancion(sancion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER);
                    hbox.getChildren().addAll(btnEditar, btnEliminar);
                    setGraphic(hbox);
                }
            }
        });
    }

    /**
     * Configura los ComboBoxes
     */
    private void configurarComboBoxes() {
        // ComboBox de tipos de sanción (Tarjeta_Roja, Suspension, Multa)
        cmbTipo.setItems(FXCollections.observableArrayList(TipoSancion.values()));
        cmbTipo.setPromptText("Todos los tipos");

        // ComboBox de equipos
        cmbEquipo.setPromptText("Todos los equipos");

        // Aquí puedes cargar los equipos si lo necesitas:
        // ArrayList<String> equipos = obtenerEquipos();
        // cmbEquipo.setItems(FXCollections.observableArrayList(equipos));
    }

    /**
     * Carga todas las sanciones desde la base de datos
     */
    private void cargarSanciones() {
        try {
            ArrayList<Sancion> sanciones = sancionController.listarTodos();
            sancionesObservable.setAll(sanciones);
            actualizarContador();
        } catch (RepositoryException e) {
            mostrarError("Error al cargar sanciones", e.getMessage());
        }
    }

    /**
     * Configura los filtros de búsqueda
     */
    private void configurarFiltros() {
        // Filtro por texto en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarSanciones();
        });

        // Filtro por tipo de sanción
        cmbTipo.setOnAction(event -> filtrarSanciones());

        // Filtro por equipo (si lo implementas)
        cmbEquipo.setOnAction(event -> filtrarSanciones());
    }

    /**
     * Filtra las sanciones según los criterios seleccionados
     */
    private void filtrarSanciones() {
        try {
            ArrayList<Sancion> todasLasSanciones = sancionController.listarTodos();
            String textoBusqueda = txtBuscar.getText().toLowerCase();
            TipoSancion tipoSeleccionado = cmbTipo.getValue();
            String equipoSeleccionado = cmbEquipo.getValue();

            ArrayList<Sancion> sancionesFiltradas = new ArrayList<>();

            for (Sancion sancion : todasLasSanciones) {
                boolean cumpleFiltroTexto = true;
                boolean cumpleFiltroTipo = true;
                boolean cumpleFiltroEquipo = true;

                // Filtro por texto (busca en nombre del jugador y motivo)
                if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
                    cumpleFiltroTexto = false;
                    if (sancion.getJugador() != null) {
                        String nombreCompleto = (sancion.getJugador().getNombre() + " " +
                                sancion.getJugador().getApellido()).toLowerCase();
                        if (nombreCompleto.contains(textoBusqueda)) {
                            cumpleFiltroTexto = true;
                        }
                    }
                    if (sancion.getMotivo() != null &&
                            sancion.getMotivo().toLowerCase().contains(textoBusqueda)) {
                        cumpleFiltroTexto = true;
                    }
                }

                // Filtro por tipo de sanción
                if (tipoSeleccionado != null) {
                    cumpleFiltroTipo = sancion.getTipo() != null &&
                            sancion.getTipo().equals(tipoSeleccionado.name());
                }

                // Filtro por equipo (si lo implementas)
                if (equipoSeleccionado != null && !equipoSeleccionado.equals("Todos los equipos")) {
                    if (sancion.getJugador() != null &&
                            sancion.getJugador().getEquipo() != null) {
                        cumpleFiltroEquipo = sancion.getJugador().getEquipo().getNombre()
                                .equals(equipoSeleccionado);
                    } else {
                        cumpleFiltroEquipo = false;
                    }
                }

                if (cumpleFiltroTexto && cumpleFiltroTipo && cumpleFiltroEquipo) {
                    sancionesFiltradas.add(sancion);
                }
            }

            sancionesObservable.setAll(sancionesFiltradas);
            actualizarContador();

        } catch (RepositoryException e) {
            mostrarError("Error al filtrar sanciones", e.getMessage());
        }
    }

    /**
     * Actualiza el contador de sanciones
     */
    private void actualizarContador() {
        int total = sancionesObservable.size();
        lblContador.setText("Total de sanciones: " + total);
    }

    @FXML
    void handleBuscar(ActionEvent event) {
        filtrarSanciones();
    }

    @FXML
    void handleNuevoSancion(ActionEvent event) {
        // TODO: Implementar diálogo para crear nueva sanción
        // Aquí puedes abrir una ventana modal para crear una nueva sanción
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad de nueva sanción por implementar.\n\n" +
                "Nota: Las sanciones por tarjeta roja se generan automáticamente " +
                "cuando un jugador recibe una tarjeta roja en un partido.");
        alert.showAndWait();

        // Ejemplo de cómo podrías implementarlo:
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/NuevaSancion.fxml"));
            Parent root = loader.load();

            NuevaSancionViewController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            // Recargar las sanciones después de crear una nueva
            cargarSanciones();
        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el formulario de nueva sanción");
        }
        */
    }

    /**
     * Maneja la edición de una sanción
     */
    private void handleEditarSancion(Sancion sancion) {
        // TODO: Implementar diálogo de edición
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Editar Sanción");
        alert.setHeaderText(null);
        alert.setContentText("Editar sanción de: " +
                sancion.getJugador().getNombre() + " " +
                sancion.getJugador().getApellido() + "\n\n" +
                "Tipo: " + sancion.getTipo() + "\n" +
                "Duración: " + sancion.getDuracion() + " fechas");
        alert.showAndWait();
    }

    /**
     * Maneja la eliminación de una sanción
     */
    private void handleEliminarSancion(Sancion sancion) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar la sanción de " +
                sancion.getJugador().getNombre() + " " +
                sancion.getJugador().getApellido() + "?\n\n" +
                "Tipo: " + sancion.getTipo() + "\n" +
                "Motivo: " + sancion.getMotivo());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sancionController.eliminarSancion(sancion.getIdSancion());
                    sancionesObservable.remove(sancion);
                    actualizarContador();

                    mostrarExito("Sanción eliminada correctamente");
                } catch (RepositoryException e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}