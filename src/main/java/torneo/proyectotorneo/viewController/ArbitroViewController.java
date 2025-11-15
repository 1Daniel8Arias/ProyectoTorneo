package torneo.proyectotorneo.viewController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import torneo.proyectotorneo.controller.ArbitroController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.model.ArbitroPartido;
import torneo.proyectotorneo.model.enums.TipoArbitro;

import java.net.URL;
import java.util.*;

/**
 * Controlador de vista para la gestión de árbitros
 */
public class ArbitroViewController implements Initializable {

    // ========== COMPONENTES DEL FXML ==========
    @FXML
    private TableView<ArbitroDTO> tableArbitro;

    @FXML
    private TableColumn<ArbitroDTO, String> colNombre;

    @FXML
    private TableColumn<ArbitroDTO, Integer>colCantidad;

    @FXML
    private TableColumn<ArbitroDTO, String> colTipo;  // Esta columna se mostrará/ocultará

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoArbitro;

    // ========== VARIABLES DE INSTANCIA ==========
    private final ArbitroController arbitroController;
    private ObservableList<ArbitroDTO> listaArbitros;

    public ArbitroViewController() {
        this.arbitroController = new ArbitroController();
        this.listaArbitros = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarComboBox();
        cargarArbitros();
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        // Columna de nombre completo
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreCompleto())
        );

        // Columna de partidos arbitrados
       colCantidad.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPartidosArbitrados()).asObject()
        );

        // Columna de tipo (inicialmente oculta)
        if (colTipo != null) {
            colTipo.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getTipo())
            );
            colTipo.setVisible(false);  // Oculta por defecto
        }

        // Establecer la lista observable en la tabla
        tableArbitro.setItems(listaArbitros);
    }

    /**
     * Configura el ComboBox con los tipos de árbitro
     */
    private void configurarComboBox() {
        ObservableList<String> tipos = FXCollections.observableArrayList(
                TipoArbitro.Principal.name(),
                TipoArbitro.Asistente.name(),
                TipoArbitro.Cuarto.name()
        );
        cmbTipo.setItems(tipos);

        // Listener para filtrar cuando se selecciona un tipo
        cmbTipo.setOnAction(event -> {
            String tipoSeleccionado = cmbTipo.getValue();
            if (tipoSeleccionado != null && !tipoSeleccionado.isEmpty()) {
                filtrarPorTipo();
                // Mostrar la columna de tipo cuando se filtra
                if (colTipo != null) {
                    colTipo.setVisible(true);
                }
            } else {
                // Si no hay selección, ocultar la columna
                if (colTipo != null) {
                    colTipo.setVisible(false);
                }
            }
        });
    }

    /**
     * Carga todos los árbitros desde la base de datos
     */
    private void cargarArbitros() {
        try {
            ArrayList<Arbitro> arbitros = arbitroController.listarTodos();
            actualizarTabla(arbitros, null);

            // Ocultar columna de tipo cuando se muestran todos
            if (colTipo != null) {
                colTipo.setVisible(false);
            }
        } catch (RepositoryException e) {
            mostrarError("Error al cargar árbitros", e.getMessage());
        }
    }

    /**
     * Filtra los árbitros por el tipo seleccionado en el ComboBox
     */
    @FXML
    private void filtrarPorTipo() {
        String tipoSeleccionado = cmbTipo.getValue();
        if (tipoSeleccionado == null || tipoSeleccionado.isEmpty()) {
            return;
        }

        try {
            ArrayList<Arbitro> arbitrosFiltrados = arbitroController.listarArbitrosPorTipo(tipoSeleccionado);
            actualizarTabla(arbitrosFiltrados, tipoSeleccionado);

            // Mostrar la columna de tipo
            if (colTipo != null) {
                colTipo.setVisible(true);
            }
        } catch (RepositoryException e) {
            mostrarError("Error al filtrar árbitros", e.getMessage());
        }
    }

    /**
     * Muestra todos los árbitros (sin filtro)
     */
    @FXML
    private void mostrarTodos() {
        cmbTipo.setValue(null);
        cargarArbitros();
    }

    /**
     * Actualiza la tabla con la lista de árbitros
     */
    @FXML
    private void actualizarTabla() {
        String tipoSeleccionado = cmbTipo.getValue();
        if (tipoSeleccionado != null && !tipoSeleccionado.isEmpty()) {
            filtrarPorTipo();
        } else {
            cargarArbitros();
        }
    }

    /**
     * Actualiza la tabla con una lista específica de árbitros
     * @param arbitros Lista de árbitros a mostrar
     * @param tipoFiltrado Tipo de árbitro seleccionado (null si no hay filtro)
     */
    private void actualizarTabla(ArrayList<Arbitro> arbitros, String tipoFiltrado) {
        listaArbitros.clear();
        for (Arbitro arbitro : arbitros) {
            int partidosArbitrados = calcularPartidosArbitrados(arbitro, tipoFiltrado);
            ArbitroDTO dto = new ArbitroDTO(
                    arbitro.getIdArbitro(),
                    arbitro.getNombreCompleto(),
                    partidosArbitrados,
                    tipoFiltrado != null ? tipoFiltrado : ""
            );
            listaArbitros.add(dto);
        }
    }

    /**
     * Calcula el número de partidos arbitrados por un árbitro
     * @param arbitro El árbitro
     * @param tipoFiltrado Tipo de árbitro (null para contar todos)
     * @return Número de partidos
     */
    private int calcularPartidosArbitrados(Arbitro arbitro, String tipoFiltrado) {
        if (arbitro.getArbitrosPartidos() == null || arbitro.getArbitrosPartidos().isEmpty()) {
            return 0;
        }

        // Usar Set para evitar contar el mismo partido varias veces
        Set<Integer> partidosUnicos = new HashSet<>();

        for (ArbitroPartido ap : arbitro.getArbitrosPartidos()) {
            if (ap.getPartido() != null) {
                // Si hay filtro, solo contar partidos donde arbitró con ese tipo
                if (tipoFiltrado == null || tipoFiltrado.equalsIgnoreCase(ap.getTipo())) {
                    partidosUnicos.add(ap.getPartido().getIdPartido());
                }
            }
        }

        return partidosUnicos.size();
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
     * Muestra un mensaje de información
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void handleBuscar(ActionEvent actionEvent) {
    }

    public void handleNuevoArbitro(ActionEvent actionEvent) {
    }

    /**
     * Clase DTO para mostrar árbitros en la tabla
     */
    public static class ArbitroDTO {
        private final Integer idArbitro;
        private final String nombreCompleto;
        private final Integer partidosArbitrados;
        private final String tipo;

        public ArbitroDTO(Integer idArbitro, String nombreCompleto, Integer partidosArbitrados, String tipo) {
            this.idArbitro = idArbitro;
            this.nombreCompleto = nombreCompleto;
            this.partidosArbitrados = partidosArbitrados;
            this.tipo = tipo;
        }

        public Integer getIdArbitro() {
            return idArbitro;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public Integer getPartidosArbitrados() {
            return partidosArbitrados;
        }

        public String getTipo() {
            return tipo;
        }
    }
}