package torneo.proyectotorneo.viewController;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.enums.PosicionJugador;
import java.util.ArrayList;
import java.util.List;





public class JugadorViewController {

    private JugadorController jugadorController;

    @FXML
    private Button btnBuscar, btnNuevoJugador;
    @FXML
    private ComboBox<String> cmbEquipo;
    @FXML
    private ComboBox<PosicionJugador> cmbPosicion;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Jugador> tableJugadores;
    @FXML
    private TableColumn<Jugador, Integer> colNumero;
    @FXML
    private TableColumn<Jugador, String> colNombre;
    @FXML
    private TableColumn<Jugador, String> colEquipo;
    @FXML
    private TableColumn<Jugador, String> colPosicion;
    @FXML
    private TableColumn<Jugador, String> colNumCamiseta;
    @FXML
    private TableColumn<Jugador, String> colContrato;
    @FXML
    private TableColumn<Jugador, String> colSalario;

    @FXML
    private TableColumn<Jugador, Void> colAcciones;
    @FXML
    private Label lblContador;

    private ObservableList<Jugador> jugadoresObservable;

    @FXML
    void initialize() {
        jugadorController = new JugadorController();
        configurarTabla();
        cargarFiltros();
        cargarJugadores();
    }

    private void configurarTabla() {
     configurarColumnaAccion();
        colNumero.setCellValueFactory(cellData -> {
            Jugador jugador = cellData.getValue();
            int index = tableJugadores.getItems().indexOf(jugador) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreCompleto"));;
        colPosicion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("posicion"));
        colNumCamiseta.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("numeroCamiseta"));
        mostrarColumnas("numero", "nombre", "posicion","camiseta");


    }

    private void configurarColumnaAccion() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = new Button("Ver Detalle");
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(5, btnVer, btnEditar, btnEliminar);


            {
                contenedor.setStyle("-fx-alignment: center;");


                // Estilos para los botones
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");


                btnVer.setOnAction(e -> {
                    Jugador jugador = getTableView().getItems().get(getIndex());
                   // verDetallesJugador(jugador);
                });


                btnEditar.setOnAction(e -> {
                    Jugador jugador = getTableView().getItems().get(getIndex());
                   // editarJugador(jugador);
                });


                btnEliminar.setOnAction(e -> {
                    Jugador jugador = getTableView().getItems().get(getIndex());
                   // eliminarJugador(jugador);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }


    private void cargarFiltros() {
        cmbPosicion.setItems(FXCollections.observableArrayList(PosicionJugador.values()));
        cmbEquipo.setItems(FXCollections.observableArrayList(jugadorController.obtenerNombresEquipos()));
    }

    private void cargarJugadores() {
        try {
            ArrayList<Jugador> jugadores = jugadorController.listarTodos();
            jugadoresObservable = FXCollections.observableArrayList(jugadores);
            tableJugadores.setItems(jugadoresObservable);
            actualizarContador();
        } catch (RepositoryException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarContador() {
        lblContador.setText("Lista de Jugadores (" + tableJugadores.getItems().size() + ")");
    }

    // ──────────────── EVENTOS ────────────────

    @FXML
    void handleBuscar(ActionEvent event) {
        try {
            String equipoSeleccionado = cmbEquipo.getValue();
            PosicionJugador posicionSeleccionada = cmbPosicion.getValue();
            String texto = txtBuscar.getText().toLowerCase();

            ArrayList<Jugador> resultado = new ArrayList<>();

            // ✅ Caso 1: filtro por equipo y posición
            if (equipoSeleccionado != null && posicionSeleccionada != null) {
                int idEquipo = jugadorController.obtenerIdEquipoPorNombre(equipoSeleccionado);
                resultado = jugadorController.listarJugadoresPorEquipoYPosicion(idEquipo, posicionSeleccionada);
                mostrarColumnas("numero","nombre","accion","posicion","equipo");

                // ✅ Caso 2: solo por equipo
            } else if (equipoSeleccionado != null) {
                int idEquipo = jugadorController.obtenerIdEquipoPorNombre(equipoSeleccionado);
                resultado = jugadorController.listarJugadoresPorEquipo(idEquipo);
                mostrarColumnas("numero","nombre","accion");

                // ✅ Caso 3: solo por posición
            } else if (posicionSeleccionada != null) {
                resultado = jugadorController.listarJugadoresPorPosicion(posicionSeleccionada);
                mostrarColumnas("numero","nombre","accion","posicion","equipo");

                // ✅ Caso 4: texto de búsqueda
            } else if (!texto.isEmpty()) {
                List<Jugador> filtrados = tableJugadores.getItems().filtered(
                        j -> j.getNombreCompleto().toLowerCase().contains(texto)
                );
                resultado = new ArrayList<>(filtrados);
                mostrarColumnas("numero","nombre","accion","equipo");

                // ✅ Caso 5: sin filtros
            } else {
                cargarJugadores();
                return;
            }

            jugadoresObservable = FXCollections.observableArrayList(resultado);
            tableJugadores.setItems(jugadoresObservable);
            actualizarContador();

        } catch (RepositoryException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    void handleJugadoresConEquipo(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarJugadoresConEquipo());
        mostrarColumnas("numero", "nombre", "equipo", "posicion", "camiseta","accion");
    }

    @FXML
    void handleJugadoresConContrato(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarJugadoresConContratoYEquipo());
        mostrarColumnas("numero", "nombre", "equipo", "contrato");

    }

    @FXML
    void handleCapitanes(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarCapitanes());
        mostrarColumnas("numero", "nombre", "equipo", "posicion");

    }

    @FXML
    void handleSalarioSuperior(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarJugadoresConSalarioSuperiorPromedio());
        mostrarColumnas("numero", "nombre", "equipo","salario");
    }

    @FXML
    void handleGolesVariosPartidos(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarJugadoresConGolesEnMasDeUnPartido());
        mostrarColumnas("numero", "nombre", "equipo","posicion");
    }

    @FXML
    void handleSinContratoActivo(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarJugadoresSinContratoActivo());
        mostrarColumnas("numero", "nombre", "equipo");
    }

    @FXML
    void handleDelanterosSinGoles(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarDelanterosSinGoles());
        mostrarColumnas("numero", "nombre", "equipo","posicion");
    }

