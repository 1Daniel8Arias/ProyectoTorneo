package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import torneo.proyectotorneo.controller.CuerpoTecnicoController;
import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.controller.TecnicoController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.CuerpoTecnico;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.utils.MensajeUtil;

import java.util.ArrayList;

public class CuerpoTecnicoViewController {
    private ObservableList<CuerpoTecnico> cuerpoTecnicosObservable;
    CuerpoTecnicoController cuerpoTecnicoController;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnNuevoCuerpoTecnico;

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private TableColumn<CuerpoTecnico, Void> colAcciones;

    @FXML
    private TableColumn<CuerpoTecnico, String> colEquipo;

    @FXML
    private TableColumn<CuerpoTecnico, String> colEspecialidad;

    @FXML
    private TableColumn<CuerpoTecnico, String> colNombre;

    @FXML
    private TableColumn<CuerpoTecnico, Integer> colNumero;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<CuerpoTecnico> tableCuerpoTecnico;

    @FXML
    private TextField txtBuscar;

    @FXML
    void handleBuscar(ActionEvent event) {

    }

    @FXML
    void handleNuevoCuerpoTecnico(ActionEvent event) {

    }

    @FXML
    void initialize() {
        cuerpoTecnicoController = new CuerpoTecnicoController();

        configurarTabla();
        cargarFiltros();
        cargarTecnicos();
    }

    private void configurarTabla() {
        configurarColumnaAccion();
        colNumero.setCellValueFactory(cellData -> {
            CuerpoTecnico cuerpoTecnico = cellData.getValue();
            int index = tableCuerpoTecnico.getItems().indexOf(cuerpoTecnico) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreCompleto"));
        colEquipo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreEquipo"));
        colEspecialidad.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("especialidad"));

        mostrarColumnas("numero", "nombre", "equipo");


    }

    private void cargarTecnicos() {

        try {
            ArrayList<CuerpoTecnico> tecnicos = cuerpoTecnicoController.listarTodos();
            cuerpoTecnicosObservable = FXCollections.observableArrayList(tecnicos);
            tableCuerpoTecnico.setItems(cuerpoTecnicosObservable);
            actualizarContador();
        } catch (RepositoryException e) {
            MensajeUtil.mostrarAdvertencia("Error"+ e.getMessage());
        }
    }
    private void actualizarContador() {
        lblContador.setText("Lista de cuerpos tecnicos (" + tableCuerpoTecnico.getItems().size() + ")");
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

    private void cargarFiltros() {
        JugadorController jugadorController =new  JugadorController();
        cmbTipo.setItems(FXCollections.observableArrayList(jugadorController.obtenerNombresEquipos()));

    }

}
