package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.EstadioNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.TipoSede;
import torneo.proyectotorneo.repository.*;

import java.util.ArrayList;

public class EstadioService {

    private final EstadioRepository estadioRepository;
    private final EquipoEstadioRepository equipoEstadioRepository;
    private final PartidoRepository partidoRepository;
    private final EquipoRepository equipoRepository;
    private final DepartamentoRepository departamentoRepository;

    public EstadioService() {
        this.estadioRepository = new EstadioRepository();
        this.equipoEstadioRepository = new EquipoEstadioRepository();
        this.partidoRepository = new PartidoRepository();
        this.equipoRepository = new EquipoRepository();
        this.departamentoRepository = new DepartamentoRepository();
    }

    /**
     * Lista todos los estadios registrados
     */
    public ArrayList<Estadio> listarTodosLosEstadios() throws RepositoryException {
        return estadioRepository.listarTodos();
    }

    /**
     * Busca un estadio por su ID
     */
    public Estadio buscarEstadioPorId(int id) throws EstadioNoEncontradoException {
        try {
            Estadio estadio = estadioRepository.buscarPorId(id);
            if (estadio == null) {
                throw new EstadioNoEncontradoException("No se encontró el estadio con ID: " + id);
            }
            return estadio;
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al buscar el estadio: " + e.getMessage());
        }
    }

