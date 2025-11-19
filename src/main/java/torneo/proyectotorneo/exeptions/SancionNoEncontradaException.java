package torneo.proyectotorneo.exeptions;

public class SancionNoEncontradaException extends RuntimeException {
    public SancionNoEncontradaException(String message) {
        super(message);
    }
}
