package torneo.proyectotorneo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "C##ARIAS";
    private static final String PASSWORD = "admin";
    private static Connection connection = null;

    private Conexion() {

    }

    public static Connection getInstance() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println(" Conexión establecida con la base de datos.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println(" No se encontró el driver de Oracle JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println(" Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    public static void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("️ Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
