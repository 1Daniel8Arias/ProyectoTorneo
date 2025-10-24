package torneo.proyectotorneo.exeptions;

public class JugadorNoEncontradoException extends RuntimeException {
    public JugadorNoEncontradoException(String message) {
        super(message);
    }
}
