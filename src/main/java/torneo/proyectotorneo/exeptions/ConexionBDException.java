package torneo.proyectotorneo.exeptions;

public class ConexionBDException extends RuntimeException {
    public ConexionBDException(String message) {
        super(message);
    }
}
