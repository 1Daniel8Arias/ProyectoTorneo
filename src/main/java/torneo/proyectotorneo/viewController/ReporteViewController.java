package torneo.proyectotorneo.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import torneo.proyectotorneo.controller.ReporteController;
import torneo.proyectotorneo.exeptions.RepositoryException;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ReporteViewController implements Initializable {

    @FXML
    private ListView<String> listViewReportes;

    @FXML
    private Button btnGenerarReporte;

    @FXML
    private Button btnExportarPDF;

    @FXML
    private Label lblTituloReporte;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblTotalReportes;

    @FXML
    private ScrollPane scrollPaneReporte;

    @FXML
    private StackPane contenedorReporte;

    private ReporteController reporteController;
    private JasperPrint jasperPrintActual;
    private String reporteSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reporteController = ReporteController.getInstance();
        cargarListaReportes();
        configurarListeners();
    }

    /**
     * Carga la lista de reportes disponibles
     */
    private void cargarListaReportes() {
        String[] reportes = reporteController.obtenerListaReportes();
        ObservableList<String> items = FXCollections.observableArrayList();

        for (String reporte : reportes) {
            String nombreLegible = reporteController.obtenerNombreLegible(reporte);
            items.add(nombreLegible);
        }

        listViewReportes.setItems(items);
        lblTotalReportes.setText("Total: " + reportes.length + " reportes");
    }

    /**
     * Configura los listeners de selección
     */
    private void configurarListeners() {
        listViewReportes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        actualizarTituloReporte(newValue);
                        btnGenerarReporte.setDisable(false);
                    }
                }
        );
    }

    /**
     * Actualiza el título del reporte seleccionado
     */
    private void actualizarTituloReporte(String nombreLegible) {
        lblTituloReporte.setText(nombreLegible);
        reporteSeleccionado = obtenerNombreArchivo(nombreLegible);
    }

    /**
     * Obtiene el nombre del archivo a partir del nombre legible
     */
    private String obtenerNombreArchivo(String nombreLegible) {
        String[] reportes = reporteController.obtenerListaReportes();
        for (String reporte : reportes) {
            if (reporteController.obtenerNombreLegible(reporte).equals(nombreLegible)) {
                return reporte;
            }
        }
        return null;
    }

    /**
     * Maneja la generación del reporte
     */
    @FXML
    void handleGenerarReporte(ActionEvent event) {
        if (reporteSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un reporte", Alert.AlertType.WARNING);
            return;
        }

        try {
            lblEstado.setText("⏳ Generando reporte...");
            lblEstado.setStyle("-fx-text-fill: #f39c12;");

            // Generar el reporte (sin parámetros adicionales)
            Map<String, Object> parametros = new HashMap<>();
            jasperPrintActual = reporteController.generarReporte(reporteSeleccionado, parametros);

            // Mostrar el reporte en el visor
            mostrarReporteEnVisor(jasperPrintActual);

            lblEstado.setText("✅ Reporte generado exitosamente");
            lblEstado.setStyle("-fx-text-fill: #27ae60;");
            btnExportarPDF.setDisable(false);

        } catch (RepositoryException e) {
            lblEstado.setText("❌ Error al generar reporte");
            lblEstado.setStyle("-fx-text-fill: #e74c3c;");
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra el reporte en el visor Swing
     */
    private void mostrarReporteEnVisor(JasperPrint jasperPrint) {
        // Limpiar el contenedor
        contenedorReporte.getChildren().clear();

        // Crear el visor de Jasper (componente Swing)
        JRViewer viewer = new JRViewer(jasperPrint);

        // Envolver el visor Swing en un SwingNode para JavaFX
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> swingNode.setContent(viewer));

        // Agregar al contenedor
        contenedorReporte.getChildren().add(swingNode);
    }

    /**
     * Maneja la exportación a PDF
     */
    @FXML
    void handleExportarPDF(ActionEvent event) {
        if (jasperPrintActual == null) {
            mostrarAlerta("Advertencia", "Primero debe generar un reporte", Alert.AlertType.WARNING);
            return;
        }

        // Mostrar diálogo para seleccionar ubicación
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte PDF");
        fileChooser.setInitialFileName(reporteSeleccionado + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File archivo = fileChooser.showSaveDialog(btnExportarPDF.getScene().getWindow());

        if (archivo != null) {
            try {
                lblEstado.setText("⏳ Exportando a PDF...");
                lblEstado.setStyle("-fx-text-fill: #f39c12;");

                reporteController.exportarAPDF(jasperPrintActual, archivo.getAbsolutePath());

                lblEstado.setText("✅ PDF exportado exitosamente");
                lblEstado.setStyle("-fx-text-fill: #27ae60;");

                mostrarAlerta("Éxito", "PDF generado en:\n" + archivo.getAbsolutePath(),
                        Alert.AlertType.INFORMATION);

            } catch (RepositoryException e) {
                lblEstado.setText("❌ Error al exportar PDF");
                lblEstado.setStyle("-fx-text-fill: #e74c3c;");
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Muestra un diálogo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}