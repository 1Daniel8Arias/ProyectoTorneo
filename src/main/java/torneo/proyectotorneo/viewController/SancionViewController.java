package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.controller.SancionController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.utils.AlertHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * ViewController para la gestión de Sanciones
 */
public class SancionViewController {

    @FXML private TableView<Sancion> tablaSanciones;
    @FXML private TableColumn<Sancion, Integer> colId;
    @FXML private TableColumn<Sancion, LocalDate> colFecha;
    @FXML private TableColumn<Sancion, String> colMotivo;
    @FXML private TableColumn<Sancion, Integer> colDuracion;
    @FXML private TableColumn<Sancion, String> colTipo;
    @FXML private TableColumn<Sancion, String> colJugador;

    @FXML private DatePicker dpFecha;
    @FXML private TextArea txtMotivo;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtTipo;
    @FXML private TextField txtBuscar;

    @FXML private ComboBox<String> cbJugador;
    @FXML private ComboBox<String> cbFiltroTipo;
    @FXML private CheckBox chkSoloActivas;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    @FXML private Label lblEstado;

    private final SancionController sancionController;
    private final JugadorController jugadorController;
    private final ObservableList<Sancion> listaSanciones;
    private Sancion sancionSeleccionada;

    public SancionViewController() {
        this.sancionController = new SancionController();
        this.jugadorController = new JugadorController();
        this.listaSanciones = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBox();
        cargarJugadores();
        cargarSanciones();
        configurarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idSancion"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colJugador.setCellValueFactory(cellData -> {
            Sancion sancion = cellData.getValue();
            if (sancion.getJugador() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        sancion.getJugador().getNombre() + " " + sancion.getJugador().getApellido()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("Sin jugador");
        });

        tablaSanciones.setItems(listaSanciones);

        tablaSanciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        sancionSeleccionada = newValue;
                        cargarDatosEnFormulario(newValue);
                    }
                }
        );
    }

    private void configurarComboBox() {
        if (cbFiltroTipo != null) {
            cbFiltroTipo.setItems(FXCollections.observableArrayList(
                    "Todos",
                    "Tarjeta Roja",
                    "Acumulación de Amarillas",
                    "Conducta Antideportiva",
                    "Dopaje",
                    "Otro"
            ));
            cbFiltroTipo.setValue("Todos");
        }
    }

    private void configurarEventos() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarSanciones(newValue);
            });
        }

        if (cbFiltroTipo != null) {
            cbFiltroTipo.setOnAction(e -> filtrarPorTipo());
        }

        if (chkSoloActivas != null) {
            chkSoloActivas.setOnAction(e -> {
                if (chkSoloActivas.isSelected()) {
                    mostrarSoloActivas();
                } else {
                    cargarSanciones();
                }
            });
        }

        if (dpFecha != null) {
            dpFecha.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null && txtDuracion.getText() != null && !txtDuracion.getText().isEmpty()) {
                    actualizarEstadoSancion();
                }
            });
        }

        if (txtDuracion != null) {
            txtDuracion.textProperty().addListener((obs, oldValue, newValue) -> {
                if (dpFecha.getValue() != null && newValue != null && !newValue.isEmpty()) {
                    actualizarEstadoSancion();
                }
            });
        }
    }

    private void cargarJugadores() {
        try {
            ArrayList<Jugador> jugadores = jugadorController.listarTodos();
            ObservableList<String> nombresJugadores = FXCollections.observableArrayList();
            nombresJugadores.add("Seleccione jugador");

            for (Jugador jugador : jugadores) {
                nombresJugadores.add(jugador.getNombre() + " " + jugador.getApellido() +
                        " (#" + jugador.getNumeroCamiseta() + ")");
            }

            if (cbJugador != null) {
                cbJugador.setItems(nombresJugadores);
                cbJugador.setValue("Seleccione jugador");
            }

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudieron cargar los jugadores: " + e.getMessage());
        }
    }

    private void cargarSanciones() {
        try {
            ArrayList<Sancion> sanciones = sancionController.listarTodos();
            listaSanciones.clear();
            listaSanciones.addAll(sanciones);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar sanciones", e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(Sancion sancion) {
        dpFecha.setValue(sancion.getFecha());
        txtMotivo.setText(sancion.getMotivo());
        txtDuracion.setText(String.valueOf(sancion.getDuracion()));
        txtTipo.setText(sancion.getTipo());

        if (cbJugador != null && sancion.getJugador() != null) {
            String nombreJugador = sancion.getJugador().getNombre() + " " +
                    sancion.getJugador().getApellido() +
                    " (#" + sancion.getJugador().getNumeroCamiseta() + ")";
            cbJugador.setValue(nombreJugador);
        }

        actualizarEstadoSancion();

        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        btnGuardar.setDisable(true);
    }

    private void actualizarEstadoSancion() {
        if (lblEstado == null || dpFecha.getValue() == null || txtDuracion.getText().isEmpty()) {
            return;
        }

        try {
            LocalDate fechaSancion = dpFecha.getValue();
            int duracion = Integer.parseInt(txtDuracion.getText());
            LocalDate fechaFinSancion = fechaSancion.plusDays(duracion);
            LocalDate hoy = LocalDate.now();

            if (hoy.isBefore(fechaFinSancion) || hoy.isEqual(fechaFinSancion)) {
                lblEstado.setText("Estado: ACTIVA (Vence: " + fechaFinSancion + ")");
                lblEstado.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                lblEstado.setText("Estado: CUMPLIDA (Venció: " + fechaFinSancion + ")");
                lblEstado.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }
        } catch (NumberFormatException e) {
            lblEstado.setText("Estado: -");
            lblEstado.setStyle("");
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Sancion nuevaSancion = new Sancion();
            nuevaSancion.setFecha(dpFecha.getValue());
            nuevaSancion.setMotivo(txtMotivo.getText().trim());
            nuevaSancion.setDuracion(Integer.parseInt(txtDuracion.getText().trim()));
            nuevaSancion.setTipo(txtTipo.getText().trim());

            // Obtener ID del jugador seleccionado
            String jugadorSeleccionado = cbJugador.getValue();
            if (jugadorSeleccionado != null && !jugadorSeleccionado.equals("Seleccione jugador")) {
                String numeroCamiseta = jugadorSeleccionado.substring(
                        jugadorSeleccionado.indexOf("#") + 1,
                        jugadorSeleccionado.indexOf(")")
                );

                ArrayList<Jugador> jugadores = jugadorController.listarTodos();
                for (Jugador j : jugadores) {
                    if (j.getNumeroCamiseta().equals(numeroCamiseta)) {
                        nuevaSancion.setIdJugador(j.getIdJugador());
                        break;
                    }
                }
            }

            sancionController.guardarSancion(nuevaSancion);

            AlertHelper.mostrarInformacion("Éxito", "Sanción registrada correctamente");
            cargarSanciones();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (sancionSeleccionada == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar una sanción de la tabla");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            sancionSeleccionada.setFecha(dpFecha.getValue());
            sancionSeleccionada.setMotivo(txtMotivo.getText().trim());
            sancionSeleccionada.setDuracion(Integer.parseInt(txtDuracion.getText().trim()));
            sancionSeleccionada.setTipo(txtTipo.getText().trim());

            // Actualizar jugador
            String jugadorSeleccionado = cbJugador.getValue();
            if (jugadorSeleccionado != null && !jugadorSeleccionado.equals("Seleccione jugador")) {
                String numeroCamiseta = jugadorSeleccionado.substring(
                        jugadorSeleccionado.indexOf("#") + 1,
                        jugadorSeleccionado.indexOf(")")
                );

                ArrayList<Jugador> jugadores = jugadorController.listarTodos();
                for (Jugador j : jugadores) {
                    if (j.getNumeroCamiseta().equals(numeroCamiseta)) {
                        sancionSeleccionada.setIdJugador(j.getIdJugador());
                        break;
                    }
                }
            }

            sancionController.actualizarSancion(sancionSeleccionada);

            AlertHelper.mostrarInformacion("Éxito", "Sanción actualizada correctamente");
            cargarSanciones();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al actualizar", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (sancionSeleccionada == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar una sanción de la tabla");
            return;
        }

        Optional<ButtonType> resultado = AlertHelper.mostrarConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar esta sanción?"
        );

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                sancionController.eliminarSancion(sancionSeleccionada.getIdSancion());

                AlertHelper.mostrarInformacion("Éxito", "Sanción eliminada correctamente");
                cargarSanciones();
                limpiarFormulario();
            } catch (RepositoryException e) {
                AlertHelper.mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        limpiarFormulario();
    }

    private void filtrarSanciones(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarSanciones();
            return;
        }

        try {
            ArrayList<Sancion> todasLasSanciones = sancionController.listarTodos();
            ArrayList<Sancion> sancionesFiltradas = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (Sancion sancion : todasLasSanciones) {
                if (sancion.getMotivo().toLowerCase().contains(terminoLower) ||
                        sancion.getTipo().toLowerCase().contains(terminoLower) ||
                        (sancion.getJugador() != null &&
                                (sancion.getJugador().getNombre().toLowerCase().contains(terminoLower) ||
                                        sancion.getJugador().getApellido().toLowerCase().contains(terminoLower)))) {
                    sancionesFiltradas.add(sancion);
                }
            }

            listaSanciones.clear();
            listaSanciones.addAll(sancionesFiltradas);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarPorTipo() {
        if (cbFiltroTipo == null) return;

        String tipoSeleccionado = cbFiltroTipo.getValue();

        if (tipoSeleccionado == null || tipoSeleccionado.equals("Todos")) {
            cargarSanciones();
            return;
        }

        try {
            ArrayList<Sancion> sancionesFiltradas = sancionController.listarSancionesPorTipo(tipoSeleccionado);

            listaSanciones.clear();
            listaSanciones.addAll(sancionesFiltradas);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void mostrarSoloActivas() {
        try {
            ArrayList<Sancion> sancionesActivas = sancionController.listarSancionesActivas();
            listaSanciones.clear();
            listaSanciones.addAll(sancionesActivas);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (dpFecha.getValue() == null) {
            AlertHelper.mostrarAdvertencia("Validación", "La fecha es obligatoria");
            dpFecha.requestFocus();
            return false;
        }

        if (txtMotivo.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "El motivo es obligatorio");
            txtMotivo.requestFocus();
            return false;
        }

        if (txtDuracion.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "La duración es obligatoria");
            txtDuracion.requestFocus();
            return false;
        }

        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion <= 0) {
                AlertHelper.mostrarAdvertencia("Validación", "La duración debe ser mayor a 0");
                txtDuracion.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.mostrarAdvertencia("Validación", "La duración debe ser un número válido");
            txtDuracion.requestFocus();
            return false;
        }

        if (txtTipo.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "El tipo de sanción es obligatorio");
            txtTipo.requestFocus();
            return false;
        }

        if (cbJugador != null && cbJugador.getValue().equals("Seleccione jugador")) {
            AlertHelper.mostrarAdvertencia("Validación", "Debe seleccionar un jugador");
            cbJugador.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        dpFecha.setValue(null);
        txtMotivo.clear();
        txtDuracion.clear();
        txtTipo.clear();

        if (cbJugador != null) {
            cbJugador.setValue("Seleccione jugador");
        }

        if (lblEstado != null) {
            lblEstado.setText("Estado: -");
            lblEstado.setStyle("");
        }

        sancionSeleccionada = null;
        tablaSanciones.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }
}