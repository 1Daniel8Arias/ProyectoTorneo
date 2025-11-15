package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.controller.TecnicoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.model.enums.TipoArbitro;
import torneo.proyectotorneo.utils.MensajeUtil;

import java.util.ArrayList;

public class TecnicoViewController {
private TecnicoController tecnicoController;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoTecnico;

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private TableColumn<Tecnico, Void> colAcciones;

    @FXML
    private TableColumn<Tecnico, String> colEquipo;

    @FXML
    private TableColumn<Tecnico, String> colNombre;

    @FXML
    private TableColumn<Tecnico, Integer> colNumero;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<Tecnico> tableCuerpoTecnico;

    @FXML
    private TextField txtBuscar;

    @FXML
    private MenuItem menuTecnicosSinEquipo;

    private ObservableList<Tecnico> tecnicosObservable;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoTecnico(ActionEvent event) {

    }

    @FXML
    void initialize() {
        tecnicoController = new TecnicoController();

         configurarTabla();
         cargarFiltros();
        cargarTecnicos();
    }

    private void cargarTecnicos() {

        try {
            ArrayList<Tecnico> tecnicos = tecnicoController.listarTodos();
            tecnicosObservable = FXCollections.observableArrayList(tecnicos);
            tableCuerpoTecnico.setItems(tecnicosObservable);
            actualizarContador();
        } catch (RepositoryException e) {
            MensajeUtil.mostrarAdvertencia("Error"+ e.getMessage());
        }
    }

    private void actualizarContador() {
        lblContador.setText("Lista de tecnicos (" + tableCuerpoTecnico.getItems().size() + ")");
    }

    private void configurarTabla() {
        configurarColumnaAccion();
        colNumero.setCellValueFactory(cellData -> {
            Tecnico tecnico = cellData.getValue();
            int index = tableCuerpoTecnico.getItems().indexOf(tecnico) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreCompleto"));
        colEquipo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreEquipo"));

        mostrarColumnas("numero", "nombre", "equipo");


    }

    @FXML
    void handleTecnicosSinEquipo(ActionEvent event) {
        ejecutarConsulta(()->tecnicoController.listarTecniSinContrato());
        mostrarColumnas("numero", "nombre");



    }

    private void ejecutarConsulta(ConsultaJugador consulta) {
        try {
            ArrayList<Tecnico> resultado = consulta.ejecutar();
            tecnicosObservable = FXCollections.observableArrayList(resultado);
            tableCuerpoTecnico.setItems(tecnicosObservable);
            actualizarContador();
        } catch (RepositoryException e) {
            MensajeUtil.mostrarError("Error"+ e.getMessage());
        }
    }

    // Interfaz funcional para consultas
    @FunctionalInterface
    private interface ConsultaJugador {
        ArrayList<Tecnico> ejecutar() throws RepositoryException;
    }



    private void cargarFiltros() {
        JugadorController jugadorController =new  JugadorController();
        cmbTipo.setItems(FXCollections.observableArrayList(jugadorController.obtenerNombresEquipos()));

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



            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void mostrarColumnas(String... columnasVisibles) {
        // Oculta todas las columnas primero
        colNumero.setVisible(false);
        colNombre.setVisible(false);
        colEquipo.setVisible(false);


        // Activa solo las que se pidan
        for (String nombre : columnasVisibles) {
            switch (nombre.toLowerCase()) {
                case "numero" -> colNumero.setVisible(true);
                case "nombre" -> colNombre.setVisible(true);
                case "equipo" -> colEquipo.setVisible(true);
                case "accion" -> colAcciones.setVisible(true);

            }
        }
    }

}
