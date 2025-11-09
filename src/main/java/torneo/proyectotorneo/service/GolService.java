package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.Gol;
import torneo.proyectotorneo.repository.GolRepository;

import java.util.ArrayList;

public class GolService {
    private final GolRepository golRepository;

    public GolService() {
        this.golRepository = new GolRepository();
    }

    public ArrayList<Gol> listarTodosLosGoles() throws RepositoryException {
        return golRepository.listarTodos();
    }

    public Gol buscarGolPorId(int id) throws RepositoryException {
        Gol gol = golRepository.buscarPorId(id);
        if (gol == null) {
            throw new RepositoryException("No se encontró el gol con ID: " + id);
        }
        return gol;
    }

    // ============================================================
    // CONSULTAS INTERMEDIAS
    // ============================================================

    /**
     * Consulta Intermedia 6: Lista goles con jugador y partido
     */
    public ArrayList<Gol> listarGolesConJugadorYPartido() throws RepositoryException {
        return golRepository.listarGolesConJugadorYPartido();
    }

    // ============================================================
    // MÉTODOS DE NEGOCIO
    // ============================================================

    public void registrarGol(Gol gol) throws RepositoryException {
        if (gol == null) {
            throw new RepositoryException("El gol no puede ser nulo");
        }

        if (gol.getPartido() == null) {
            throw new RepositoryException("El partido es obligatorio");
        }

        if (gol.getJugador() == null) {
            throw new RepositoryException("El jugador es obligatorio");
        }

        if (gol.getNumeroGoles() <= 0) {
            throw new RepositoryException("El número de goles debe ser mayor a cero");
        }

        golRepository.guardar(gol);
    }

    public void actualizarGol(Gol gol) throws RepositoryException {
        if (gol == null || gol.getIdGol() == null) {
            throw new RepositoryException("El ID del gol es requerido para actualizar");
        }

        golRepository.actualizar(gol);
    }

    public void eliminarGol(int id) throws RepositoryException {
        golRepository.eliminar(id);
    }
}