    /**
     * Busca estadios por departamento
     */
    public ArrayList<Estadio> buscarEstadiosPorDepartamento(int idDepartamento) throws EstadioNoEncontradoException {
        try {
            return estadioRepository.buscarPorDepartamento(idDepartamento);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al buscar estadios: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo estadio
     */
    public void registrarEstadio(Estadio estadio) throws EstadioNoEncontradoException {
        validarEstadio(estadio);

        try {
            estadioRepository.guardar(estadio);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al registrar el estadio: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un estadio
     */
    public void actualizarEstadio(Estadio estadio) throws EstadioNoEncontradoException {
        validarEstadio(estadio);

        if (estadio.getIdEstadio() == null) {
            throw new EstadioNoEncontradoException("El ID del estadio es requerido para actualizar");
        }

        try {
            estadioRepository.actualizar(estadio);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al actualizar el estadio: " + e.getMessage());
        }
    }

    /**
     * Asigna un estadio a un equipo como sede local o neutral
     */
    public void asignarEstadioAEquipo(int idEquipo, int idEstadio, TipoSede tipoSede)
            throws EstadioNoEncontradoException {
        try {
            // Verificar que el equipo existe
            Equipo equipo = equipoRepository.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new EstadioNoEncontradoException("El equipo no existe");
            }

            // Verificar que el estadio existe
            Estadio estadio = estadioRepository.buscarPorId(idEstadio);
            if (estadio == null) {
                throw new EstadioNoEncontradoException("El estadio no existe");
            }

            // Crear la relación
            EquipoEstadio equipoEstadio = new EquipoEstadio();
            equipoEstadio.setEquipo(equipo);
            equipoEstadio.setEstadio(estadio);
            equipoEstadio.setSede(tipoSede);

            equipoEstadioRepository.guardar(equipoEstadio);

        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al asignar el estadio: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los estadios de un equipo
     */


    /**
     * Obtiene el historial de partidos jugados en un estadio
     */
    public ArrayList<Partido> obtenerHistorialPartidos(int idEstadio)
            throws EstadioNoEncontradoException {
        try {
            return partidoRepository.buscarPartidosPorEstadio(idEstadio);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al obtener el historial: " + e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas del estadio
     */
    public String obtenerEstadisticasEstadio(int idEstadio) throws EstadioNoEncontradoException {
        try {
            Estadio estadio = estadioRepository.buscarPorId(idEstadio);
            if (estadio == null) {
                throw new EstadioNoEncontradoException("No se encontró el estadio");
            }

            ArrayList<Partido> partidos = partidoRepository.buscarPartidosPorEstadio(idEstadio);
            int totalPartidos = partidos.size();

            // Contar equipos que lo usan
            ArrayList<EquipoEstadio> equipos = equipoEstadioRepository.listarTodos();
            int equiposLocales = 0;
            int equiposNeutrales = 0;

            for (EquipoEstadio ee : equipos) {
                if (ee.getEstadio().getIdEstadio().equals(idEstadio)) {
                    if (ee.getSede() == TipoSede.Local) {
                        equiposLocales++;
                    } else {
                        equiposNeutrales++;
                    }
                }
            }

            return String.format(
                    "==== ESTADIO ====\n" +
                            "Nombre: %s\n" +
                            "Capacidad: %,d espectadores\n" +
                            "Departamento: %s\n" +
                            "Partidos Jugados: %d\n" +
                            "Equipos que lo usan como sede local: %d\n" +
                            "Equipos que lo usan como sede neutral: %d\n",
                    estadio.getNombre(),
                    estadio.getCapacidad(),
                    estadio.getDepartamento() != null ? estadio.getDepartamento().getNombre() : "N/A",
                    totalPartidos,
                    equiposLocales,
                    equiposNeutrales
            );

        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    /**
     * Elimina la relación entre un equipo y un estadio
     */
    public void removerEstadioDeEquipo(int idEquipo, int idEstadio)
            throws EstadioNoEncontradoException {
        try {
            equipoEstadioRepository.eliminar(idEquipo, idEstadio);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al remover el estadio: " + e.getMessage());
        }
    }

    /**
     * Elimina un estadio del sistema
     */
    public void eliminarEstadio(int id) throws EstadioNoEncontradoException {
        try {
            Estadio estadio = estadioRepository.buscarPorId(id);
            if (estadio == null) {
                throw new EstadioNoEncontradoException("No se encontró el estadio con ID: " + id);
            }

            estadioRepository.eliminar(id);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al eliminar el estadio: " + e.getMessage());
        }
    }

    /**
     * Obtiene los estadios con mayor capacidad
     */
    public String obtenerEstadiosMayorCapacidad(int limite) throws EstadioNoEncontradoException {
        try {
            ArrayList<Estadio> estadios = estadioRepository.listarTodos();

            // Ordenar por capacidad descendente
            estadios.sort((e1, e2) -> Integer.compare(e2.getCapacidad(), e1.getCapacidad()));

            StringBuilder resultado = new StringBuilder("Estadios con Mayor Capacidad:\n");
            int max = Math.min(limite, estadios.size());

            for (int i = 0; i < max; i++) {
                Estadio e = estadios.get(i);
                resultado.append(String.format("%d. %s - %,d espectadores\n",
                        i + 1,
                        e.getNombre(),
                        e.getCapacidad()
                ));
            }

            return resultado.toString();

        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al obtener estadios: " + e.getMessage());
        }
    }

    /**
     * Validaciones para un estadio
     */
    private void validarEstadio(Estadio estadio) throws EstadioNoEncontradoException {
        if (estadio == null) {
            throw new EstadioNoEncontradoException("El estadio no puede ser nulo");
        }

        if (estadio.getNombre() == null || estadio.getNombre().trim().isEmpty()) {
            throw new EstadioNoEncontradoException("El nombre del estadio es obligatorio");
        }

        if (estadio.getCapacidad() <= 0) {
            throw new EstadioNoEncontradoException("La capacidad debe ser mayor a cero");
        }

        if (estadio.getDepartamento() == null) {
            throw new EstadioNoEncontradoException("El departamento del estadio es obligatorio");
        }
    }

    /**
     * Guarda un nuevo estadio
     */
    public void guardarEstadio(Estadio estadio) throws EstadioNoEncontradoException {
        try {
            if (estadio == null || estadio.getNombre() == null || estadio.getNombre().trim().isEmpty()) {
                throw new EstadioNoEncontradoException("El estadio y su nombre no pueden ser nulos o vacíos");
            }
            if (estadio.getCapacidad() <= 0) {
                throw new EstadioNoEncontradoException("La capacidad del estadio debe ser mayor a 0");
            }
            estadioRepository.guardar(estadio);
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al guardar el estadio: " + e.getMessage());
        }
    }

    /**
     * Lista estadios por departamento
     */
    public ArrayList<Estadio> listarEstadiosPorDepartamento(String nombreDepartamento) throws EstadioNoEncontradoException {
        try {
            if (nombreDepartamento == null || nombreDepartamento.trim().isEmpty()) {
                throw new EstadioNoEncontradoException("El nombre del departamento no puede ser nulo o vacío");
            }

            ArrayList<Estadio> todosLosEstadios = estadioRepository.listarTodos();
            ArrayList<Estadio> estadiosPorDepartamento = new ArrayList<>();

            for (Estadio estadio : todosLosEstadios) {
                if (estadio.getDepartamento() != null &&
                        estadio.getDepartamento() != null &&
                        estadio.getDepartamento().getNombre().equalsIgnoreCase(nombreDepartamento)) {
                    estadiosPorDepartamento.add(estadio);
                }
            }

            return estadiosPorDepartamento;
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al listar estadios por departamento: " + e.getMessage());
        }
    }

    /**
     * Lista estadios con capacidad mínima especificada
     */
    public ArrayList<Estadio> listarEstadiosPorCapacidadMinima(int capacidadMinima) throws EstadioNoEncontradoException {
        try {
            if (capacidadMinima <= 0) {
                throw new EstadioNoEncontradoException("La capacidad mínima debe ser mayor a 0");
            }

            ArrayList<Estadio> todosLosEstadios = estadioRepository.listarTodos();
            ArrayList<Estadio> estadiosFiltrados = new ArrayList<>();

            for (Estadio estadio : todosLosEstadios) {
                if (estadio.getCapacidad() >= capacidadMinima) {
                    estadiosFiltrados.add(estadio);
                }
            }

            return estadiosFiltrados;
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al listar estadios por capacidad: " + e.getMessage());
        }
    }

    /**
     * Lista todos los departamentos disponibles
     */
    public ArrayList<Departamento> listarDepartamentos() throws EstadioNoEncontradoException {
        try {
            return departamentoRepository.listarTodos();
        } catch (RepositoryException e) {
            throw new EstadioNoEncontradoException("Error al listar departamentos: " + e.getMessage());
        }
    }

}
