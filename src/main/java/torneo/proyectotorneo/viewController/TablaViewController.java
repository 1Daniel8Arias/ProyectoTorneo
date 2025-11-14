package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import torneo.proyectotorneo.controller.TablaController;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.TablaPosicion;
import torneo.proyectotorneo.utils.AlertHelper;

import java.util.ArrayList;

/**
 * ViewController para la visualización de la Tabla de Posiciones
 */
public class TablaViewController {

    @FXML private TableView<TablaPosicion> tablaPosiciones;
    @FXML private TableColumn<TablaPosicion, Integer> colPosicion;
    @FXML private TableColumn<TablaPosicion, String> colEquipo;
    @FXML private TableColumn<TablaPosicion, Integer> colPuntos;
    @FXML private TableColumn<TablaPosicion, Integer> colGanados;
    @FXML private TableColumn<TablaPosicion, Integer> colEmpates;
    @FXML private TableColumn<TablaPosicion, Integer> colPerdidos;
    @FXML private TableColumn<TablaPosicion, Integer> colGolesFavor;
    @FXML private TableColumn<TablaPosicion, Integer> colGolesContra;
    @FXML private TableColumn<TablaPosicion, Integer> colDiferenciaGoles;

    @FXML private ComboBox<String> cbOrdenamiento;
    @FXML private TextField txtBuscarEquipo;
    @FXML private Button btnActualizar;
    @FXML private Button btnExportar;

    @FXML private Label lblTotalEquipos;
    @FXML private Label lblLider;
    @FXML private Label lblUltimo;

    private final TablaController tablaController;
    private final ObservableList<TablaPosicion> listaTablaPosiciones;

    public TablaViewController() {
        this.tablaController = new TablaController();
        this.listaTablaPosiciones = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBox();
        cargarTablaPosiciones();
        configurarEventos();
    }

