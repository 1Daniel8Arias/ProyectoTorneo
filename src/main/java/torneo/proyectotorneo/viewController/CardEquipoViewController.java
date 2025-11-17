package torneo.proyectotorneo.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.EquipoEstadio;
import javafx.scene.image.ImageView;

/**
 * Controlador para la tarjeta de equipo
 * Versión FINAL - Muestra Ciudad (Municipio) del estadio
 */
public class CardEquipoViewController {

    @FXML
    private VBox cardEquipo;

    @FXML
    private Label lblEstadio;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblNJugadores;

    @FXML
    private Label lblDT;

    @FXML
    private Label lblCiudad;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblCiudadT;

    @FXML
    private Label lblDTT;

    @FXML
    private Label lblEstadioT;

    @FXML
    private Label lblNJugadoresT;

    private Equipo equipo;

    /**
     * Establece el equipo y actualiza la vista
     */
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
        actualizarVista();
    }

    /**
     * Actualiza la vista con los datos del equipo
     */
    private void actualizarVista() {
        if (equipo == null) return;

        // Nombre del equipo - SIEMPRE SE MUESTRA
        lblNombre.setText(equipo.getNombre());

        // Director Técnico
        if (equipo.getTecnico() != null) {
            lblDT.setText(equipo.getTecnico().getNombre() + " " + equipo.getTecnico().getApellido());
        } else {
            lblDT.setText("Sin asignar");
        }

        // ✅ ESTADIO - Buscar sede "Local", si no existe tomar el primero
        if (equipo.getEstadios() != null && !equipo.getEstadios().isEmpty()) {
            EquipoEstadio estadioPrincipal = equipo.getEstadios().stream()
                    .filter(ee -> ee.getSede().toString().equalsIgnoreCase("Local") || ee.getSede().toString().equalsIgnoreCase("Neutral") )
                    .findFirst()
                    .orElse(equipo.getEstadios().get(0));

            lblEstadio.setText(estadioPrincipal.getEstadio().getNombre());
        } else {
            lblEstadio.setText("No asignado");
        }

        // ✅ CIUDAD (MUNICIPIO) - Obtener del estadio
        if (equipo.getEstadios() != null && !equipo.getEstadios().isEmpty()) {
            EquipoEstadio estadioPrincipal = equipo.getEstadios().stream()
                    .filter(ee -> ee.getSede().toString().equalsIgnoreCase("Local"))
                    .findFirst()
                    .orElse(equipo.getEstadios().get(0));

            // Obtener el municipio (ciudad) del estadio
            if (estadioPrincipal.getEstadio() != null &&
                    estadioPrincipal.getEstadio().getMunicipio() != null) {
                lblCiudad.setText(estadioPrincipal.getEstadio().getMunicipio().getNombre());
            } else {
                lblCiudad.setText("No disponible");
            }
        } else {
            lblCiudad.setText("No disponible");
        }

        // Cantidad de jugadores - Manejo seguro de la lista
        int cantidadJugadores = equipo.getCantidadJugadores();
        if (cantidadJugadores > 0 || equipo.getListaJugadoresJugadores() != null) {
            lblNJugadores.setText(String.valueOf(cantidadJugadores));
        } else {
            lblNJugadores.setText("0");
        }
    }

    /**
     * Inicialización del controlador de la tarjeta
     */
    @FXML
    private void initialize() {
        cardEquipo.setOnMouseClicked(e -> {
            // Manejo del clic en la tarjeta
            if (equipo != null) {
                System.out.println("Clic en equipo: " + equipo.getNombre());
                // Aquí puedes agregar lógica para abrir detalles del equipo
            }
        });
    }

    /**
     * Controla la visibilidad de los campos de la tarjeta
     *
     * @param dt Si debe mostrar el director técnico
     * @param ciudad Si debe mostrar la ciudad (municipio)
     * @param jugadores Si debe mostrar la cantidad de jugadores
     * @param estadio Si debe mostrar el estadio
     */
    public void mostrarCampos(boolean dt, boolean ciudad, boolean jugadores, boolean estadio) {
        lblDT.setVisible(dt);
        lblDT.setManaged(dt);
        lblDTT.setVisible(dt);
        lblDTT.setManaged(dt);

        lblCiudad.setVisible(ciudad);
        lblCiudad.setManaged(ciudad);
        lblCiudadT.setVisible(ciudad);
        lblCiudadT.setManaged(ciudad);

        lblNJugadores.setVisible(jugadores);
        lblNJugadores.setManaged(jugadores);
        lblNJugadoresT.setVisible(jugadores);
        lblNJugadoresT.setManaged(jugadores);

        lblEstadio.setVisible(estadio);
        lblEstadio.setManaged(estadio);
        lblEstadioT.setVisible(estadio);
        lblEstadioT.setManaged(estadio);
    }
}