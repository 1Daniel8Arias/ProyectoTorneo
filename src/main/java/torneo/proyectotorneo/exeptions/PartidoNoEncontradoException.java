package torneo.proyectotorneo.exeptions;

public class PartidoNoEncontradoException extends RuntimeException {
    public PartidoNoEncontradoException(String message) {
        super(message);
    }
}
