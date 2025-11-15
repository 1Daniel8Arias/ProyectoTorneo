package torneo.proyectotorneo.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import torneo.proyectotorneo.model.Equipo;

public class CardEquipoViewController {

    @FXML
    private VBox cardEquipo;
    @FXML
    private Label lblEstadio;

    @FXML
    private Label lblDT;

    @FXML
    private Label lblCiudad;

    @FXML
    private Label lblNombre;

    private Equipo equipo;


    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
        actualizarVista();
    }

    private void actualizarVista() {
        if (equipo == null) return;
        lblNombre.setText(equipo.getNombre());
        lblDT.setText("Técnico: " + equipo.getTecnico());
        // lblEstadio.setText("Estadio: " + equipo.getEstadios().get(1));
        lblCiudad.setText("Ciudad: ");

    }

    @FXML
    private void initialize() {
        cardEquipo.setOnMouseClicked(e -> {
            // ejemplo: imprimir o abrir detalles
            System.out.println("Clic en equipo: " + (equipo != null ? equipo.getNombre() : "sin equipo"));

        });
    }

}