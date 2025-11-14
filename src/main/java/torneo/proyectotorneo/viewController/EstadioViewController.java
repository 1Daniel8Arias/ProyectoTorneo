package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.EstadioController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Departamento;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ViewController para la gestión de Estadios
 */
public class EstadioViewController {

    @FXML private TableView<Estadio> tablaEstadios;
    @FXML private TableColumn<Estadio, Integer> colId;
    @FXML private TableColumn<Estadio, String> colNombre;
    @FXML private TableColumn<Estadio, Integer> colCapacidad;
    @FXML private TableColumn<Estadio, String> colDepartamento;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbDepartamento;
    @FXML private ComboBox<String> cbFiltroDepartamento;
    @FXML private Spinner<Integer> spinnerCapacidadMinima;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    private final EstadioController estadioController;
    private final ObservableList<Estadio> listaEstadios;
    private Estadio estadioSeleccionado;

    public EstadioViewController() {
        this.estadioController = new EstadioController();
        this.listaEstadios = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarSpinner();
        cargarDepartamentos();
        cargarEstadios();
        configurarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEstadio"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));

        colDepartamento.setCellValueFactory(cellData -> {
            Estadio estadio = cellData.getValue();
            if (estadio.getDepartamento() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        estadio.getDepartamento().getNombre()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("Sin departamento");
        });

        tablaEstadios.setItems(listaEstadios);

        tablaEstadios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        estadioSeleccionado = newValue;
                        cargarDatosEnFormulario(newValue);
                    }
                }
        );
    }

    private void configurarSpinner() {
        if (spinnerCapacidadMinima != null) {
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 0, 1000);
            spinnerCapacidadMinima.setValueFactory(valueFactory);

            spinnerCapacidadMinima.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null && newValue > 0) {
                    filtrarPorCapacidadMinima(newValue);
                }
            });
        }
    }

    private void configurarEventos() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarEstadios(newValue);
            });
        }

        if (cbFiltroDepartamento != null) {
            cbFiltroDepartamento.setOnAction(e -> filtrarPorDepartamento());
        }
    }

    private void cargarDepartamentos() {
        try {
            ArrayList<Departamento> departamentos = estadioController.listarDepartamentos();
            ObservableList<String> nombresDepartamentos = FXCollections.observableArrayList();
            nombresDepartamentos.add("Seleccione departamento");

            for (Departamento depto : departamentos) {
                nombresDepartamentos.add(depto.getNombre());
            }

            if (cbDepartamento != null) {
                cbDepartamento.setItems(nombresDepartamentos);
                cbDepartamento.setValue("Seleccione departamento");
            }

            if (cbFiltroDepartamento != null) {
                ObservableList<String> filtros = FXCollections.observableArrayList();
                filtros.add("Todos");
                filtros.addAll(nombresDepartamentos.subList(1, nombresDepartamentos.size()));
                cbFiltroDepartamento.setItems(filtros);
                cbFiltroDepartamento.setValue("Todos");
            }

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudieron cargar los departamentos: " + e.getMessage());
        }
    }

    private void cargarEstadios() {
        try {
            ArrayList<Estadio> estadios = estadioController.listarTodos();
            listaEstadios.clear();
            listaEstadios.addAll(estadios);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar estadios", e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(Estadio estadio) {
        txtNombre.setText(estadio.getNombre());
        txtCapacidad.setText(String.valueOf(estadio.getCapacidad()));

        if (cbDepartamento != null && estadio.getDepartamento() != null) {
            cbDepartamento.setValue(estadio.getDepartamento().getNombre());
        }

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
            Estadio nuevoEstadio = new Estadio();
            nuevoEstadio.setNombre(txtNombre.getText().trim());
            nuevoEstadio.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));

            // Obtener el departamento seleccionado
            String nombreDepartamento = cbDepartamento.getValue();
            if (nombreDepartamento != null && !nombreDepartamento.equals("Seleccione departamento")) {
                ArrayList<Departamento> departamentos = estadioController.listarDepartamentos();
                for (Departamento depto : departamentos) {
                    if (depto.getNombre().equals(nombreDepartamento)) {
                        nuevoEstadio.setDepartamento(depto);
                        break;
                    }
                }
            }

            estadioController.guardarEstadio(nuevoEstadio);

            AlertHelper.mostrarInformacion("Éxito", "Estadio registrado correctamente");
            cargarEstadios();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al guardar", e.getMessage());
        } catch (NumberFormatException e) {
            AlertHelper.mostrarError("Error", "La capacidad debe ser un número válido");
        }
    }

    @FXML
    private void handleActualizar() {
        if (estadioSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un estadio de la tabla");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            estadioSeleccionado.setNombre(txtNombre.getText().trim());
            estadioSeleccionado.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));

            // Actualizar departamento
            String nombreDepartamento = cbDepartamento.getValue();
            if (nombreDepartamento != null && !nombreDepartamento.equals("Seleccione departamento")) {
                ArrayList<Departamento> departamentos = estadioController.listarDepartamentos();
                for (Departamento depto : departamentos) {
                    if (depto.getNombre().equals(nombreDepartamento)) {
                        estadioSeleccionado.setDepartamento(depto);  // ✅ USAR 'depto' en lugar de 'departamentoSeleccionado'
                        break;
                    }
                }
            }

            estadioController.actualizarEstadio(estadioSeleccionado);

            AlertHelper.mostrarInformacion("Éxito", "Estadio actualizado correctamente");
            cargarEstadios();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al actualizar", e.getMessage());
        } catch (NumberFormatException e) {
            AlertHelper.mostrarError("Error", "La capacidad debe ser un número válido");
        }
    }

    @FXML
    private void handleEliminar() {
        if (estadioSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un estadio de la tabla");
            return;
        }

        Optional<ButtonType> resultado = AlertHelper.mostrarConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar el estadio: " + estadioSeleccionado.getNombre() + "?"
        );

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                estadioController.eliminarEstadio(estadioSeleccionado.getIdEstadio());

                AlertHelper.mostrarInformacion("Éxito", "Estadio eliminado correctamente");
                cargarEstadios();
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

    private void filtrarEstadios(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarEstadios();
            return;
        }

        try {
            ArrayList<Estadio> todosLosEstadios = estadioController.listarTodos();
            ArrayList<Estadio> estadiosFiltrados = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (Estadio estadio : todosLosEstadios) {
                if (estadio.getNombre().toLowerCase().contains(terminoLower)) {
                    estadiosFiltrados.add(estadio);
                }
            }

            listaEstadios.clear();
            listaEstadios.addAll(estadiosFiltrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarPorDepartamento() {
        if (cbFiltroDepartamento == null) return;

        String departamentoSeleccionado = cbFiltroDepartamento.getValue();

        if (departamentoSeleccionado == null || departamentoSeleccionado.equals("Todos")) {
            cargarEstadios();
            return;
        }

        try {
            // Buscar ID del departamento
            ArrayList<Departamento> departamentos = estadioController.listarDepartamentos();
            Integer idDepartamento = null;

            for (Departamento depto : departamentos) {
                if (depto.getNombre().equals(departamentoSeleccionado)) {
                    idDepartamento = depto.getIdDepartamento();
                    break;
                }
            }

            if (idDepartamento != null) {
                ArrayList<Estadio> estadiosFiltrados =
                        estadioController.listarEstadiosPorDepartamento(idDepartamento);
                listaEstadios.clear();
                listaEstadios.addAll(estadiosFiltrados);
            }
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarPorCapacidadMinima(int capacidadMinima) {
        try {
            ArrayList<Estadio> estadiosFiltrados =
                    estadioController.listarEstadiosPorCapacidadMinima(capacidadMinima);
            listaEstadios.clear();
            listaEstadios.addAll(estadiosFiltrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "El nombre del estadio es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (txtCapacidad.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "La capacidad es obligatoria");
            txtCapacidad.requestFocus();
            return false;
        }

        try {
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            if (capacidad <= 0) {
                AlertHelper.mostrarAdvertencia("Validación", "La capacidad debe ser mayor a 0");
                txtCapacidad.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.mostrarAdvertencia("Validación", "La capacidad debe ser un número válido");
            txtCapacidad.requestFocus();
            return false;
        }

        if (cbDepartamento != null && cbDepartamento.getValue().equals("Seleccione departamento")) {
            AlertHelper.mostrarAdvertencia("Validación", "Debe seleccionar un departamento");
            cbDepartamento.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtCapacidad.clear();

        if (cbDepartamento != null) {
            cbDepartamento.setValue("Seleccione departamento");
        }

        estadioSeleccionado = null;
        tablaEstadios.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        txtNombre.requestFocus();
    }
}