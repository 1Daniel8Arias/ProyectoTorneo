package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Tarjeta;
import torneo.proyectotorneo.repository.TarjetaRepository;

import java.util.ArrayList;

public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaService() {
        this.tarjetaRepository = new TarjetaRepository();
    }

    public ArrayList<Tarjeta> listarTodasLasTarjetas() throws RepositoryException {
        return tarjetaRepository.listarTodos();
    }

    public Tarjeta buscarTarjetaPorId(int id) throws RepositoryException {
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(id);
        if (tarjeta == null) {
            throw new RepositoryException("No se encontró la tarjeta con ID: " + id);
        }
        return tarjeta;
    }

    // ============================================================
    // CONSULTAS INTERMEDIAS
    // ============================================================

    /**
     * Consulta Intermedia 8: Lista tarjetas por partido
     */
    public ArrayList<Tarjeta> listarTarjetasPorPartido() throws RepositoryException {
        return tarjetaRepository.listarTarjetasPorPartido();
    }

    // ============================================================
    // MÉTODOS DE NEGOCIO
    // ============================================================

    public void registrarTarjeta(Tarjeta tarjeta) throws RepositoryException {
        if (tarjeta == null) {
            throw new RepositoryException("La tarjeta no puede ser nula");
        }

        if (tarjeta.getPartido() == null) {
            throw new RepositoryException("El partido es obligatorio");
        }

        if (tarjeta.getJugador() == null) {
            throw new RepositoryException("El jugador es obligatorio");
        }

        if (tarjeta.getTipo() == null) {
            throw new RepositoryException("El tipo de tarjeta es obligatorio");
        }

        tarjetaRepository.guardar(tarjeta);
    }

    public void actualizarTarjeta(Tarjeta tarjeta) throws RepositoryException {
        if (tarjeta == null || tarjeta.getIdTarjeta() == null) {
            throw new RepositoryException("El ID de la tarjeta es requerido para actualizar");
        }

        tarjetaRepository.actualizar(tarjeta);
    }

    public void eliminarTarjeta(int id) throws RepositoryException {
        tarjetaRepository.eliminar(id);
    }
}
