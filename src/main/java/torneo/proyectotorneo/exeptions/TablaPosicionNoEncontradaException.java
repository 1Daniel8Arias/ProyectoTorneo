package torneo.proyectotorneo.exeptions;

public class TablaPosicionNoEncontradaException extends RuntimeException {
    public TablaPosicionNoEncontradaException(String message) {
        super(message);
    }
}
