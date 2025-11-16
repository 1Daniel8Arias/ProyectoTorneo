package torneo.proyectotorneo.viewController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import torneo.proyectotorneo.controller.EstadioController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Estadio;
import torneo.proyectotorneo.model.EquipoEstadio;
import torneo.proyectotorneo.model.enums.TipoSede;
import torneo.proyectotorneo.utils.MensajeUtil;

import java.net.URL;
import java.util.*;

/**
 * Controlador de vista para la gestión de estadios
 * Versión SIN DTO - Usa directamente las clases del modelo
 */
public class EstadioViewController implements Initializable {

    // ========== COMPONENTES DEL FXML ==========
    @FXML
    private TableView<EquipoEstadio> tableEstadio;

    @FXML
    private TableColumn<EquipoEstadio, Integer> colNumero;

    @FXML
    private TableColumn<EquipoEstadio, String> colNombre;

    @FXML
    private TableColumn<EquipoEstadio, String> colTipo;

    @FXML
    private TableColumn<EquipoEstadio, Integer> colCapacidad;

    @FXML
    private TableColumn<EquipoEstadio, String> colEquipo;

    @FXML
    private TableColumn<EquipoEstadio, String> colDepartamento;

    @FXML
    private TableColumn<EquipoEstadio, String> colMunicipio;

    @FXML
    private TableColumn<EquipoEstadio, String> colCiudad;

    @FXML
    private TableColumn<EquipoEstadio, Void> colAcciones;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private ComboBox<String> cmbEquipo;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoEstadio;

    @FXML
    private Label lblContador;

    // ========== VARIABLES DE INSTANCIA ==========
    private final EstadioController estadioController;
    private ObservableList<EquipoEstadio> listaEquipoEstadios;
    private List<EquipoEstadio> todosLosEquipoEstadios; // Lista completa para búsquedas

    public EstadioViewController() {
        this.estadioController = new EstadioController();
        this.listaEquipoEstadios = FXCollections.observableArrayList();
        this.todosLosEquipoEstadios = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarComboBoxes();
        cargarEstadios();
        configurarBusqueda();
    }

    /**
     * Configura las columnas de la tabla usando directamente EquipoEstadio
     */
    private void configurarTabla() {
        // Columna de número (contador)
        colNumero.setCellValueFactory(cellData -> {
            int index = tableEstadio.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });

        // Columna de nombre del estadio
        colNombre.setCellValueFactory(cellData -> {
            Estadio estadio = cellData.getValue().getEstadio();
            String nombre = estadio != null ? estadio.getNombre() : "N/A";
            return new SimpleStringProperty(nombre);
        });

        // Columna de tipo (Local/Neutral)
        colTipo.setCellValueFactory(cellData -> {
            TipoSede tipo = cellData.getValue().getSede();
            String tipoStr = tipo != null ? tipo.name() : "N/A";
            return new SimpleStringProperty(tipoStr);
        });

        // Columna de capacidad
        colCapacidad.setCellValueFactory(cellData -> {
            Estadio estadio = cellData.getValue().getEstadio();
            int capacidad = estadio != null ? estadio.getCapacidad() : 0;
            return new SimpleIntegerProperty(capacidad).asObject();
        });

        // Columna de equipo
        colEquipo.setCellValueFactory(cellData -> {
            EquipoEstadio ee = cellData.getValue();
            String nombreEquipo = (ee.getEquipo() != null) ? ee.getEquipo().getNombre() : "Sin equipo";
            return new SimpleStringProperty(nombreEquipo);
        });

        // Columna de departamento
        colDepartamento.setCellValueFactory(cellData -> {
            Estadio estadio = cellData.getValue().getEstadio();
            String dept = (estadio != null && estadio.getMunicipio() != null)
                    ? estadio.getMunicipio().getNombre()
                    : "N/A";
            return new SimpleStringProperty(dept);
        });

        // Columna de municipio
        colMunicipio.setCellValueFactory(cellData -> {
            // NOTA: Actualmente no existe relación directa en tu modelo
            // Si agregas municipio al modelo Estadio, cambiar a:
            // estadio.getMunicipio().getNombre()
            return new SimpleStringProperty("N/A");
        });

        // Columna de ciudad
        colCiudad.setCellValueFactory(cellData -> {
            // NOTA: Actualmente no existe relación directa en tu modelo
            // Si agregas ciudad al modelo Estadio, cambiar a:
            // estadio.getCiudad().getNombre()
            return new SimpleStringProperty("N/A");
        });

        // Configurar columna de acciones
        configurarColumnaAcciones();

        // Establecer la lista observable en la tabla
        tableEstadio.setItems(listaEquipoEstadios);
    }

