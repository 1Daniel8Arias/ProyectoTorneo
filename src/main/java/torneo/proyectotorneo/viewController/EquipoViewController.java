package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import torneo.proyectotorneo.controller.EquipoController;
import torneo.proyectotorneo.model.Equipo;

import java.io.IOException;
import java.util.List;

public class EquipoViewController {

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoEquipo;

    @FXML
    private FlowPane flowContainer;

    @FXML
    private Label lblContador;

    @FXML
    private TextField txtBuscar;

    private EquipoController equipoController;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoEquipo(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        this.equipoController = new EquipoController();
        // ejemplo: obtener lista de equipos (reemplaza por tu repositorio)
        List<Equipo> equipos = equipoController.obtenerEquipos();
        int con = equipos.size();
        lblContador.setText(String.valueOf(con));

        // cargar cada tarjeta
        for (Equipo eq : equipos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/CardEquipo.fxml"));
                Node card = loader.load();
                CardEquipoViewController controller = loader.getController();
                controller.setEquipo(eq); // pasar datos al controlador de la tarjeta

                // opcional: fijar tamaño de la tarjeta (o usar css)
                card.setUserData(eq);
                flowContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
