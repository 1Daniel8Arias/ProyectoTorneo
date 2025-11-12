package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Contrato;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.utils.MensajeUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class NuevoJugadorViewController {

    private boolean esEdicion = false;
    @FXML
    ComboBox<PosicionJugador> cmbPosicion;
    private JugadorController jugadorController;
    private Jugador jugadorEditando;
    private JugadorViewController jugadorViewController;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
    @FXML
    private CheckBox chkEsCapitan;
    @FXML
    private ComboBox<String> cmbEquipo;
    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNumeroCamiseta;

    @FXML
    private TextField txtSalario;

    private Jugador jugador;
    private ObservableList<Jugador> jugadores;

    public Jugador getJugador() {
        return jugador;
    }

    @FXML
    void initialize() {
        jugadorController = new JugadorController();

        cmbEquipo.setItems(FXCollections.observableArrayList(jugadorController.obtenerNombresEquipos()));

        // Cargar posiciones
        cmbPosicion.setItems(FXCollections.observableArrayList(PosicionJugador.values()));


    }


    public void initAttributtes(ObservableList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public void initAttributtes(ObservableList<Jugador> jugadores, Jugador j) {
        this.jugadores = jugadores;
        this.jugador = j;
        // cargo los datos de la persona
        this.txtNombre.setText(j.getNombre());
        this.txtApellido.setText(j.getApellido());
        this.txtNumeroCamiseta.setText(j.getNumeroCamiseta());
        cmbPosicion.setValue(jugador.getPosicion());
        if (jugador.getEquipo() != null) {
            cmbEquipo.setValue(jugador.getEquipo().getNombre());
        }
        if (jugador.getListaContratos() != null && !jugador.getListaContratos().isEmpty()) {
            Contrato contrato = jugador.getListaContratos().get(0);
            dpFechaInicio.setValue(contrato.getFechaInicio());
            dpFechaFin.setValue(contrato.getFechaFin());
            txtSalario.setText(String.valueOf(contrato.getSalario()));
        }


    }

    @FXML
    void handleCancelar(ActionEvent event) {

    }

    @FXML
    void handleGuardar(ActionEvent event) {

        try {
            if (esEdicion) {
                actualizarJugador();
                MensajeUtil.mostrarConfirmacion("Jugador actualizado correctamente");
            } else {
                crearNuevoJugador();
                MensajeUtil.mostrarConfirmacion("Jugador guardado correctamente");
            }

            if (jugadorViewController != null) {
                jugadorViewController.getTableJugadores().refresh();
            }

            cerrarVentana();

        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error al guardar el jugador: " + e.getMessage());
        } catch (Exception e) {
            MensajeUtil.mostrarError("Error inesperado: " + e.getMessage());
        }

    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void crearNuevoJugador() throws RepositoryException {
        // Validar campos
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtNumeroCamiseta.getText().trim().isEmpty() ||
                cmbPosicion.getValue() == null ||
                cmbEquipo.getValue() == null) {
            MensajeUtil.mostrarError("Debe completar todos los campos obligatorios (*)");
            return;
        }

        Jugador nuevoJugador = new Jugador();
        nuevoJugador.setNombre(txtNombre.getText().trim());
        nuevoJugador.setApellido(txtApellido.getText().trim());
        nuevoJugador.setNumeroCamiseta(txtNumeroCamiseta.getText().trim());
        nuevoJugador.setPosicion(cmbPosicion.getValue());

        // Equipo
        String nombreEquipo = cmbEquipo.getValue();
        int idEquipo = jugadorController.obtenerIdEquipoPorNombre(nombreEquipo);
        Equipo equipo = new Equipo();
        equipo.setId(idEquipo);
        equipo.setNombre(nombreEquipo);
        nuevoJugador.setEquipo(equipo);

        // Guardar jugador y obtener su ID (ya lo tienes implementado)
         jugadorController.guardarJugador(nuevoJugador);

        // ✅ Si hay datos de contrato, guardarlo a través del servicio de jugador
        if (dpFechaInicio.getValue() != null &&
                dpFechaFin.getValue() != null &&
                !txtSalario.getText().trim().isEmpty()) {

            Contrato contrato = new Contrato();
            contrato.setFechaInicio(dpFechaInicio.getValue());
            contrato.setFechaFin(dpFechaFin.getValue());
            contrato.setSalario(Double.parseDouble(txtSalario.getText().trim()));

            Jugador jugadorRef = new Jugador();
            jugadorRef.setId(idJugador);
            contrato.setJugador(jugadorRef);

            // Aquí usamos el JugadorService (a través del TorneoService)
            jugadorController.registrarContrato(contrato);
        }

        MensajeUtil.mostrarConfirmacion("Jugador y contrato guardados correctamente");
    }




    private void actualizarJugador() throws RepositoryException {
        jugadorEditando.setNombre(txtNombre.getText().trim());
        jugadorEditando.setApellido(txtApellido.getText().trim());
        jugadorEditando.setNumeroCamiseta(txtNumeroCamiseta.getText().trim());
        jugadorEditando.setPosicion(cmbPosicion.getValue());

        // Actualizar equipo
        String nombreEquipo = cmbEquipo.getValue();
        int idEquipo = jugadorController.obtenerIdEquipoPorNombre(nombreEquipo);
        Equipo equipo = new Equipo();
        equipo.setId(idEquipo);
        equipo.setNombre(nombreEquipo);
        jugadorEditando.setEquipo(equipo);

        jugadorController.actualizarJugador(jugadorEditando);
    }

    public void setJugadorViewController(JugadorViewController controller) {
        this.jugadorViewController = controller;
    }

    public void setEsEdicion(boolean esEdicion) {
        this.esEdicion = esEdicion;
        lblTitulo.setText("Editar Jugador");
    }



}
