package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.exeptions.SancionNoEncontradaException;
import torneo.proyectotorneo.model.Jugador;
import torneo.proyectotorneo.model.Sancion;
import torneo.proyectotorneo.repository.JugadorRepository;
import torneo.proyectotorneo.repository.SancionRepository;

import java.time.LocalDate;
import java.util.ArrayList;

public class SancionService {

    private final SancionRepository sancionRepository;
    private final JugadorRepository jugadorRepository;

    public SancionService() {
        this.sancionRepository = new SancionRepository();
        this.jugadorRepository = new JugadorRepository();
    }

    /**
     * Busca una sanción por su ID
     */
    public Sancion buscarSancionPorId(int id) throws SancionNoEncontradaException {
        try {
            Sancion sancion = sancionRepository.buscarPorId(id);
            if (sancion == null) {
                throw new SancionNoEncontradaException("No se encontró la sanción con ID: " + id);
            }
            return sancion;
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al buscar la sanción: " + e.getMessage());
        }
    }

    /**
     * Lista todas las sanciones
     */
    public ArrayList<Sancion> listarTodasLasSanciones() throws SancionNoEncontradaException {
        try {
            return sancionRepository.listarTodos();
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al listar las sanciones: " + e.getMessage());
        }
    }

    /**
     * Guarda una nueva sanción
     */
    public void guardarSancion(Sancion sancion) throws SancionNoEncontradaException {
        try {
            if (sancion == null) {
                throw new SancionNoEncontradaException("La sanción no puede ser nula");
            }
            if (sancion.getJugador() == null) {
                throw new SancionNoEncontradaException("El jugador de la sanción no puede ser nulo");
            }
            if (sancion.getTipo() == null || sancion.getTipo().trim().isEmpty()) {
                throw new SancionNoEncontradaException("El tipo de sanción es obligatorio");
            }
            if (sancion.getFecha() == null) {
                throw new SancionNoEncontradaException("La fecha de inicio de la sanción es obligatoria");
            }

            sancionRepository.guardar(sancion);
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al guardar la sanción: " + e.getMessage());
        }
    }

    /**
     * Actualiza una sanción existente
     */
    public void actualizarSancion(Sancion sancion) throws SancionNoEncontradaException {
        try {
            if (sancion == null || sancion.getIdSancion() == null) {
                throw new SancionNoEncontradaException("La sanción y su ID no pueden ser nulos");
            }

            // Verificar que la sanción existe
            Sancion sancionExistente = sancionRepository.buscarPorId(sancion.getIdSancion());
            if (sancionExistente == null) {
                throw new SancionNoEncontradaException("No se encontró la sanción con ID: " + sancion.getIdSancion());
            }

            sancionRepository.actualizar(sancion);
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al actualizar la sanción: " + e.getMessage());
        }
    }

    /**
     * Elimina una sanción
     */
    public void eliminarSancion(int id) throws SancionNoEncontradaException {
        try {
            Sancion sancion = sancionRepository.buscarPorId(id);
            if (sancion == null) {
                throw new SancionNoEncontradaException("No se encontró la sanción con ID: " + id);
            }
            sancionRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al eliminar la sanción: " + e.getMessage());
        }
    }

    /**
     * Lista sanciones de un jugador específico
     */
    public ArrayList<Sancion> listarSancionesPorJugador(int idJugador) throws SancionNoEncontradaException {
        try {
            ArrayList<Sancion> todasLasSanciones = sancionRepository.listarTodos();
            ArrayList<Sancion> sancionesDelJugador = new ArrayList<>();

            for (Sancion sancion : todasLasSanciones) {
                if (sancion.getJugador() != null && sancion.getJugador().getId().equals(idJugador)) {
                    sancionesDelJugador.add(sancion);
                }
            }

            return sancionesDelJugador;
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al listar sanciones por jugador: " + e.getMessage());
        }
    }

    /**
     * Lista sanciones por tipo (Amarilla, Roja, Suspensión, etc.)
     */
    public ArrayList<Sancion> listarSancionesPorTipo(String tipo) throws SancionNoEncontradaException {
        try {
            if (tipo == null || tipo.trim().isEmpty()) {
                throw new SancionNoEncontradaException("El tipo de sanción no puede ser nulo o vacío");
            }

            ArrayList<Sancion> todasLasSanciones = sancionRepository.listarTodos();
            ArrayList<Sancion> sancionesPorTipo = new ArrayList<>();

            for (Sancion sancion : todasLasSanciones) {
                if (sancion.getTipo().equalsIgnoreCase(tipo)) {
                    sancionesPorTipo.add(sancion);
                }
            }

            return sancionesPorTipo;
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al listar sanciones por tipo: " + e.getMessage());
        }
    }

    /**
     * Lista sanciones activas (que no han expirado)
     */
    public ArrayList<Sancion> listarSancionesActivas() throws SancionNoEncontradaException {
        try {
            ArrayList<Sancion> todasLasSanciones = sancionRepository.listarTodos();
            ArrayList<Sancion> sancionesActivas = new ArrayList<>();
            LocalDate hoy = LocalDate.now();

            for (Sancion sancion : todasLasSanciones) {
                // Una sanción está activa si:
                // 1. La fecha de inicio ya pasó
                // 2. No tiene fecha de fin O la fecha de fin aún no ha llegado
                boolean haComenzado = sancion.getFecha() != null &&
                        (sancion.getFecha().isBefore(hoy) || sancion.getFecha().isEqual(hoy));



            }

            return sancionesActivas;
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al listar sanciones activas: " + e.getMessage());
        }
    }

    /**
     * Lista sanciones en un rango de fechas
     */
    public ArrayList<Sancion> listarSancionesPorFecha(LocalDate fechaInicio, LocalDate fechaFin)
            throws SancionNoEncontradaException {
        try {
            if (fechaInicio == null || fechaFin == null) {
                throw new SancionNoEncontradaException("Las fechas no pueden ser nulas");
            }
            if (fechaInicio.isAfter(fechaFin)) {
                throw new SancionNoEncontradaException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }

            ArrayList<Sancion> todasLasSanciones = sancionRepository.listarTodos();
            ArrayList<Sancion> sancionesPorFecha = new ArrayList<>();

            for (Sancion sancion : todasLasSanciones) {
                if (sancion.getFecha() != null) {
                    boolean estaEnRango = (sancion.getFecha().isEqual(fechaInicio) ||
                            sancion.getFecha().isAfter(fechaInicio)) &&
                            (sancion.getFecha().isEqual(fechaFin) ||
                                    sancion.getFecha().isBefore(fechaFin));

                    if (estaEnRango) {
                        sancionesPorFecha.add(sancion);
                    }
                }
            }

            return sancionesPorFecha;
        } catch (RepositoryException e) {
            throw new SancionNoEncontradaException("Error al listar sanciones por fecha: " + e.getMessage());
        }
    }

    /**
     * Verifica si un jugador tiene sanciones activas
     */
    public boolean jugadorTieneSancionActiva(int idJugador) throws SancionNoEncontradaException {
        ArrayList<Sancion> sancionesDelJugador = listarSancionesPorJugador(idJugador);
        ArrayList<Sancion> sancionesActivas = listarSancionesActivas();

        for (Sancion sancion : sancionesDelJugador) {
            for (Sancion activa : sancionesActivas) {
                if (sancion.getIdSancion().equals(activa.getIdSancion())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Cuenta el número total de sanciones de un jugador
     */
    public int contarSancionesDeJugador(int idJugador) throws SancionNoEncontradaException {
        ArrayList<Sancion> sanciones = listarSancionesPorJugador(idJugador);
        return sanciones.size();
    }
}