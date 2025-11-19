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
        // Implementar búsqueda según necesites
    }

    @FXML
    void handleNuevoEquipo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/torneo/proyectotorneo/NuevoEquipo.fxml")
            );

            Node root = loader.load();

            NuevoEquipoViewController controlador = loader.getController();
            controlador.initAttributes();

            // Abrir ventana modal
            javafx.scene.Scene scene = new javafx.scene.Scene((javafx.scene.Parent) root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("Nuevo Equipo");
            stage.showAndWait();

            // Objeto devuelto
            Equipo nuevo = controlador.getEquipoCreado();

            if (nuevo != null) {
                // Agregar tarjeta visual
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/CardEquipo.fxml"));
                Node card = cardLoader.load();

                CardEquipoViewController cardController = cardLoader.getController();
                cardController.setEquipo(nuevo);

                // mostrar campos por defecto (igual al initialize)
                cardController.mostrarCampos(false, true, false, true);

                flowContainer.getChildren().add(card);

                // actualizar contador
                lblContador.setText("Equipos (" + flowContainer.getChildren().size() + ")");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al crear nuevo equipo: " + ex.getMessage());
        }
    }

    /**
     * Muestra los equipos con la cantidad de jugadores que tiene cada uno
     */
    @FXML
    void handleEquiposConCantidadDeJugadores(ActionEvent event) {
        try {
            List<Equipo> equipos = equipoController.listarEquiposConCantidadDeJugadores();
            cargarCards(
                    equipos,
                    false,   // ocultar técnico
                    false,   // mostrar ciudad
                    true,    // MOSTRAR cantidad de jugadores
                    false    // mostrar estadio
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar equipos con cantidad de jugadores: " + e.getMessage());
        }
    }

    /**
     * Muestra los equipos que tienen jugadores sancionados
     */
    @FXML
    void handleEquiposConSanciones(ActionEvent event) {
        try {
            List<Equipo> equipos = equipoController.listarEquiposConSancion();
            cargarCards(
                    equipos,
                    false,   // ocultar técnico
                    false,   // mostrar ciudad
                    false,   // ocultar cantidad de jugadores
                    false    // mostrar estadio
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar equipos con sanciones: " + e.getMessage());
        }
    }

    /**
     * Muestra los equipos con el nombre de su técnico
     */
    @FXML
    void handleEquiposConTecnico(ActionEvent event) {
        try {
            List<Equipo> equipos = equipoController.listarEquiposConTecnico();
            cargarCards(
                    equipos,
                    true,    // MOSTRAR técnico
                    false,   // mostrar ciudad
                    false,   // ocultar cantidad de jugadores
                    false    // mostrar estadio
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar equipos con técnico: " + e.getMessage());
        }
    }

    /**
     * Inicialización del controlador
     * Por defecto muestra: nombre, imagen, estadio y ciudad
     * Oculta: técnico y cantidad de jugadores (solo se muestran en consultas específicas)
     */
    @FXML
    private void initialize() {
        this.equipoController = new EquipoController();
        try {
            // Obtener lista de todos los equipos
            List<Equipo> equipos = equipoController.listarTodos();
            lblContador.setText("Equipos (" + equipos.size() + ")");

            // Cargar cards mostrando solo: nombre, imagen, estadio y ciudad
            // Ocultar: técnico y cantidad de jugadores (aparecerán solo en consultas)
            cargarCards(
                    equipos,
                    false,   // ocultar técnico
                    true,   // mostrar ciudad
                    false,   // ocultar cantidad de jugadores
                    true    // mostrar estadio
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al inicializar equipos: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para cargar las tarjetas de equipos con visibilidad personalizada
     *
     * @param equipos Lista de equipos a mostrar
     * @param mostrarDT Si debe mostrar el director técnico
     * @param mostrarCiudad Si debe mostrar la ciudad
     * @param mostrarJugadores Si debe mostrar la cantidad de jugadores
     * @param mostrarEstadio Si debe mostrar el estadio
     */
    private void cargarCards(List<Equipo> equipos,
                             boolean mostrarDT,
                             boolean mostrarCiudad,
                             boolean mostrarJugadores,
                             boolean mostrarEstadio) {

        // Limpiar el contenedor
        flowContainer.getChildren().clear();

        // Cargar cada tarjeta
        for (Equipo eq : equipos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/CardEquipo.fxml"));
                Node card = loader.load();

                CardEquipoViewController controller = loader.getController();
                controller.setEquipo(eq);

                // Aplicar visibilidad de campos según los parámetros
                controller.mostrarCampos(mostrarDT, mostrarCiudad, mostrarJugadores, mostrarEstadio);

                flowContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al cargar la tarjeta del equipo " + eq.getNombre() + ": " + e.getMessage());
            }
        }

        // Actualizar el contador
        lblContador.setText("Equipos (" + equipos.size() + ")");
    }

    /**
     * Interfaz funcional para ejecutar consultas de equipos
     */
    @FunctionalInterface
    private interface ConsultaEquipo {
        List<Equipo> ejecutar() throws Exception;
    }

    /**
     * Método auxiliar alternativo para ejecutar consultas
     * (Si prefieres usarlo en lugar de llamar directamente a cargarCards)
     */
    private void ejecutarConsultaEquipos(ConsultaEquipo consulta) {
        try {
            List<Equipo> resultado = consulta.ejecutar();

            // Limpiar el contenedor
            flowContainer.getChildren().clear();

            // Actualizar el contador
            lblContador.setText("Equipos (" + resultado.size() + ")");

            // Volver a cargar tarjetas
            for (Equipo eq : resultado) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/CardEquipo.fxml"));
                Node card = loader.load();

                CardEquipoViewController controller = loader.getController();
                controller.setEquipo(eq);

                flowContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al ejecutar consulta de equipos: " + e.getMessage());
        }
    }
}