    private void configurarTabla() {
        // Columna de posición (calculada según el orden)
        colPosicion.setCellValueFactory(cellData -> {
            int index = tablaPosiciones.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        colEquipo.setCellValueFactory(cellData -> {
            TablaPosicion tabla = cellData.getValue();
            if (tabla.getEquipo() != null) {
                return new javafx.beans.property.SimpleStringProperty(tabla.getEquipo().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("Sin equipo");
        });

        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntos"));
        colGanados.setCellValueFactory(new PropertyValueFactory<>("ganados"));
        colEmpates.setCellValueFactory(new PropertyValueFactory<>("empates"));
        colPerdidos.setCellValueFactory(new PropertyValueFactory<>("perdidos"));
        colGolesFavor.setCellValueFactory(new PropertyValueFactory<>("golesAFavor"));
        colGolesContra.setCellValueFactory(new PropertyValueFactory<>("golesEnContra"));
        colDiferenciaGoles.setCellValueFactory(new PropertyValueFactory<>("diferenciaGoles"));

        // Estilo para resaltar posiciones importantes
        tablaPosiciones.setRowFactory(tv -> new TableRow<TablaPosicion>() {
            @Override
            protected void updateItem(TablaPosicion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    int index = getIndex();
                    if (index < 4) {
                        // Zona de clasificación (verde claro)
                        setStyle("-fx-background-color: #c8e6c9;");
                    } else if (index >= tablaPosiciones.getItems().size() - 3) {
                        // Zona de descenso (rojo claro)
                        setStyle("-fx-background-color: #ffcdd2;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tablaPosiciones.setItems(listaTablaPosiciones);
    }

    private void configurarComboBox() {
        if (cbOrdenamiento != null) {
            cbOrdenamiento.setItems(FXCollections.observableArrayList(
                    "Por Puntos",
                    "Por Diferencia de Goles",
                    "Por Goles a Favor",
                    "Por Partidos Ganados"
            ));
            cbOrdenamiento.setValue("Por Puntos");
        }
    }

    private void configurarEventos() {
        if (txtBuscarEquipo != null) {
            txtBuscarEquipo.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarPorEquipo(newValue);
            });
        }

        if (cbOrdenamiento != null) {
            cbOrdenamiento.setOnAction(e -> aplicarOrdenamiento());
        }
    }

    private void cargarTablaPosiciones() {
        try {
            ArrayList<TablaPosicion> tabla = tablaController.listarTodos();
            listaTablaPosiciones.clear();
            listaTablaPosiciones.addAll(tabla);

            actualizarEstadisticas();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al cargar tabla de posiciones", e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        if (listaTablaPosiciones.isEmpty()) {
            return;
        }

        // Total de equipos
        if (lblTotalEquipos != null) {
            lblTotalEquipos.setText("Total de equipos: " + listaTablaPosiciones.size());
        }

        // Líder
        if (lblLider != null && !listaTablaPosiciones.isEmpty()) {
            TablaPosicion lider = listaTablaPosiciones.get(0);
            if (lider.getEquipo() != null) {
                lblLider.setText("Líder: " + lider.getEquipo().getNombre() +
                        " (" + lider.getPuntos() + " pts)");
            }
        }

        // Último
        if (lblUltimo != null && listaTablaPosiciones.size() > 0) {
            TablaPosicion ultimo = listaTablaPosiciones.get(listaTablaPosiciones.size() - 1);
            if (ultimo.getEquipo() != null) {
                lblUltimo.setText("Último: " + ultimo.getEquipo().getNombre() +
                        " (" + ultimo.getPuntos() + " pts)");
            }
        }
    }

    @FXML
    private void handleActualizar() {
        cargarTablaPosiciones();
        AlertHelper.mostrarInformacion("Actualización", "Tabla de posiciones actualizada correctamente");
    }

    @FXML
    private void handleExportar() {
        // Aquí puedes implementar la exportación a CSV, PDF, etc.
        AlertHelper.mostrarInformacion("Exportar", "Funcionalidad de exportación en desarrollo");
    }

    private void aplicarOrdenamiento() {
        if (cbOrdenamiento == null) return;

        String ordenSeleccionado = cbOrdenamiento.getValue();

        try {
            ArrayList<TablaPosicion> tablaOrdenada;

            switch (ordenSeleccionado) {
                case "Por Puntos":
                    tablaOrdenada = tablaController.ordenarPorPuntos();
                    break;
                case "Por Diferencia de Goles":
                    tablaOrdenada = tablaController.ordenarPorDiferenciaGoles();
                    break;
                case "Por Goles a Favor":
                    // Ordenar por goles a favor
                    tablaOrdenada = tablaController.listarTodos();
                    tablaOrdenada.sort((t1, t2) ->
                            Integer.compare(t2.getGolesAFavor(), t1.getGolesAFavor()));
                    break;
                case "Por Partidos Ganados":
                    // Ordenar por partidos ganados
                    tablaOrdenada = tablaController.listarTodos();
                    tablaOrdenada.sort((t1, t2) ->
                            Integer.compare(t2.getGanados(), t1.getGanados()));
                    break;
                default:
                    tablaOrdenada = tablaController.ordenarPorPuntos();
            }

            listaTablaPosiciones.clear();
            listaTablaPosiciones.addAll(tablaOrdenada);
            actualizarEstadisticas();

        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al ordenar", e.getMessage());
        }
    }

    private void filtrarPorEquipo(String termino) {
        if (termino == null || termino.isEmpty()) {
            cargarTablaPosiciones();
            return;
        }

        try {
            ArrayList<TablaPosicion> todasLasPosiciones = tablaController.listarTodos();
            ArrayList<TablaPosicion> posicionesFiltradas = new ArrayList<>();

            String terminoLower = termino.toLowerCase();

            for (TablaPosicion tabla : todasLasPosiciones) {
                if (tabla.getEquipo() != null &&
                        tabla.getEquipo().getNombre().toLowerCase().contains(terminoLower)) {
                    posicionesFiltradas.add(tabla);
                }
            }

            listaTablaPosiciones.clear();
            listaTablaPosiciones.addAll(posicionesFiltradas);
            actualizarEstadisticas();
        } catch (RepositoryException e) {
            AlertHelper.mostrarError("Error al filtrar", e.getMessage());
        }
    }

    /**
     * Obtiene información detallada de un equipo específico
     */
    @FXML
    private void handleVerDetalle() {
        TablaPosicion seleccionada = tablaPosiciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            AlertHelper.mostrarAdvertencia("Selección requerida",
                    "Debe seleccionar un equipo de la tabla");
            return;
        }

        if (seleccionada.getEquipo() == null) {
            return;
        }

        // Calcular estadísticas adicionales
        int partidosJugados = seleccionada.getGanados() +
                seleccionada.getEmpates() +
                seleccionada.getPerdidos();

        double porcentajeVictorias = partidosJugados > 0 ?
                (seleccionada.getGanados() * 100.0 / partidosJugados) : 0;

        double promedioGolesFavor = partidosJugados > 0 ?
                (seleccionada.getGolesAFavor() * 1.0 / partidosJugados) : 0;

        double promedioGolesContra = partidosJugados > 0 ?
                (seleccionada.getGolesEnContra() * 1.0 / partidosJugados) : 0;

        String mensaje = String.format(
                "Equipo: %s\n\n" +
                        "Partidos Jugados: %d\n" +
                        "Puntos: %d\n\n" +
                        "Victorias: %d (%.1f%%)\n" +
                        "Empates: %d\n" +
                        "Derrotas: %d\n\n" +
                        "Goles a Favor: %d (Promedio: %.2f)\n" +
                        "Goles en Contra: %d (Promedio: %.2f)\n" +
                        "Diferencia de Goles: %+d",
                seleccionada.getEquipo().getNombre(),
                partidosJugados,
                seleccionada.getPuntos(),
                seleccionada.getGanados(),
                porcentajeVictorias,
                seleccionada.getEmpates(),
                seleccionada.getPerdidos(),
                seleccionada.getGolesAFavor(),
                promedioGolesFavor,
                seleccionada.getGolesEnContra(),
                promedioGolesContra,
                seleccionada.getDiferenciaGoles()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del Equipo");
        alert.setHeaderText("Estadísticas Detalladas");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Calcula y muestra el rendimiento general del torneo
     */
    @FXML
    private void handleEstadisticasGenerales() {
        if (listaTablaPosiciones.isEmpty()) {
            AlertHelper.mostrarAdvertencia("Sin datos", "No hay datos en la tabla de posiciones");
            return;
        }

        int totalPartidos = 0;
        int totalGoles = 0;
        int equiposInvictos = 0;

        for (TablaPosicion tabla : listaTablaPosiciones) {
            int partidosJugados = tabla.getGanados() + tabla.getEmpates() + tabla.getPerdidos();
            totalPartidos += partidosJugados;
            totalGoles += tabla.getGolesAFavor();

            if (tabla.getPerdidos() == 0 && partidosJugados > 0) {
                equiposInvictos++;
            }
        }

        double promedioGolesPorPartido = totalPartidos > 0 ? (totalGoles * 2.0 / totalPartidos) : 0;

        String mensaje = String.format(
                "Estadísticas Generales del Torneo:\n\n" +
                        "Total de Partidos: %d\n" +
                        "Total de Goles: %d\n" +
                        "Promedio de Goles por Partido: %.2f\n" +
                        "Equipos Invictos: %d\n" +
                        "Equipos Participantes: %d",
                totalPartidos / 2, // Dividir por 2 porque cada partido cuenta para 2 equipos
                totalGoles,
                promedioGolesPorPartido,
                equiposInvictos,
                listaTablaPosiciones.size()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Estadísticas Generales");
        alert.setHeaderText("Resumen del Torneo");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}