package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.CuerpoTecnicoController;
import torneo.proyectotorneo.controller.EquipoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.CuerpoTecnico;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ViewController para la gestión de Cuerpo Técnico
 */
public class CuerpoTecnicoViewController {

    @FXML private TableView<CuerpoTecnico> tablaCuerpoTecnico;
    @FXML private TableColumn<CuerpoTecnico, Integer> colId;
    @FXML private TableColumn<CuerpoTecnico, String> colNombre;
    @FXML private TableColumn<CuerpoTecnico, String> colApellido;
    @FXML private TableColumn<CuerpoTecnico, String> colEspecialidad;
    @FXML private TableColumn<CuerpoTecnico, String> colEquipo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtBuscar;

    @FXML private ComboBox<String> cbEquipo;
    @FXML private ComboBox<String> cbFiltroEquipo;
    @FXML private ComboBox<String> cbFiltroEspecialidad;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    private final CuerpoTecnicoController cuerpoTecnicoController;
    private final EquipoController equipoController;
    private final ObservableList<CuerpoTecnico> listaCuerpoTecnico;
    private CuerpoTecnico cuerpoTecnicoSeleccionado;

    public CuerpoTecnicoViewController() {
        this.cuerpoTecnicoController = new CuerpoTecnicoController();
        this.equipoController = new EquipoController();
        this.listaCuerpoTecnico = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBox();
        cargarEquipos();
        cargarCuerpoTecnico();
        configurarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCuerpoTecnico"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

        colEquipo.setCellValueFactory(cellData -> {
            CuerpoTecnico ct = cellData.getValue();
            if (ct.getEquipo() != null) {
                return new javafx.beans.property.SimpleStringProperty(ct.getEquipo().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("Sin equipo");
        });

        tablaCuerpoTecnico.setItems(listaCuerpoTecnico);

        tablaCuerpoTecnico.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cuerpoTecnicoSeleccionado = newValue;
                        cargarDatosEnFormulario(newValue);
                    }
                }
        );
    }

    private void configurarComboBox() {
        if (cbFiltroEspecialidad != null) {
            cbFiltroEspecialidad.setItems(FXCollections.observableArrayList(
                    "Todas",
                    "Preparador Físico",
                    "Asistente Técnico",
                    "Entrenador de Porteros",
                    "Médico Deportivo",
                    "Fisioterapeuta",
                    "Nutricionista",
                    "Psicólogo Deportivo",
                    "Analista de Video",
                    "Otro"
            ));
            cbFiltroEspecialidad.setValue("Todas");
        }
    }

    private void configurarEventos() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarCuerpoTecnico(newValue);
            });
        }

        if (cbFiltroEquipo != null) {
            cbFiltroEquipo.setOnAction(e -> filtrarPorEquipo());
        }

        if (cbFiltroEspecialidad != null) {
            cbFiltroEspecialidad.setOnAction(e -> filtrarPorEspecialidad());
        }
    }

    private void cargarEquipos() {
        try {
            ArrayList<Equipo> equipos = equipoController.listarTodos();
            ObservableList<String> nombresEquipos = FXCollections.observableArrayList();
            nombresEquipos.add("Seleccione equipo");

            for (Equipo equipo : equipos) {
                nombresEquipos.add(equipo.getNombre());
            }

            if (cbEquipo != null) {
                cbEquipo.setItems(nombresEquipos);
                cbEquipo.setValue("Seleccione equipo");
            }

            if (cbFiltroEquipo != null) {
                ObservableList<String> filtros = FXCollections.observableArrayList();
                filtros.add("Todos");
                filtros.addAll(nombresEquipos.subList(1, nombresEquipos.size()));
                cbFiltroEquipo.setItems(filtros);
                cbFiltroEquipo.setValue("Todos");
            }

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudieron cargar los equipos: " + e.getMessage());
        }
    }

    private void cargarCuerpoTecnico() {
        try {
            ArrayList<CuerpoTecnico> miembros = cuerpoTecnicoController.listarTodos();
            listaCuerpoTecnico.clear();
            listaCuerpoTecnico.addAll(miembros);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar cuerpo técnico", e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(CuerpoTecnico cuerpoTecnico) {
        txtNombre.setText(cuerpoTecnico.getNombre());
        txtApellido.setText(cuerpoTecnico.getApellido());
        txtEspecialidad.setText(cuerpoTecnico.getEspecialidad());

        if (cbEquipo != null && cuerpoTecnico.getEquipo() != null) {
            cbEquipo.setValue(cuerpoTecnico.getEquipo().getNombre());
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
            CuerpoTecnico nuevoCuerpoTecnico = new CuerpoTecnico();
            nuevoCuerpoTecnico.setNombre(txtNombre.getText().trim());
            nuevoCuerpoTecnico.setApellido(txtApellido.getText().trim());
            nuevoCuerpoTecnico.setEspecialidad(txtEspecialidad.getText().trim());

            String nombreEquipo = cbEquipo.getValue();
            if (nombreEquipo != null && !nombreEquipo.equals("Seleccione equipo")) {
                int idEquipo = equipoController.obtenerIdEquipoPorNombre(nombreEquipo);
                Equipo equipo = new Equipo();
                equipo.setId(idEquipo);
                nuevoCuerpoTecnico.setEquipo(equipo);

            }

            cuerpoTecnicoController.guardarCuerpoTecnico(nuevoCuerpoTecnico);

            AlertHelper.mostrarInformacion("Éxito", "Miembro del cuerpo técnico registrado correctamente");
            cargarCuerpoTecnico();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (cuerpoTecnicoSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un miembro del cuerpo técnico");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            cuerpoTecnicoSeleccionado.setNombre(txtNombre.getText().trim());
            cuerpoTecnicoSeleccionado.setApellido(txtApellido.getText().trim());
            cuerpoTecnicoSeleccionado.setEspecialidad(txtEspecialidad.getText().trim());

            String nombreEquipo = cbEquipo.getValue();
            if (nombreEquipo != null && !nombreEquipo.equals("Seleccione equipo")) {
                int idEquipo = equipoController.obtenerIdEquipoPorNombre(nombreEquipo);
                Equipo equipo = new Equipo();
                equipo.setId(idEquipo);
                cuerpoTecnicoSeleccionado.setEquipo(equipo);
            }

            cuerpoTecnicoController.actualizarCuerpoTecnico(cuerpoTecnicoSeleccionado);

            AlertHelper.mostrarInformacion("Éxito", "Miembro del cuerpo técnico actualizado correctamente");
            cargarCuerpoTecnico();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al actualizar", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (cuerpoTecnicoSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un miembro del cuerpo técnico");
            return;
        }

        Optional<ButtonType> resultado = AlertHelper.mostrarConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar a: " + cuerpoTecnicoSeleccionado.getNombre() + " " +
                        cuerpoTecnicoSeleccionado.getApellido() + "?"
        );

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                cuerpoTecnicoController.eliminarCuerpoTecnico(cuerpoTecnicoSeleccionado.getId());

                AlertHelper.mostrarInformacion("Éxito", "Miembro del cuerpo técnico eliminado correctamente");
                cargarCuerpoTecnico();
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

    private void filtrarCuerpoTecnico(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarCuerpoTecnico();
            return;
        }

        try {
            ArrayList<CuerpoTecnico> todos = cuerpoTecnicoController.listarTodos();
            ArrayList<CuerpoTecnico> filtrados = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (CuerpoTecnico ct : todos) {
                if (ct.getNombre().toLowerCase().contains(terminoLower) ||
                        ct.getApellido().toLowerCase().contains(terminoLower) ||
                        ct.getEspecialidad().toLowerCase().contains(terminoLower)) {
                    filtrados.add(ct);
                }
            }

            listaCuerpoTecnico.clear();
            listaCuerpoTecnico.addAll(filtrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarPorEquipo() {
        if (cbFiltroEquipo == null) return;

        String equipoSeleccionado = cbFiltroEquipo.getValue();

        if (equipoSeleccionado == null || equipoSeleccionado.equals("Todos")) {
            cargarCuerpoTecnico();
            return;
        }

        try {
            int idEquipo = equipoController.obtenerIdEquipoPorNombre(equipoSeleccionado);
            ArrayList<CuerpoTecnico> filtrados = cuerpoTecnicoController.listarCuerpoTecnicoPorEquipo(idEquipo);

            listaCuerpoTecnico.clear();
            listaCuerpoTecnico.addAll(filtrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarPorEspecialidad() {
        if (cbFiltroEspecialidad == null) return;

        String especialidadSeleccionada = cbFiltroEspecialidad.getValue();

        if (especialidadSeleccionada == null || especialidadSeleccionada.equals("Todas")) {
            cargarCuerpoTecnico();
            return;
        }

        try {
            ArrayList<CuerpoTecnico> filtrados =
                    cuerpoTecnicoController.listarCuerpoTecnicoPorEspecialidad(especialidadSeleccionada);

            listaCuerpoTecnico.clear();
            listaCuerpoTecnico.addAll(filtrados);
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

        if (txtEspecialidad.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "La especialidad es obligatoria");
            txtEspecialidad.requestFocus();
            return false;
        }

        if (cbEquipo != null && cbEquipo.getValue().equals("Seleccione equipo")) {
            AlertHelper.mostrarAdvertencia("Validación", "Debe seleccionar un equipo");
            cbEquipo.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtEspecialidad.clear();

        if (cbEquipo != null) {
            cbEquipo.setValue("Seleccione equipo");
        }

        cuerpoTecnicoSeleccionado = null;
        tablaCuerpoTecnico.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        txtNombre.requestFocus();
    }
}