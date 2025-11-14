package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.exeptions.TecnicoNoEncontradoException;
import torneo.proyectotorneo.model.CuerpoTecnico;
import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.model.Tecnico;
import torneo.proyectotorneo.repository.CuerpoTecnicoRepository;
import torneo.proyectotorneo.repository.EquipoRepository;
import torneo.proyectotorneo.repository.TecnicoRepository;

import java.util.ArrayList;

public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final CuerpoTecnicoRepository cuerpoTecnicoRepository;
    private final EquipoRepository equipoRepository;

    public TecnicoService() {
        this.tecnicoRepository = new TecnicoRepository();
        this.cuerpoTecnicoRepository = new CuerpoTecnicoRepository();
        this.equipoRepository = new EquipoRepository();
    }

    /**
     * Lista todos los técnicos registrados
     */
    public ArrayList<Tecnico> listarTodosLosTecnicos() throws RepositoryException {
        return tecnicoRepository.listarTodos();
    }

    /**
     * Busca un técnico por su ID
     */
    public Tecnico buscarTecnicoPorId(int id) throws TecnicoNoEncontradoException {
        try {
            Tecnico tecnico = tecnicoRepository.buscarPorId(id);
            if (tecnico == null) {
                throw new TecnicoNoEncontradoException("No se encontró el técnico con ID: " + id);
            }
            return tecnico;
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al buscar el técnico: " + e.getMessage());
        }
    }

    /**
     * Busca el técnico de un equipo específico
     */
    public Tecnico buscarTecnicoPorEquipo(int idEquipo) throws TecnicoNoEncontradoException {
        try {
            Tecnico tecnico = tecnicoRepository.buscarPorEquipo(idEquipo);
            if (tecnico == null) {
                throw new TecnicoNoEncontradoException("El equipo no tiene técnico asignado");
            }
            return tecnico;
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al buscar el técnico: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo técnico
     * Validación: Un equipo solo puede tener un técnico activo
     */
    public void registrarTecnico(Tecnico tecnico) throws TecnicoNoEncontradoException {
        validarTecnico(tecnico);

        try {
            // Verificar que el equipo no tenga ya un técnico
            if (tecnicoRepository.equipoTieneTecnico(tecnico.getEquipo().getId())) {
                throw new TecnicoNoEncontradoException(
                        "El equipo ya tiene un director técnico activo. Solo puede tener uno a la vez."
                );
            }

            tecnicoRepository.guardar(tecnico);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al registrar el técnico: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un técnico
     */
    public void actualizarTecnico(Tecnico tecnico) throws TecnicoNoEncontradoException {
        validarTecnico(tecnico);

        if (tecnico.getId() == null) {
            throw new TecnicoNoEncontradoException("El ID del técnico es requerido para actualizar");
        }

        try {
            tecnicoRepository.actualizar(tecnico);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al actualizar el técnico: " + e.getMessage());
        }
    }

    /**
     * Cambia el técnico de un equipo
     * Un equipo puede cambiar de técnico durante la temporada
     */
    public void cambiarTecnico(int idEquipo, Tecnico nuevoTecnico) throws TecnicoNoEncontradoException {
        try {
            // Verificar que el equipo existe
            Equipo equipo = equipoRepository.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new TecnicoNoEncontradoException("El equipo no existe");
            }

            // Buscar si ya tiene un técnico
            Tecnico tecnicoActual = tecnicoRepository.buscarPorEquipo(idEquipo);

            if (tecnicoActual != null) {
                // Eliminar el técnico actual
                tecnicoRepository.eliminar(tecnicoActual.getId());
            }

            // Asignar el nuevo técnico
            nuevoTecnico.setEquipo(equipo);
            validarTecnico(nuevoTecnico);
            tecnicoRepository.guardar(nuevoTecnico);

        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al cambiar el técnico: " + e.getMessage());
        }
    }

    /**
     * Elimina un técnico del sistema
     */
    public void eliminarTecnico(int id) throws TecnicoNoEncontradoException {
        try {
            Tecnico tecnico = tecnicoRepository.buscarPorId(id);
            if (tecnico == null) {
                throw new TecnicoNoEncontradoException("No se encontró el técnico con ID: " + id);
            }

            tecnicoRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al eliminar el técnico: " + e.getMessage());
        }
    }

    /**
     * Registra un miembro del cuerpo técnico de apoyo
     */
    public void registrarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws TecnicoNoEncontradoException {
        validarCuerpoTecnico(cuerpoTecnico);

        try {
            cuerpoTecnicoRepository.guardar(cuerpoTecnico);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al registrar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Actualiza un miembro del cuerpo técnico
     */
    public void actualizarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws TecnicoNoEncontradoException {
        validarCuerpoTecnico(cuerpoTecnico);

        if (cuerpoTecnico.getId() == null) {
            throw new TecnicoNoEncontradoException("El ID del cuerpo técnico es requerido para actualizar");
        }

        try {
            cuerpoTecnicoRepository.actualizar(cuerpoTecnico);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al actualizar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Elimina un miembro del cuerpo técnico
     */
    public void eliminarCuerpoTecnico(int id) throws TecnicoNoEncontradoException {
        try {
            CuerpoTecnico cuerpoTecnico = cuerpoTecnicoRepository.buscarPorId(id);
            if (cuerpoTecnico == null) {
                throw new TecnicoNoEncontradoException("No se encontró el miembro del cuerpo técnico");
            }

            cuerpoTecnicoRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al eliminar el cuerpo técnico: " + e.getMessage());
        }
    }

    /**
     * Obtiene información completa del staff técnico de un equipo
     */
    public String obtenerStaffCompletoEquipo(int idEquipo) throws TecnicoNoEncontradoException {
        try {
            Equipo equipo = equipoRepository.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new TecnicoNoEncontradoException("El equipo no existe");
            }

            StringBuilder info = new StringBuilder();
            info.append("==== STAFF TÉCNICO ====\n");
            info.append("Equipo: ").append(equipo.getNombre()).append("\n\n");

            // Director Técnico
            Tecnico tecnico = tecnicoRepository.buscarPorEquipo(idEquipo);
            if (tecnico != null) {
                info.append("Director Técnico: ")
                        .append(tecnico.getNombre())
                        .append(" ")
                        .append(tecnico.getApellido())
                        .append("\n\n");
            } else {
                info.append("Director Técnico: No asignado\n\n");
            }

            // Cuerpo Técnico
            ArrayList<CuerpoTecnico> cuerpoTecnico = equipo.getListaCuerpoTecnicos();
            if (cuerpoTecnico != null && !cuerpoTecnico.isEmpty()) {
                info.append("Cuerpo Técnico de Apoyo:\n");
                for (CuerpoTecnico ct : cuerpoTecnico) {
                    info.append("- ")
                            .append(ct.getNombre())
                            .append(" ")
                            .append(ct.getApellido())
                            .append(" (")
                            .append(ct.getEspecialidad())
                            .append(")\n");
                }
            } else {
                info.append("Cuerpo Técnico: No registrado\n");
            }

            return info.toString();

        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al obtener el staff: " + e.getMessage());
        }
    }

    /**
     * Verifica si un equipo tiene técnico asignado
     */
    public boolean equipoTieneTecnico(int idEquipo) throws RepositoryException {
        return tecnicoRepository.equipoTieneTecnico(idEquipo);
    }

    /**
     * Validaciones para un técnico
     */
    private void validarTecnico(Tecnico tecnico) throws TecnicoNoEncontradoException {
        if (tecnico == null) {
            throw new TecnicoNoEncontradoException("El técnico no puede ser nulo");
        }

        if (tecnico.getNombre() == null || tecnico.getNombre().trim().isEmpty()) {
            throw new TecnicoNoEncontradoException("El nombre del técnico es obligatorio");
        }

        if (tecnico.getApellido() == null || tecnico.getApellido().trim().isEmpty()) {
            throw new TecnicoNoEncontradoException("El apellido del técnico es obligatorio");
        }

        if (tecnico.getEquipo() == null) {
            throw new TecnicoNoEncontradoException("El equipo del técnico es obligatorio");
        }
    }

    /**
     * Validaciones para un miembro del cuerpo técnico
     */
    private void validarCuerpoTecnico(CuerpoTecnico cuerpoTecnico) throws TecnicoNoEncontradoException {
        if (cuerpoTecnico == null) {
            throw new TecnicoNoEncontradoException("El cuerpo técnico no puede ser nulo");
        }

        if (cuerpoTecnico.getNombre() == null || cuerpoTecnico.getNombre().trim().isEmpty()) {
            throw new TecnicoNoEncontradoException("El nombre es obligatorio");
        }

        if (cuerpoTecnico.getApellido() == null || cuerpoTecnico.getApellido().trim().isEmpty()) {
            throw new TecnicoNoEncontradoException("El apellido es obligatorio");
        }

        if (cuerpoTecnico.getEspecialidad() == null || cuerpoTecnico.getEspecialidad().trim().isEmpty()) {
            throw new TecnicoNoEncontradoException("La especialidad es obligatoria");
        }

        if (cuerpoTecnico.getEquipo() == null) {
            throw new TecnicoNoEncontradoException("El equipo es obligatorio");
        }
    }

// ============================================================
    // CONSULTAS AVANZADAS
    // ============================================================

    /**
     * Consulta Avanzada 4: Lista técnicos sin equipo
     */
    public ArrayList<Tecnico> listarTecnicosSinEquipo() throws TecnicoNoEncontradoException {
        try {
            return tecnicoRepository.listarTecnicosSinEquipo();
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al listar técnicos sin equipo: " + e.getMessage());
        }
    }


    /**
     * Guarda un nuevo técnico
     */
    public void guardarTecnico(Tecnico tecnico) throws TecnicoNoEncontradoException {
        validarTecnico(tecnico);

        try {
            tecnicoRepository.guardar(tecnico);
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al guardar el técnico: " + e.getMessage());
        }
    }

    /**
     * Lista técnicos que tienen equipo asignado
     */
    public ArrayList<Tecnico> listarTecnicosConEquipo() throws TecnicoNoEncontradoException {
        try {
            ArrayList<Tecnico> todosLosTecnicos = tecnicoRepository.listarTodos();
            ArrayList<Tecnico> tecnicosConEquipo = new ArrayList<>();

            for (Tecnico tecnico : todosLosTecnicos) {
                if (tecnico.getEquipo() != null) {
                    tecnicosConEquipo.add(tecnico);
                }
            }

            return tecnicosConEquipo;
        } catch (RepositoryException e) {
            throw new TecnicoNoEncontradoException("Error al listar técnicos con equipo: " + e.getMessage());
        }
    }
}