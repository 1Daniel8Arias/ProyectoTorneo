package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.PartidoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;
import torneo.proyectotorneo.utils.MensajeUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la ventana de crear/editar partido
 */
public class NuevoPartidoViewController {

    private PartidoViewController partidoViewController;
    private PartidoController partidoController;
    private ModelFactoryController modelFactory;
    private Partido partidoEditando;
    private boolean modoEdicion = false;

    @FXML private Label lblTitulo;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private ComboBox<String> cmbJornada;
    @FXML private ComboBox<String> cmbEquipoLocal;
    @FXML private ComboBox<String> cmbEquipoVisitante;
    @FXML private ComboBox<String> cmbEstadio;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    /**
     * Inicializa el controlador
     */
    @FXML
    void initialize() {
        partidoController = new PartidoController();
        modelFactory = ModelFactoryController.getInstance();
        cargarDatosComboBox();
        configurarValidaciones();
    }

    /**
     * Carga los datos en los ComboBox
     */
    private void cargarDatosComboBox() {
        try {
            // Cargar jornadas
            ArrayList<Jornada> jornadas = modelFactory.getTorneoService().listarJornadas();
            List<String> jornadasStr = new ArrayList<>();
            for (Jornada j : jornadas) {
                jornadasStr.add("Jornada " + j.getNumeroJornada());
            }
            cmbJornada.setItems(FXCollections.observableArrayList(jornadasStr));

            // Cargar equipos
            ArrayList<Equipo> equipos = modelFactory.obtenerEquipos();
            List<String> equiposStr = new ArrayList<>();
            for (Equipo e : equipos) {
                equiposStr.add(e.getNombre());
            }
            cmbEquipoLocal.setItems(FXCollections.observableArrayList(equiposStr));
            cmbEquipoVisitante.setItems(FXCollections.observableArrayList(equiposStr));

            // Cargar estadios
            ArrayList<Estadio> estadios = modelFactory.getTorneoService().listarEstadios();
            List<String> estadiosStr = new ArrayList<>();
            for (Estadio est : estadios) {
                estadiosStr.add(est.getNombre());
            }
            cmbEstadio.setItems(FXCollections.observableArrayList(estadiosStr));

        } catch (Exception e) {
            MensajeUtil.mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    /**
     * Configura las validaciones de los campos
     */
    private void configurarValidaciones() {
        // Validar que la hora tenga formato HH:MM
        txtHora.setPromptText("HH:MM (Ej: 15:30)");

        // Agregar listener para validar formato de hora mientras escribe
        txtHora.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                if (newValue.length() > 5) {
                    txtHora.setText(oldValue);
                }
            }
        });
    }

    /**
     * Establece la referencia al controlador principal
     */
    public void setPartidoViewController(PartidoViewController controller) {
        this.partidoViewController = controller;
    }

    /**
     * Carga los datos de un partido para edición
     */
    public void cargarPartido(Partido partido) {
        this.partidoEditando = partido;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Partido");
        btnGuardar.setText("💾 Actualizar");

        try {
            // Cargar fecha
            dpFecha.setValue(partido.getFecha());

            // Cargar hora
            txtHora.setText(partido.getHora());

            // Cargar jornada
            if (partido.getJornada() != null) {
                cmbJornada.setValue("Jornada " + partido.getJornada().getNumeroJornada());
            }

            // Cargar equipo local
            if (partido.getEquipoLocal() != null) {
                cmbEquipoLocal.setValue(partido.getEquipoLocal().getNombre());
            }

            // Cargar equipo visitante
            if (partido.getEquipoVisitante() != null) {
                cmbEquipoVisitante.setValue(partido.getEquipoVisitante().getNombre());
            }

            // Cargar estadio
            if (partido.getEstadio() != null) {
                cmbEstadio.setValue(partido.getEstadio().getNombre());
            }

        } catch (Exception e) {
            MensajeUtil.mostrarError("Error al cargar datos del partido: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de guardar
     */
    @FXML
    void handleGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        try {
            Partido partido = construirPartido();

            if (modoEdicion) {
                partidoController.actualizarPartido(partido);
                MensajeUtil.mostrarExito("Partido actualizado correctamente");
            } else {
                partidoController.guardarPartido(partido);
                MensajeUtil.mostrarExito("Partido creado correctamente");
            }

            cerrarVentana();

        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al guardar: " + e.getMessage());
        } catch (Exception e) {
            MensajeUtil.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de cancelar
     */
    @FXML
    void handleCancelar(ActionEvent event) {
        if (MensajeUtil.mostrarConfirmacion("¿Está seguro de cancelar? Se perderán los cambios no guardados.")) {
            cerrarVentana();
        }
    }

    /**
     * Valida que todos los campos obligatorios estén completos
     */
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (dpFecha.getValue() == null) {
            errores.append("- Debe seleccionar una fecha\n");
        }

        if (txtHora.getText() == null || txtHora.getText().trim().isEmpty()) {
            errores.append("- Debe ingresar una hora\n");
        } else if (!txtHora.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            errores.append("- La hora debe tener formato HH:MM (Ej: 15:30)\n");
        }

        if (cmbJornada.getValue() == null) {
            errores.append("- Debe seleccionar una jornada\n");
        }

        if (cmbEquipoLocal.getValue() == null) {
            errores.append("- Debe seleccionar el equipo local\n");
        }

        if (cmbEquipoVisitante.getValue() == null) {
            errores.append("- Debe seleccionar el equipo visitante\n");
        }

        if (cmbEquipoLocal.getValue() != null && cmbEquipoLocal.getValue().equals(cmbEquipoVisitante.getValue())) {
            errores.append("- El equipo local y visitante no pueden ser el mismo\n");
        }

        if (cmbEstadio.getValue() == null) {
            errores.append("- Debe seleccionar un estadio\n");
        }

        if (errores.length() > 0) {
            MensajeUtil.mostrarAdvertencia("Por favor complete los siguientes campos:\n\n" + errores.toString());
            return false;
        }

        return true;
    }

    /**
     * Construye el objeto Partido desde los campos del formulario
     */
    private Partido construirPartido() throws RepositoryException {
        Partido partido = modoEdicion ? partidoEditando : new Partido();

        // Fecha y hora
        partido.setFecha(dpFecha.getValue());
        partido.setHora(txtHora.getText().trim());

        // Jornada
        String jornadaStr = cmbJornada.getValue().replace("Jornada ", "");
        int numeroJornada = Integer.parseInt(jornadaStr);
        Jornada jornada = buscarJornadaPorNumero(numeroJornada);
        partido.setJornada(jornada);

        // Equipo Local
        String nombreLocal = cmbEquipoLocal.getValue();
        Equipo equipoLocal = buscarEquipoPorNombre(nombreLocal);
        partido.setEquipoLocal(equipoLocal);

        // Equipo Visitante
        String nombreVisitante = cmbEquipoVisitante.getValue();
        Equipo equipoVisitante = buscarEquipoPorNombre(nombreVisitante);
        partido.setEquipoVisitante(equipoVisitante);

        // Estadio
        String nombreEstadio = cmbEstadio.getValue();
        Estadio estadio = buscarEstadioPorNombre(nombreEstadio);
        partido.setEstadio(estadio);

        return partido;
    }

    /**
     * Busca una jornada por su número
     */
    private Jornada buscarJornadaPorNumero(int numero) throws RepositoryException {
        ArrayList<Jornada> jornadas = modelFactory.getTorneoService().listarJornadas();
        for (Jornada j : jornadas) {
            if (j.getNumeroJornada() == numero) {
                return j;
            }
        }
        throw new RepositoryException("Jornada no encontrada");
    }

    /**
     * Busca un equipo por su nombre
     */
    private Equipo buscarEquipoPorNombre(String nombre) throws RepositoryException {
        ArrayList<Equipo> equipos = modelFactory.obtenerEquipos();
        for (Equipo e : equipos) {
            if (e.getNombre().equals(nombre)) {
                return e;
            }
        }
        throw new RepositoryException("Equipo no encontrado: " + nombre);
    }

    /**
     * Busca un estadio por su nombre
     */
    private Estadio buscarEstadioPorNombre(String nombre) throws RepositoryException {
        ArrayList<Estadio> estadios = modelFactory.getTorneoService().listarEstadios();
        for (Estadio est : estadios) {
            if (est.getNombre().equals(nombre)) {
                return est;
            }
        }
        throw new RepositoryException("Estadio no encontrado: " + nombre);
    }

    /**
     * Cierra la ventana actual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}