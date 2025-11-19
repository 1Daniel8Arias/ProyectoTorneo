package torneo.proyectotorneo.viewController;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import torneo.proyectotorneo.controller.ArbitroController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Arbitro;
import torneo.proyectotorneo.model.Jugador;


import java.io.IOException;
import java.util.ArrayList;



public class ArbitroViewController {


    private ArbitroController arbitroController;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<Arbitro> tableArbitro;
    @FXML
    private TableColumn<Arbitro, Void> colAcciones;
    @FXML
    private TableColumn<Arbitro, String> colNombre;
    @FXML
    private TableColumn<Arbitro, Integer> colNumero;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnNuevoArbitro;

    private ObservableList<Arbitro> arbitrosObservable;


    @FXML
    public void initialize() {
        arbitroController = new ArbitroController();
        configurarTabla();
        cargarArbitros();
    }

    private void configurarTabla() {
        configurarColumnaAccion();
        colNumero.setCellValueFactory(cellData -> {
            Arbitro arbitro = cellData.getValue();
            int index = tableArbitro.getItems().indexOf(arbitro) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("NombreCompleto"));


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
                    Arbitro arbitro = getTableView().getItems().get(getIndex());

                });


                btnEditar.setOnAction(e -> {
                    Arbitro arbitro = getTableView().getItems().get(getIndex());

                });


                btnEliminar.setOnAction(e -> {
                    Arbitro arbitro = getTableView().getItems().get(getIndex());

                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }


    private void cargarArbitros() {
        try {
            ArrayList<Arbitro> arbitros = arbitroController.listarTodos();
            arbitrosObservable = FXCollections.observableArrayList(arbitros);
            tableArbitro.setItems(arbitrosObservable);
            actualizarContador();
        } catch (RepositoryException e) {
            mostrarError("Error", e.getMessage());
        }
    }

    private void actualizarContador() {
        lblContador.setText("Lista de Jugadores (" + tableArbitro.getItems().size() + ")");
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de información
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void handleBuscar(ActionEvent actionEvent) {
    }

    public void handleNuevoArbitro(ActionEvent actionEvent) {

        try {
            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/torneo/proyectotorneo/NuevoArbitro.fxml"));

            // Cargo la ventana
            Parent root = loader.load();

            // Cojo el controlador
            NuevoArbitroViewController controlador = loader.getController();
            controlador.initAttributtes(arbitrosObservable);

            // Creo el Scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            // cojo la persona devuelta
            Arbitro p = controlador.getArbitro();
            if (p != null) {

                // Añado la persona
                this.arbitrosObservable.add(p);

                // Refresco la tabla
                this.tableArbitro.refresh();
            }

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }


    }
}


