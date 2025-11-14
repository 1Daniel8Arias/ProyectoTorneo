package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.EquipoController;
import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ViewController para la gestión de Equipos
 */
public class EquipoViewController {

    @FXML private TableView<Equipo> tablaEquipos;
    @FXML private TableColumn<Equipo, Integer> colId;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colCapitan;

    @FXML private TextField txtNombre;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbCapitan;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    @FXML private Label lblCantidadJugadores;
    @FXML private Label lblTecnico;

    private final EquipoController equipoController;
    private final JugadorController jugadorController;
    private final ObservableList<Equipo> listaEquipos;
    private Equipo equipoSeleccionado;

    public EquipoViewController() {
        this.equipoController = new EquipoController();
        this.jugadorController = new JugadorController();
        this.listaEquipos = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarEquipos();
        configurarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEquipo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Mostrar nombre del capitán si existe
        colCapitan.setCellValueFactory(cellData -> {
            Equipo equipo = cellData.getValue();
            if (equipo.getIdJugadorCapitan() != null) {
                try {
                    Jugador capitan = jugadorController.buscarPorId(equipo.getIdJugadorCapitan());
                    return new javafx.beans.property.SimpleStringProperty(
                            capitan.getNombre() + " " + capitan.getApellido()
                    );
                } catch (RepositoryException e) {
                    return new javafx.beans.property.SimpleStringProperty("Sin capitán");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("Sin capitán");
        });

        tablaEquipos.setItems(listaEquipos);

        tablaEquipos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        equipoSeleccionado = newValue;
                        cargarDatosEnFormulario(newValue);
                    }
                }
        );
    }

    private void configurarEventos() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarEquipos(newValue);
            });
        }
    }

    private void cargarEquipos() {
        try {
            ArrayList<Equipo> equipos = equipoController.listarTodos();
            listaEquipos.clear();
            listaEquipos.addAll(equipos);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar equipos", e.getMessage());
        }
    }

    private void cargarDatosEnFormulario(Equipo equipo) {
        txtNombre.setText(equipo.getNombre());

        // Cargar información adicional
        cargarInformacionAdicional(equipo);

        // Cargar jugadores disponibles para capitán
        cargarJugadoresParaCapitan(equipo.getIdEquipo());

        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        btnGuardar.setDisable(true);
    }

    private void cargarInformacionAdicional(Equipo equipo) {
        try {
            // Cantidad de jugadores
            ArrayList<Jugador> jugadores = equipoController.listarJugadoresPorEquipo(equipo.getIdEquipo());
            if (lblCantidadJugadores != null) {
                lblCantidadJugadores.setText("Jugadores: " + jugadores.size());
            }

            // Técnico
            // Este código depende de si tienes un TecnicoController
            // if (lblTecnico != null) {
            //     Tecnico tecnico = tecnicoController.buscarTecnicoPorEquipo(equipo.getIdEquipo());
            //     if (tecnico != null) {
            //         lblTecnico.setText("DT: " + tecnico.getNombre() + " " + tecnico.getApellido());
            //     } else {
            //         lblTecnico.setText("DT: Sin asignar");
            //     }
            // }

        } catch (RepositoryException e) {
            System.err.println("Error cargando información adicional: " + e.getMessage());
        }
    }

    private void cargarJugadoresParaCapitan(int idEquipo) {
        if (cbCapitan == null) return;

        try {
            ArrayList<Jugador> jugadores = equipoController.listarJugadoresPorEquipo(idEquipo);
            ObservableList<String> nombresJugadores = FXCollections.observableArrayList();
            nombresJugadores.add("Sin capitán");

            for (Jugador jugador : jugadores) {
                nombresJugadores.add(jugador.getNombre() + " " + jugador.getApellido() +
                        " (#" + jugador.getNumeroCamiseta() + ")");
            }

            cbCapitan.setItems(nombresJugadores);
            cbCapitan.setValue("Sin capitán");

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudieron cargar los jugadores: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Equipo nuevoEquipo = new Equipo();
            nuevoEquipo.setNombre(txtNombre.getText().trim());

            equipoController.guardarEquipo(nuevoEquipo);

            AlertHelper.mostrarInformacion("Éxito", "Equipo registrado correctamente");
            cargarEquipos();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (equipoSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un equipo de la tabla");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            equipoSeleccionado.setNombre(txtNombre.getText().trim());

            equipoController.actualizarEquipo(equipoSeleccionado);

            AlertHelper.mostrarInformacion("Éxito", "Equipo actualizado correctamente");
            cargarEquipos();
            limpiarFormulario();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al actualizar", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (equipoSeleccionado == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un equipo de la tabla");
            return;
        }

        Optional<ButtonType> resultado = AlertHelper.mostrarConfirmacion(
                "Confirmar eliminación",
                "¿Está seguro de eliminar el equipo: " + equipoSeleccionado.getNombre() + "?"
        );

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                equipoController.eliminarEquipo(equipoSeleccionado.getIdEquipo());

                AlertHelper.mostrarInformacion("Éxito", "Equipo eliminado correctamente");
                cargarEquipos();
                limpiarFormulario();
            } catch (RepositoryException e) {
                AlertHelper.mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAsignarCapitan() {
        if (equipoSeleccionado == null || cbCapitan == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida", "Debe seleccionar un equipo");
            return;
        }

        String seleccion = cbCapitan.getValue();
        if (seleccion == null || seleccion.equals("Sin capitán")) {
            return;
        }

        try {
            // Extraer el número de camiseta del texto seleccionado
            String numeroCamiseta = seleccion.substring(seleccion.indexOf("#") + 1, seleccion.indexOf(")"));

            // Buscar el jugador por número de camiseta y equipo
            ArrayList<Jugador> jugadores = equipoController.listarJugadoresPorEquipo(equipoSeleccionado.getIdEquipo());
            Jugador capitanSeleccionado = null;

            for (Jugador j : jugadores) {
                if (j.getNumeroCamiseta().equals(numeroCamiseta)) {
                    capitanSeleccionado = j;
                    break;
                }
            }

            if (capitanSeleccionado != null) {
                equipoController.asignarCapitan(equipoSeleccionado.getIdEquipo(), capitanSeleccionado.getIdJugador());
                AlertHelper.mostrarInformacion("Éxito", "Capitán asignado correctamente");
                cargarEquipos();
            }

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudo asignar el capitán: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        limpiarFormulario();
    }

    private void filtrarEquipos(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarEquipos();
            return;
        }

        try {
            ArrayList<Equipo> todosLosEquipos = equipoController.listarTodos();
            ArrayList<Equipo> equiposFiltrados = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (Equipo equipo : todosLosEquipos) {
                if (equipo.getNombre().toLowerCase().contains(terminoLower)) {
                    equiposFiltrados.add(equipo);
                }
            }

            listaEquipos.clear();
            listaEquipos.addAll(equiposFiltrados);
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            AlertHelper.mostrarAdvertencia("Validación", "El nombre del equipo es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        if (cbCapitan != null) {
            cbCapitan.setValue("Sin capitán");
        }
        if (lblCantidadJugadores != null) {
            lblCantidadJugadores.setText("Jugadores: 0");
        }
        if (lblTecnico != null) {
            lblTecnico.setText("DT: Sin asignar");
        }

        equipoSeleccionado = null;
        tablaEquipos.getSelectionModel().clearSelection();

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        txtNombre.requestFocus();
    }
}