    /**
     * Configura la columna de acciones con botones
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(10);

            {
                btnEditar.getStyleClass().add("btn-edit");
                btnEliminar.getStyleClass().add("btn-delete");

                contenedor.setAlignment(Pos.CENTER);
                contenedor.getChildren().addAll(btnEditar, btnEliminar);

                // Evento para editar
                btnEditar.setOnAction(event -> {
                    EquipoEstadio equipoEstadio = getTableView().getItems().get(getIndex());
                    editarEstadio(equipoEstadio.getEstadio());
                });

                // Evento para eliminar
                btnEliminar.setOnAction(event -> {
                    EquipoEstadio equipoEstadio = getTableView().getItems().get(getIndex());
                    eliminarEstadio(equipoEstadio.getEstadio());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });
    }

    /**
     * Configura los ComboBox con los tipos y equipos
     */
    private void configurarComboBoxes() {
        // Configurar ComboBox de tipos
        ObservableList<String> tipos = FXCollections.observableArrayList(
                "Todos",
                TipoSede.Local.name(),
                TipoSede.Neutral.name()
        );
        cmbTipo.setItems(tipos);
        cmbTipo.setValue("Todos");

        // Listener para filtrar cuando se selecciona un tipo
        cmbTipo.setOnAction(event -> aplicarFiltros());

        // Configurar ComboBox de equipos
        ObservableList<String> equipos = FXCollections.observableArrayList("Todos");
        cmbEquipo.setItems(equipos);
        cmbEquipo.setValue("Todos");

        cmbEquipo.setOnAction(event -> aplicarFiltros());
    }

    /**
     * Configura el campo de búsqueda con listener en tiempo real
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });
    }

    /**
     * Carga todos los estadios desde la base de datos
     * Convierte cada Estadio en sus EquipoEstadio correspondientes
     */
    private void cargarEstadios() {
        try {
            ArrayList<Estadio> estadios = estadioController.listarTodos();
            todosLosEquipoEstadios.clear();

            // Convertir cada estadio en sus relaciones EquipoEstadio
            for (Estadio estadio : estadios) {
                if (estadio.getEquipoEstadios() != null && !estadio.getEquipoEstadios().isEmpty()) {
                    // Agregar todas las relaciones equipo-estadio
                    todosLosEquipoEstadios.addAll(estadio.getEquipoEstadios());
                } else {
                    // Si el estadio no tiene equipos, crear una entrada temporal
                    // para que el estadio aparezca en la tabla
                    EquipoEstadio sinEquipo = new EquipoEstadio();
                    sinEquipo.setEstadio(estadio);
                    sinEquipo.setEquipo(null);
                    sinEquipo.setSede(null);
                    todosLosEquipoEstadios.add(sinEquipo);
                }
            }

            actualizarTabla(todosLosEquipoEstadios);
        } catch (RepositoryException e) {
            mostrarError("Error al cargar estadios", e.getMessage());
        }
    }

