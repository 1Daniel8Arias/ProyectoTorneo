package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.EquipoController;
import torneo.proyectotorneo.controller.TecnicoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ViewController para la gestión de Técnicos
 */
public class TecnicoViewController {

    @FXML private TableView<Tecnico> tablaTecnicos;
    @FXML private TableColumn<Tecnico, Integer> colId;
    @FXML private TableColumn<Tecnico, String> colNombre;
    @FXML private TableColumn<Tecnico, String> colApellido;
    @FXML private TableColumn<Tecnico, String> colEquipo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEquipo;
    @FXML private ComboBox<String> cbFiltroEquipo;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    private final TecnicoController tecnicoController;
    private final EquipoController equipoController;
    private final ObservableList<Tecnico> listaTecnicos;
    private Tecnico tecnicoSeleccionado;

    public TecnicoViewController() {
        this.tecnicoController = new TecnicoController();
        this.equipoController = new EquipoController();
        this.listaTecnicos = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarEquipos();
        cargarTecnicos();
        configurarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTecnico"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        colEquipo.setCellValueFactory(cellData -> {
            Tecnico tecnico = cellData.getValue();
            if (tecnico.getEquipo() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        tecnico.getEquipo().getNombre()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("Sin equipo");
        });

        tablaTecnicos.setItems(listaTecnicos);

        tablaTecnicos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        tecnicoSeleccionado = newValue;
                        cargarDatosEnFormulario(newValue);
                    }
                }
        );
    }

    private void configurarEventos() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarTecnicos(newValue);
            });
        }

        if (cbFiltroEquipo != null) {
            cbFiltroEquipo.setOnAction(e -> filtrarPorEquipo());
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

    private void cargarTecnicos() {
        try {
            ArrayList<Tecnico> tecnicos = tecnicoController.listarTodos();
            listaTecnicos.clear();
            listaTecnicos.addAll(tecnicos);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar técnicos", e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(Tecnico tecnico) {
        txtNombre.setText(tecnico.getNombre());
        txtApellido.setText(tecnico.getApellido());

        if (cbEquipo != null && tecnico.getEquipo() != null) {
            cbEquipo.setValue(tecnico.getEquipo().getNombre());
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
            Tecnico nuevoTecnico = new Tecnico();
            nuevoTecnico.setNombre(txtNombre.getText().trim());
            nuevoTecnico.setApellido(txtApellido.getText().trim());

            // Obtener el equipo seleccionado
            String nombreEquipo = cbEquipo.getValue();
            if (nombreEquipo != null && !nombreEquipo.equals("Seleccione equipo")) {
                int idEquipo = equipoController.obtenerIdEquipoPorNombre(nombreEquipo);
                Equipo equipo = new Equipo();
                equipo.setId(idEquipo);
                nuevoTecnico.setEquipo(equipo);
            }

            tecnicoController.guardarTecnico(nuevoTecnico);

            AlertHelper.mostrarInformacion("Éxito", "Técnico registrado correctamente");
            cargarTecnicos();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (tecnicoSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un técnico de la tabla");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            tecnicoSeleccionado.setNombre(txtNombre.getText().trim());
            tecnicoSeleccionado.setApellido(txtApellido.getText().trim());

            // Actualizar equipo
            String nombreEquipo = cbEquipo.getValue();
            if (nombreEquipo != null && !nombreEquipo.equals("Seleccione equipo")) {
                int idEquipo = equipoController.obtenerIdEquipoPorNombre(nombreEquipo);
                Equipo equipo = new Equipo();
                equipo.setId(idEquipo);
                tecnicoSeleccionado.setEquipo(equipo);
            }

            tecnicoController.actualizarTecnico(tecnicoSeleccionado);

            AlertHelper.mostrarInformacion("Éxito", "Técnico actualizado correctamente");
            cargarTecnicos();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al actualizar", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (tecnicoSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un técnico de la tabla");
            return;
        }

        Optional<ButtonType> resultado = AlertHelper.mostrarConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar al técnico: " + tecnicoSeleccionado.getNombre() + " " +
                        tecnicoSeleccionado.getApellido() + "?"
        );

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                tecnicoController.eliminarTecnico(tecnicoSeleccionado.getId());

                AlertHelper.mostrarInformacion("Éxito", "Técnico eliminado correctamente");
                cargarTecnicos();
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

    private void filtrarTecnicos(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarTecnicos();
            return;
        }

        try {
            ArrayList<Tecnico> todosLosTecnicos = tecnicoController.listarTodos();
            ArrayList<Tecnico> tecnicosFiltrados = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (Tecnico tecnico : todosLosTecnicos) {
                if (tecnico.getNombre().toLowerCase().contains(terminoLower) ||
                        tecnico.getApellido().toLowerCase().contains(terminoLower)) {
                    tecnicosFiltrados.add(tecnico);
                }
            }

            listaTecnicos.clear();
            listaTecnicos.addAll(tecnicosFiltrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private void filtrarPorEquipo() {
        if (cbFiltroEquipo == null) return;

        String equipoSeleccionado = cbFiltroEquipo.getValue();

        if (equipoSeleccionado == null || equipoSeleccionado.equals("Todos")) {
            cargarTecnicos();
            return;
        }

        try {
            ArrayList<Tecnico> todosLosTecnicos = tecnicoController.listarTodos();
            ArrayList<Tecnico> tecnicosFiltrados = new ArrayList<>();

            for (Tecnico tecnico : todosLosTecnicos) {
                if (tecnico.getEquipo() != null &&
                        tecnico.getEquipo().getNombre().equals(equipoSeleccionado)) {
                    tecnicosFiltrados.add(tecnico);
                }
            }

            listaTecnicos.clear();
            listaTecnicos.addAll(tecnicosFiltrados);
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

        if (cbEquipo != null) {
            cbEquipo.setValue("Seleccione equipo");
        }

        tecnicoSeleccionado = null;
        tablaTecnicos.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        txtNombre.requestFocus();
    }
}