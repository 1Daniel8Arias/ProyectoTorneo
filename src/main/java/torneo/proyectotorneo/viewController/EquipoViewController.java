package torneo.proyectotorneo.viewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
    private MenuItem menuEquiposConCantidadDeJugadores;

    @FXML
    private MenuItem menuEquiposConSanciones;

    @FXML
    private MenuItem menuEquiposConTecnico;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoEquipo(ActionEvent event) {

    }

    @FXML
    void handleEquiposConCantidadDeJugadores(ActionEvent event) {
        List<Equipo> equipos = equipoController.listarEquiposConCantidadDeJugadores();
        cargarCards(
                equipos,
                false,   // mostrar DT
                true,  // ocultar ciudad
                true,  // ocultar n° jugadores
                true   // ocultar estadio
        );

    }

    @FXML
    void handleEquiposConSanciones(ActionEvent event) {

    }

    @FXML
    void handleEquiposConTecnico(ActionEvent event) {

    }


    @FXML
    private void initialize() {
        this.equipoController = new EquipoController();
        // ejemplo: obtener lista de equipos (reemplaza por tu repositorio)
        List<Equipo> equipos = equipoController.listarTodos();
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

    private void ejecutarConsultaEquipos(ConsultaEquipo consulta) {
        try {
            List<Equipo> resultado = consulta.ejecutar();

            // limpiar el contenedor
            flowContainer.getChildren().clear();

            // actualizar el contador
            lblContador.setText("Equipos (" + resultado.size() + ")");

            // volver a cargar tarjetas
            for (Equipo eq : resultado) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/CardEquipo.fxml"));
                Node card = loader.load();

                CardEquipoViewController controller = loader.getController();
                controller.setEquipo(eq);

                flowContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface ConsultaEquipo {
        List<Equipo> ejecutar() throws Exception;
    }


    private void cargarCards(List<Equipo> equipos,
                             boolean mostrarDT,
                             boolean mostrarCiudad,
                             boolean mostrarJugadores,
                             boolean mostrarEstadio) {

        flowContainer.getChildren().clear();

        for (Equipo eq : equipos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/CardEquipo.fxml"));
                Node card = loader.load();

                CardEquipoViewController controller = loader.getController();
                controller.setEquipo(eq);

                // 👇 Aquí aplicas visibilidad
                controller.mostrarCampos(mostrarDT, mostrarCiudad, mostrarJugadores, mostrarEstadio);

                flowContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lblContador.setText("Equipos (" + equipos.size() + ")");
    }



}