    @FXML
    void handleMayorSalarioPosicion(ActionEvent event) {
        ejecutarConsulta(() -> jugadorController.listarJugadoresConMayorSalarioPorPosicion());
        mostrarColumnas("numero", "nombre", "equipo","posicion","salario");
    }

    @FXML
    void handleMostrarTodos(ActionEvent event) {
        cargarJugadores();
    }

    @FXML
    void handleNuevoJugador(ActionEvent event) {
        mostrarAlerta("Acción pendiente", "Aquí puedes abrir el formulario para crear un nuevo jugador.", Alert.AlertType.INFORMATION);
    }

    // ──────────────── MÉTODOS AUXILIARES ────────────────

    private void ejecutarConsulta(ConsultaJugador consulta) {
        try {
            ArrayList<Jugador> resultado = consulta.ejecutar();
            jugadoresObservable = FXCollections.observableArrayList(resultado);
            tableJugadores.setItems(jugadoresObservable);
            actualizarContador();
        } catch (RepositoryException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Interfaz funcional para consultas
    @FunctionalInterface
    private interface ConsultaJugador {
        ArrayList<Jugador> ejecutar() throws RepositoryException;
    }

    private void mostrarColumnas(String... columnasVisibles) {
        // Oculta todas las columnas primero
        colNumero.setVisible(false);
        colNombre.setVisible(false);
        colEquipo.setVisible(false);
        colPosicion.setVisible(false);
        colNumCamiseta.setVisible(false);
        colContrato.setVisible(false);
        colSalario.setVisible(false);

        // Activa solo las que se pidan
        for (String nombre : columnasVisibles) {
            switch (nombre.toLowerCase()) {
                case "numero" -> colNumero.setVisible(true);
                case "nombre" -> colNombre.setVisible(true);
                case "equipo" -> colEquipo.setVisible(true);
                case "posicion" -> colPosicion.setVisible(true);
                case "camiseta" -> colNumCamiseta.setVisible(true);
                case "contrato" -> colContrato.setVisible(true);
                case "accion" -> colAcciones.setVisible(true);
                case "salario" -> colSalario.setVisible(true);
            }
        }
    }

}
