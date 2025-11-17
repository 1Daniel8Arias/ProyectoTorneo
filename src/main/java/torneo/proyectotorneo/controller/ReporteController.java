package torneo.proyectotorneo.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.utils.Conexion;  // ⭐ CAMBIO AQUÍ

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReporteController {

    private static ReporteController instance;

    // Rutas de los reportes
    private static final String RUTA_REPORTES_JRXML = "/torneo/proyectotorneo/reportes/jrxml/";
    private static final String RUTA_REPORTES_JASPER = "src/main/resources/torneo/proyectotorneo/reportes/jasper/";

    private ReporteController() {
        // Constructor privado para Singleton
    }

    public static ReporteController getInstance() {
        if (instance == null) {
            instance = new ReporteController();
        }
        return instance;
    }

    /**
     * Compila un archivo .jrxml a .jasper
     */
    public void compilarReporte(String nombreReporte) throws RepositoryException {
        try {
            // Leer el archivo .jrxml
            InputStream inputStream = getClass().getResourceAsStream(
                    RUTA_REPORTES_JRXML + nombreReporte + ".jrxml"
            );

            if (inputStream == null) {
                throw new RepositoryException("No se encontró el archivo: " + nombreReporte + ".jrxml");
            }

            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            // Crear directorio si no existe
            File directorioJasper = new File(RUTA_REPORTES_JASPER);
            if (!directorioJasper.exists()) {
                directorioJasper.mkdirs();
            }

            // Guardar el archivo compilado .jasper
            String rutaSalida = RUTA_REPORTES_JASPER + nombreReporte + ".jasper";
            JRSaver.saveObject(jasperReport, rutaSalida);

            System.out.println("✅ Reporte compilado: " + nombreReporte);

        } catch (Exception e) {
            throw new RepositoryException("Error al compilar el reporte: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte y lo visualiza
     */
    public JasperPrint generarReporte(String nombreReporte, Map<String, Object> parametros)
            throws RepositoryException {
        try {
            // Compilar si no existe el .jasper
            File archivoJasper = new File(RUTA_REPORTES_JASPER + nombreReporte + ".jasper");
            if (!archivoJasper.exists()) {
                compilarReporte(nombreReporte);
            }

            // Cargar el reporte compilado
            InputStream jasperStream = new FileInputStream(archivoJasper);

            // ⭐ CAMBIO AQUÍ: Obtener conexión a la BD
            Connection conexion = Conexion.getInstance();

            // Si no hay parámetros, crear un mapa vacío
            if (parametros == null) {
                parametros = new HashMap<>();
            }

            // Llenar el reporte con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperStream,
                    parametros,
                    conexion
            );

            System.out.println("✅ Reporte generado: " + nombreReporte);

            return jasperPrint;

        } catch (SQLException e) {
            throw new RepositoryException("Error de conexión a la base de datos: " + e.getMessage());
        } catch (Exception e) {
            throw new RepositoryException("Error al generar el reporte: " + e.getMessage());
        }
    }

    /**
     * Exporta un reporte a PDF
     */
    public void exportarAPDF(JasperPrint jasperPrint, String rutaDestino)
            throws RepositoryException {
        try {
            // Configurar el exportador
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(rutaDestino));

            // Exportar
            exporter.exportReport();

            System.out.println("✅ PDF generado en: " + rutaDestino);

        } catch (Exception e) {
            throw new RepositoryException("Error al exportar a PDF: " + e.getMessage());
        }
    }

    /**
     * Lista todos los reportes disponibles
     */
    public String[] obtenerListaReportes() {
        return new String[]{
                "01_ListadoJugadores",
                "02_ListadoEquipos",
                "03_ListadoArbitros",
                "04_FixturePartidos",
                "05_ContratosVigentes",
                "06_CuerpoTecnicoPorEquipo",
                "07_EstadiosPorDepartamento",
                "08_TablaPosiciones",
                "09_GoleadoresTorneo"
        };
    }

    /**
     * Obtiene el nombre legible de un reporte
     */
    public String obtenerNombreLegible(String nombreReporte) {
        Map<String, String> nombres = new HashMap<>();
        nombres.put("01_ListadoJugadores", "Listado de Jugadores");
        nombres.put("02_ListadoEquipos", "Listado de Equipos");
        nombres.put("03_ListadoArbitros", "Listado de Árbitros");
        nombres.put("04_FixturePartidos", "Fixture de Partidos por Jornada");
        nombres.put("05_ContratosVigentes", "Contratos Vigentes de Jugadores");
        nombres.put("06_CuerpoTecnicoPorEquipo", "Cuerpo Técnico por Equipo");
        nombres.put("07_EstadiosPorDepartamento", "Estadios por Departamento");
        nombres.put("08_TablaPosiciones", "Tabla de Posiciones");
        nombres.put("09_GoleadoresTorneo", "Goleadores del Torneo");


        return nombres.getOrDefault(nombreReporte, nombreReporte);
    }
}