    /**
     * Actualiza la tabla con la lista de EquipoEstadio proporcionada
     */
    private void actualizarTabla(List<EquipoEstadio> equipoEstadios) {
        listaEquipoEstadios.clear();
        listaEquipoEstadios.addAll(equipoEstadios);
        actualizarContador();
    }

    /**
     * Aplica los filtros de búsqueda, tipo y equipo
     */
    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        String tipoSeleccionado = cmbTipo.getValue();
        String equipoSeleccionado = cmbEquipo.getValue();

        List<EquipoEstadio> filtrados = new ArrayList<>(todosLosEquipoEstadios);

        // Filtrar por texto de búsqueda (nombre del estadio)
        if (!textoBusqueda.isEmpty()) {
            filtrados = filtrados.stream()
                    .filter(ee -> {
                        Estadio estadio = ee.getEstadio();
                        return estadio != null &&
                                estadio.getNombre().toLowerCase().contains(textoBusqueda);
                    })
                    .toList();
        }

        // Filtrar por tipo
        if (tipoSeleccionado != null && !tipoSeleccionado.equals("Todos")) {
            TipoSede tipoSede = TipoSede.valueOf(tipoSeleccionado);
            filtrados = filtrados.stream()
                    .filter(ee -> ee.getSede() == tipoSede)
                    .toList();
        }

        // Filtrar por equipo
        if (equipoSeleccionado != null && !equipoSeleccionado.equals("Todos")) {
            filtrados = filtrados.stream()
                    .filter(ee -> ee.getEquipo() != null &&
                            ee.getEquipo().getNombre().equals(equipoSeleccionado))
                    .toList();
        }

        actualizarTabla(filtrados);
    }

    /**
     * Actualiza el contador de estadios
     */
    private void actualizarContador() {
        // Contar estadios únicos (no repetir por cada equipo)
        long estadiosUnicos = listaEquipoEstadios.stream()
                .map(ee -> ee.getEstadio().getIdEstadio())
                .distinct()
                .count();

        int totalRelaciones = listaEquipoEstadios.size();

        lblContador.setText(String.format("Lista de Estadios (%d estadios, %d relaciones)",
                estadiosUnicos, totalRelaciones));
    }

    /**
     * Maneja el evento del botón Buscar
     */
    @FXML
    void handleBuscar(ActionEvent event) {
        aplicarFiltros();
    }

    /**
     * Maneja el evento del botón Nuevo Estadio
     */
    @FXML
    void handleNuevoEstadio(ActionEvent event) {
        // Aquí deberías abrir un diálogo o ventana para crear un nuevo estadio
        MensajeUtil.mostrarInfo(
                "Nuevo Estadio",
                "Funcionalidad en desarrollo"+
                "La ventana para crear un nuevo estadio se abrirá aquí."
        );
    }

    /**
     * Edita un estadio existente
     */
    private void editarEstadio(Estadio estadio) {
        // Aquí deberías abrir un diálogo o ventana para editar el estadio
        MensajeUtil.mostrarInfo(
                "Editar Estadio",
                "Editar: " + estadio.getNombre()+
                "La ventana de edición se abrirá aquí."
        );
    }

    /**
     * Elimina un estadio con confirmación
     */
    private void eliminarEstadio(Estadio estadio) {
        boolean confirmado = MensajeUtil.mostrarConfirmacion(
                "Eliminar Estadio"+
                "¿Está seguro de eliminar el estadio?"+
                "Estadio: " + estadio.getNombre() + "\n" +
                        "Capacidad: " + estadio.getCapacidad() + "\n" +
                        "Esta acción no se puede deshacer."
        );

        if (confirmado) {
            try {
                estadioController.eliminarEstadio(estadio.getIdEstadio());
                MensajeUtil.mostrarExito(
                        "Estadio Eliminado"+
                        "El estadio se ha eliminado correctamente"
                );
                cargarEstadios(); // Recargar la lista
            } catch (RepositoryException e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        MensajeUtil.mostrarError( mensaje);
    }
}