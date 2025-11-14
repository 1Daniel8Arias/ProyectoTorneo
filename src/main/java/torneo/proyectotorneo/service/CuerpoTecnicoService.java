package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.CuerpoTecnicoNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.CuerpoTecnico;
import torneo.proyectotorneo.repository.CuerpoTecnicoRepository;

import java.util.ArrayList;

public class CuerpoTecnicoService {

    private final CuerpoTecnicoRepository cuerpoTecnicoRepository;

    public CuerpoTecnicoService() {
        this.cuerpoTecnicoRepository = new CuerpoTecnicoRepository();
    }

    /**
     * Busca un miembro del cuerpo técnico por su ID
     */
    public CuerpoTecnico buscarCuerpoTecnicoPorId(int id) throws CuerpoTecnicoNoEncontradoException {
        try {
            CuerpoTecnico cuerpoTecnico = cuerpoTecnicoRepository.buscarPorId(id);
            if (cuerpoTecnico == null) {
                throw new CuerpoTecnicoNoEncontradoException("No se encontró el miembro del cuerpo técnico con ID: " + id);
            }
            return cuerpoTecnico;
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al buscar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Lista todo el cuerpo técnico
     */
    public ArrayList<CuerpoTecnico> listarTodoElCuerpoTecnico() throws CuerpoTecnicoNoEncontradoException {
        try {
            return cuerpoTecnicoRepository.listarTodos();
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al listar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Guarda un nuevo miembro del cuerpo técnico
     */
    public void guardarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws CuerpoTecnicoNoEncontradoException {
        try {
            if (cuerpoTecnico == null) {
                throw new CuerpoTecnicoNoEncontradoException("El cuerpo técnico no puede ser nulo");
            }
            if (cuerpoTecnico.getNombre() == null || cuerpoTecnico.getNombre().trim().isEmpty()) {
                throw new CuerpoTecnicoNoEncontradoException("El nombre es obligatorio");
            }
            if (cuerpoTecnico.getEspecialidad() == null || cuerpoTecnico.getEspecialidad().trim().isEmpty()) {
                throw new CuerpoTecnicoNoEncontradoException("La especialidad es obligatoria");
            }

            cuerpoTecnicoRepository.guardar(cuerpoTecnico);
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al guardar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Actualiza un miembro del cuerpo técnico
     */
    public void actualizarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws CuerpoTecnicoNoEncontradoException {
        try {
            if (cuerpoTecnico == null || cuerpoTecnico.getId() == null) {
                throw new CuerpoTecnicoNoEncontradoException("El cuerpo técnico y su ID no pueden ser nulos");
            }

            CuerpoTecnico existente = cuerpoTecnicoRepository.buscarPorId(cuerpoTecnico.getId());
            if (existente == null) {
                throw new CuerpoTecnicoNoEncontradoException("No se encontró el miembro del cuerpo técnico con ID: " + cuerpoTecnico.getId());
            }

            cuerpoTecnicoRepository.actualizar(cuerpoTecnico);
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al actualizar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Elimina un miembro del cuerpo técnico
     */
    public void eliminarCuerpoTecnico(int id) throws CuerpoTecnicoNoEncontradoException {
        try {
            CuerpoTecnico cuerpoTecnico = cuerpoTecnicoRepository.buscarPorId(id);
            if (cuerpoTecnico == null) {
                throw new CuerpoTecnicoNoEncontradoException("No se encontró el miembro del cuerpo técnico con ID: " + id);
            }
            cuerpoTecnicoRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al eliminar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Lista miembros del cuerpo técnico por equipo
     */
    public ArrayList<CuerpoTecnico> listarCuerpoTecnicoPorEquipo(int idEquipo) throws CuerpoTecnicoNoEncontradoException {
        try {
            ArrayList<CuerpoTecnico> todoElCuerpo = cuerpoTecnicoRepository.listarTodos();
            ArrayList<CuerpoTecnico> cuerpoDelEquipo = new ArrayList<>();

            for (CuerpoTecnico miembro : todoElCuerpo) {
                if (miembro.getEquipo() != null && miembro.getEquipo().getId().equals(idEquipo)) {
                    cuerpoDelEquipo.add(miembro);
                }
            }

            return cuerpoDelEquipo;
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al listar cuerpo técnico por equipo: " + e.getMessage());
        }
    }

    /**
     * Lista miembros del cuerpo técnico por especialidad
     */
    public ArrayList<CuerpoTecnico> listarCuerpoTecnicoPorEspecialidad(String especialidad) throws CuerpoTecnicoNoEncontradoException {
        try {
            if (especialidad == null || especialidad.trim().isEmpty()) {
                throw new CuerpoTecnicoNoEncontradoException("La especialidad no puede ser nula o vacía");
            }

            ArrayList<CuerpoTecnico> todoElCuerpo = cuerpoTecnicoRepository.listarTodos();
            ArrayList<CuerpoTecnico> cuerpoPorEspecialidad = new ArrayList<>();

            for (CuerpoTecnico miembro : todoElCuerpo) {
                if (miembro.getEspecialidad().equalsIgnoreCase(especialidad)) {
                    cuerpoPorEspecialidad.add(miembro);
                }
            }

            return cuerpoPorEspecialidad;
        } catch (RepositoryException e) {
            throw new CuerpoTecnicoNoEncontradoException("Error al listar cuerpo técnico por especialidad: " + e.getMessage());
        }
    }
}