package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.ArbitroController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ViewController para la gestión de Árbitros
 * Maneja la interfaz de usuario y delega la lógica al ArbitroController
 */
public class ArbitroViewController {

    @FXML private TableView<Arbitro> tablaArbitros;
    @FXML private TableColumn<Arbitro, Integer> colId;
    @FXML private TableColumn<Arbitro, String> colNombre;
    @FXML private TableColumn<Arbitro, String> colApellido;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtBuscar;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;

    @FXML private ComboBox<String> cbTipoFiltro;

    private final ArbitroController arbitroController;
    private final ObservableList<Arbitro> listaArbitros;
    private Arbitro arbitroSeleccionado;

    public ArbitroViewController() {
        this.arbitroController = new ArbitroController();
        this.listaArbitros = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBox();
        cargarArbitros();
        configurarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idArbitro"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        tablaArbitros.setItems(listaArbitros);

        // Evento de selección
        tablaArbitros.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        arbitroSeleccionado = newValue;
                        cargarDatosEnFormulario(newValue);
                    }
                }
        );
    }

    private void configurarComboBox() {
        if (cbTipoFiltro != null) {
            cbTipoFiltro.setItems(FXCollections.observableArrayList(
                    "Todos",
                    "Principal",
                    "Asistente 1",
                    "Asistente 2",
                    "Cuarto Árbitro"
            ));
            cbTipoFiltro.setValue("Todos");
        }
    }

    private void configurarEventos() {
        // Búsqueda en tiempo real
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarArbitros(newValue);
            });
        }
    }

    private void cargarArbitros() {
        try {
            ArrayList<Arbitro> arbitros = arbitroController.listarTodos();
            listaArbitros.clear();
            listaArbitros.addAll(arbitros);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar árbitros", e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(Arbitro arbitro) {
        txtNombre.setText(arbitro.getNombre());
        txtApellido.setText(arbitro.getApellido());

        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        btnGuardar.setDisable(true);
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Arbitro nuevoArbitro = new Arbitro();
            nuevoArbitro.setNombre(txtNombre.getText().trim());
            nuevoArbitro.setApellido(txtApellido.getText().trim());

            arbitroController.guardarArbitro(nuevoArbitro);

            AlertHelper.mostrarInformacion("Éxito", "Árbitro registrado correctamente");
            cargarArbitros();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (arbitroSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un árbitro de la tabla");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            arbitroSeleccionado.setNombre(txtNombre.getText().trim());
            arbitroSeleccionado.setApellido(txtApellido.getText().trim());

            arbitroController.actualizarArbitro(arbitroSeleccionado);

            AlertHelper.mostrarInformacion("Éxito", "Árbitro actualizado correctamente");
            cargarArbitros();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al actualizar", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (arbitroSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un árbitro de la tabla");
            return;
        }

        Optional<ButtonType> resultado = AlertHelper.mostrarConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar al árbitro: " + arbitroSeleccionado.getNombre() + " " +
                        arbitroSeleccionado.getApellido() + "?"
        );

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                arbitroController.eliminarArbitro(arbitroSeleccionado.getIdArbitro());

                AlertHelper.mostrarInformacion("Éxito", "Árbitro eliminado correctamente");
                cargarArbitros();
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

    @FXML
    private void handleBuscar() {
        String termino = txtBuscar.getText().trim();
        filtrarArbitros(termino);
    }

    @FXML
    private void handleFiltrarPorTipo() {
        if (cbTipoFiltro == null) return;

        String tipoSeleccionado = cbTipoFiltro.getValue();

        try {
            if (tipoSeleccionado.equals("Todos")) {
                cargarArbitros();
            } else {
                ArrayList<Arbitro> arbitrosFiltrados = arbitroController.listarArbitrosPorTipo(tipoSeleccionado);
                listaArbitros.clear();
                listaArbitros.addAll(arbitrosFiltrados);
            }
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarArbitros(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarArbitros();
            return;
        }

        try {
            ArrayList<Arbitro> todosLosArbitros = arbitroController.listarTodos();
            ArrayList<Arbitro> arbitrosFiltrados = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (Arbitro arbitro : todosLosArbitros) {
                if (arbitro.getNombre().toLowerCase().contains(terminoLower) ||
                        arbitro.getApellido().toLowerCase().contains(terminoLower)) {
                    arbitrosFiltrados.add(arbitro);
                }
            }

            listaArbitros.clear();
            listaArbitros.addAll(arbitrosFiltrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (txtApellido.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "El apellido es obligatorio");
            txtApellido.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();

        arbitroSeleccionado = null;
        tablaArbitros.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        txtNombre.requestFocus();
    }
}