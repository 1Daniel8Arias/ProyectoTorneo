package torneo.proyectotorneo.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.EquipoEstadio;
import javafx.scene.image.ImageView;

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
            lblDT.setText( equipo.getTecnico().getNombre() + " " + equipo.getTecnico().getApellido());
        } else {
            lblDT.setText("Técnico: Sin asignar");
        }

        // Estadio - Mostrar el estadio principal (sede PRINCIPAL)
        if (equipo.getEstadios() != null && !equipo.getEstadios().isEmpty()) {
            // Buscar el estadio principal
            EquipoEstadio estadioPrincipal = equipo.getEstadios().stream()
                    .filter(ee -> ee.getSede().toString().equals("PRINCIPAL"))
                    .findFirst()
                    .orElse(equipo.getEstadios().get(0)); // Si no hay principal, tomar el primero

            lblEstadio.setText("Estadio: " + estadioPrincipal.getEstadio().getNombre());
        } else {
            lblEstadio.setText("Estadio: No asignado");
        }

        // Ciudad - Puedes obtenerla del estadio o de otra fuente
        // Por ahora la dejamos pendiente ya que no veo un campo ciudad directamente en Equipo
        // Si tienes la ciudad en el estadio, podrías hacer:
        if (equipo.getEstadios() != null && !equipo.getEstadios().isEmpty()) {
            EquipoEstadio estadioPrincipal = equipo.getEstadios().stream()
                    .filter(ee -> ee.getSede().toString().equals("PRINCIPAL"))
                    .findFirst()
                    .orElse(equipo.getEstadios().get(0));

            // Si el estadio tiene ciudad asociada:
            // lblCiudad.setText("Ciudad: " + estadioPrincipal.getEstadio().getCiudad().getNombre());

            // Si no tienes ciudad en el modelo, puedes dejarlo así temporalmente:
            lblCiudad.setText("Ciudad: Por definir");
        } else {
            lblCiudad.setText("Ciudad: No disponible");
        }

        // Cantidad de jugadores - Manejo seguro de la lista
        int cantidadJugadores = equipo.getCantidadJugadores();
        if (cantidadJugadores > 0 || equipo.getListaJugadoresJugadores() != null) {
            lblNJugadores.setText("Jugadores: " + cantidadJugadores);
        } else {
            lblNJugadores.setText("Jugadores: 0");
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
                // Por ejemplo: abrirDetallesEquipo(equipo);
            }
        });
    }

    /**
     * Controla la visibilidad de los campos de la tarjeta
     *
     * @param dt Si debe mostrar el director técnico
     * @param ciudad Si debe mostrar la ciudad
     * @param jugadores Si debe mostrar la cantidad de jugadores
     * @param estadio Si debe mostrar el estadio
     */
    public void mostrarCampos(boolean dt, boolean ciudad, boolean jugadores, boolean estadio) {
        lblDT.setVisible(dt);
       lblDT.setManaged(dt);// setManaged hace que el espacio se colapse cuando está oculto
        lblDTT.setVisible(dt);

        lblCiudad.setVisible(ciudad);
        lblCiudad.setManaged(ciudad);
        lblCiudadT.setVisible(ciudad);

        lblNJugadores.setVisible(jugadores);
        lblNJugadores.setManaged(jugadores);
        lblNJugadoresT.setVisible(jugadores);

        lblEstadio.setVisible(estadio);
        lblEstadio.setManaged(estadio);
        lblEstadio.setVisible(jugadores);
    }
}