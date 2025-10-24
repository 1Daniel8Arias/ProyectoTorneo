package torneo.proyectotorneo.exeptions;

public class CampeonatoException extends Exception {
    public CampeonatoException(String message) {
        super(message);
    }

    public CampeonatoException(String message, Throwable cause) {
        super(message, cause);
    }
}
