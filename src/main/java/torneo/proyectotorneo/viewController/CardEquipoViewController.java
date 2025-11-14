package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import torneo.proyectotorneo.controller.CardEquipoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ViewController para visualizar tarjetas/cards de equipos
 * Muestra información completa del equipo en un formato visual tipo card
 */
public class CardEquipoViewController {

    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPaneCards;
    @FXML private ComboBox<String> cbEquipos;
    @FXML private TextField txtBuscar;
    @FXML private Button btnMostrarTodos;
    @FXML private Button btnActualizar;

    private final CardEquipoController cardEquipoController;
    private ArrayList<Equipo> equipos;

    public CardEquipoViewController() {
        this.cardEquipoController = new CardEquipoController();
        this.equipos = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        configurarFlowPane();
        cargarEquipos();
        cargarCards();
        configurarEventos();
    }

    private void configurarFlowPane() {
        if (flowPaneCards != null) {
            flowPaneCards.setHgap(20);
            flowPaneCards.setVgap(20);
            flowPaneCards.setPadding(new Insets(20));
        }
    }

    private void configurarEventos() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarCards(newValue);
            });
        }

        if (cbEquipos != null) {
            cbEquipos.setOnAction(e -> {
                String equipoSeleccionado = cbEquipos.getValue();
                if (equipoSeleccionado != null && !equipoSeleccionado.equals("Todos los equipos")) {
                    mostrarCardEspecifica(equipoSeleccionado);
                } else {
                    cargarCards();
                }
            });
        }
    }

    private void cargarEquipos() {
        try {
            equipos = cardEquipoController.listarTodosLosEquipos();

            if (cbEquipos != null) {
                ObservableList<String> nombresEquipos = FXCollections.observableArrayList();
                nombresEquipos.add("Todos los equipos");
                for (Equipo equipo : equipos) {
                    nombresEquipos.add(equipo.getNombre());
                }
                cbEquipos.setItems(nombresEquipos);
                cbEquipos.setValue("Todos los equipos");
            }
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudieron cargar los equipos: " + e.getMessage());
        }
    }

    private void cargarCards() {
        if (flowPaneCards == null) return;

        flowPaneCards.getChildren().clear();

        try {
            for (Equipo equipo : equipos) {
                VBox card = crearCard(equipo);
                flowPaneCards.getChildren().add(card);
            }
        } catch (Exception e) {
            AlertHelper.mostrarError("Error", "Error al cargar las cards: " + e.getMessage());
        }
    }

    private VBox crearCard(Equipo equipo) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setMinWidth(300);
        card.setMaxWidth(350);

        // Título del equipo
        Label lblNombreEquipo = new Label(equipo.getNombre());
        lblNombreEquipo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblNombreEquipo.setStyle("-fx-text-fill: #1976d2;");

        // Información del técnico
        HBox hboxTecnico = new HBox(5);
        Label lblTecnicoLabel = new Label("DT:");
        lblTecnicoLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label lblTecnicoNombre = new Label();

        try {
            Tecnico tecnico = cardEquipoController.obtenerTecnicoDelEquipo(equipo.getId());
            if (tecnico != null) {
                lblTecnicoNombre.setText(tecnico.getNombre() + " " + tecnico.getApellido());
            } else {
                lblTecnicoNombre.setText("Sin asignar");
                lblTecnicoNombre.setStyle("-fx-text-fill: #757575;");
            }
        } catch (RepositoryException e) {
            lblTecnicoNombre.setText("Sin asignar");
            lblTecnicoNombre.setStyle("-fx-text-fill: #757575;");
        }

        hboxTecnico.getChildren().addAll(lblTecnicoLabel, lblTecnicoNombre);

        // Información del capitán
        HBox hboxCapitan = new HBox(5);
        Label lblCapitanLabel = new Label("Capitán:");
        lblCapitanLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label lblCapitanNombre = new Label();

        try {
            Jugador capitan = cardEquipoController.obtenerCapitanDelEquipo(equipo.getId());
            if (capitan != null) {
                lblCapitanNombre.setText(capitan.getNombre() + " " + capitan.getApellido() +
                        " (#" + capitan.getNumeroCamiseta() + ")");
            } else {
                lblCapitanNombre.setText("Sin asignar");
                lblCapitanNombre.setStyle("-fx-text-fill: #757575;");
            }
        } catch (RepositoryException e) {
            lblCapitanNombre.setText("Sin asignar");
            lblCapitanNombre.setStyle("-fx-text-fill: #757575;");
        }

        hboxCapitan.getChildren().addAll(lblCapitanLabel, lblCapitanNombre);

        // Separador
        Separator separator = new Separator();

        // Estadísticas de jugadores
        Label lblJugadoresLabel = new Label("Plantilla:");
        lblJugadoresLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblJugadoresLabel.setStyle("-fx-text-fill: #424242;");

        VBox vboxJugadores = new VBox(5);

        try {
            ArrayList<Jugador> jugadores = cardEquipoController.obtenerJugadoresDelEquipo(equipo.getId());

            // Conteo por posición
            Map<PosicionJugador, Integer> conteo = new HashMap<>();
            for (Jugador jugador : jugadores) {
                conteo.put(jugador.getPosicion(), conteo.getOrDefault(jugador.getPosicion(), 0) + 1);
            }

            // Total
            Label lblTotal = new Label("Total: " + jugadores.size() + " jugadores");
            lblTotal.setStyle("-fx-font-weight: bold;");
            vboxJugadores.getChildren().add(lblTotal);

            // Por posición
            for (PosicionJugador posicion : PosicionJugador.values()) {
                int cantidad = conteo.getOrDefault(posicion, 0);
                if (cantidad > 0) {
                    HBox hboxPosicion = new HBox(10);
                    Label lblPosicion = new Label("• " + posicion.name() + ":");
                    lblPosicion.setMinWidth(120);
                    Label lblCantidad = new Label(String.valueOf(cantidad));
                    lblCantidad.setStyle("-fx-font-weight: bold; -fx-text-fill: #1976d2;");
                    hboxPosicion.getChildren().addAll(lblPosicion, lblCantidad);
                    vboxJugadores.getChildren().add(hboxPosicion);
                }
            }

        } catch (RepositoryException e) {
            Label lblError = new Label("No se pudo cargar la información");
            lblError.setStyle("-fx-text-fill: #d32f2f;");
            vboxJugadores.getChildren().add(lblError);
        }

        // Botón para ver detalle
        Button btnVerDetalle = new Button("Ver Detalle Completo");
        btnVerDetalle.setStyle(
                "-fx-background-color: #1976d2; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        );
        btnVerDetalle.setMaxWidth(Double.MAX_VALUE);
        btnVerDetalle.setOnAction(e -> mostrarDetalleCompleto(equipo));

        // Agregar todos los componentes a la card
        card.getChildren().addAll(
                lblNombreEquipo,
                hboxTecnico,
                hboxCapitan,
                separator,
                lblJugadoresLabel,
                vboxJugadores,
                btnVerDetalle
        );

        // Efecto hover
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #1976d2; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3);"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            );
        });

        return card;
    }

    private void mostrarDetalleCompleto(Equipo equipo) {
        try {
            ArrayList<Jugador> jugadores = cardEquipoController.obtenerJugadoresDelEquipo(equipo.getId());
            Tecnico tecnico = cardEquipoController.obtenerTecnicoDelEquipo(equipo.getId());
            Jugador capitan = cardEquipoController.obtenerCapitanDelEquipo(equipo.getId());

            // Crear diálogo con la información completa
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Detalle Completo del Equipo");
            dialog.setHeaderText(equipo.getNombre());

            // Contenido del diálogo
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setMinWidth(500);

            // Información del técnico
            Label lblTecnicoTitle = new Label("Director Técnico:");
            lblTecnicoTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            Label lblTecnico = new Label(tecnico != null ?
                    tecnico.getNombre() + " " + tecnico.getApellido() : "Sin asignar");

            // Información del capitán
            Label lblCapitanTitle = new Label("Capitán:");
            lblCapitanTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            Label lblCapitan = new Label(capitan != null ?
                    capitan.getNombre() + " " + capitan.getApellido() +
                            " (#" + capitan.getNumeroCamiseta() + ")" : "Sin asignar");

            // Lista de jugadores
            Label lblJugadoresTitle = new Label("Plantilla Completa (" + jugadores.size() + " jugadores):");
            lblJugadoresTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

            ListView<String> listViewJugadores = new ListView<>();
            ObservableList<String> jugadoresInfo = FXCollections.observableArrayList();

            for (Jugador j : jugadores) {
                String esCapitan = (capitan != null && j.getId() == capitan.getId()) ? " (C)" : "";
                jugadoresInfo.add(String.format("#%s - %s %s - %s%s",
                        j.getNumeroCamiseta(),
                        j.getNombre(),
                        j.getApellido(),
                        j.getPosicion().name(),
                        esCapitan
                ));
            }

            listViewJugadores.setItems(jugadoresInfo);
            listViewJugadores.setPrefHeight(300);

            content.getChildren().addAll(
                    lblTecnicoTitle, lblTecnico,
                    new Separator(),
                    lblCapitanTitle, lblCapitan,
                    new Separator(),
                    lblJugadoresTitle, listViewJugadores
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error", "No se pudo cargar la información completa: " + e.getMessage());
        }
    }

    private void filtrarCards(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarCards();
            return;
        }

        if (flowPaneCards == null) return;

        flowPaneCards.getChildren().clear();

        String terminoLower = termino.toLowerCase();

        for (Equipo equipo : equipos) {
            if (equipo.getNombre().toLowerCase().contains(terminoLower)) {
                VBox card = crearCard(equipo);
                flowPaneCards.getChildren().add(card);
            }
        }
    }

    private void mostrarCardEspecifica(String nombreEquipo) {
        if (flowPaneCards == null) return;

        flowPaneCards.getChildren().clear();

        for (Equipo equipo : equipos) {
            if (equipo.getNombre().equals(nombreEquipo)) {
                VBox card = crearCard(equipo);
                flowPaneCards.getChildren().add(card);
                break;
            }
        }
    }

    @FXML
    private void handleMostrarTodos() {
        if (cbEquipos != null) {
            cbEquipos.setValue("Todos los equipos");
        }
        if (txtBuscar != null) {
            txtBuscar.clear();
        }
        cargarCards();
    }

    @FXML
    private void handleActualizar() {
        cargarEquipos();
        cargarCards();
        AlertHelper.mostrarInformacion("Actualización", "Cards actualizadas correctamente");
    }
}