package torneo.proyectotorneo.service;

import torneo.proyectotorneo.controller.JugadorController;
import torneo.proyectotorneo.exeptions.EquipoNoEncontradoException;
import torneo.proyectotorneo.exeptions.JugadorNoEncontradoException;
import torneo.proyectotorneo.exeptions.PartidoNoEncontradoException;
import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.model.enums.PosicionJugador;

import java.time.LocalDate;
import java.util.ArrayList;

public class TorneoService {

    private final EquipoService equipoService;
    private final PartidoService partidoService;
    private final JornadaService jornadaService;
    private final ArbitroService arbitroService;
    private final JugadorService jugadorService;
    private final GolService golService;
    private final EstadioService estadioService;
    private final TecnicoService tecnicoService;
    private final UsuarioService usuarioService;


    public TorneoService() {
        this.equipoService = new EquipoService();
        this.partidoService = new PartidoService();
        this.jornadaService = new JornadaService();
        this.arbitroService = new ArbitroService();
        this.jugadorService = new JugadorService();
        this.golService = new GolService();
        this.estadioService = new EstadioService();
        this.tecnicoService = new TecnicoService();
        this.usuarioService = new UsuarioService();
    }

    // ================== EQUIPOS ==================
    public ArrayList<Equipo> listarEquipos() throws RepositoryException {
        return equipoService.listarTodosLosEquipos();
    }

    public void registrarEquipo(Equipo equipo) throws RepositoryException {
        equipoService.registrarEquipo(equipo);
    }

    public void actualizarEquipo(Equipo equipo) throws RepositoryException {
        equipoService.actualizarEquipo(equipo);
    }

    public void eliminarEquipo(int id) throws RepositoryException {
        equipoService.eliminarEquipo(id);
    }

    public ArrayList<Equipo> listarEquiposConTecnico() throws RepositoryException {
        return equipoService.listarEquiposConTecnico();
    }

    // ================== PARTIDOS ==================
    public ArrayList<Partido> listarPartidos() throws RepositoryException {
        return partidoService.listarTodosLosPartidos();
    }

    public void registrarPartido(Partido partido) throws RepositoryException {
        partidoService.guardar(partido);
    }

    public Usuario obtenerUsuario(String usuario, String contrasenia) {

        return usuarioService.buscarUsuario(usuario,contrasenia);
    }

    // ================== ESTADÍSTICAS GENERALES ==================
    public int contarEquipos() throws RepositoryException {
        return equipoService.listarTodosLosEquipos().size();
    }

    public int contarJugadores() throws RepositoryException {
        return jugadorService.listarTodosLosJugadores().size();
    }

    public int contarPartidosJugados() throws RepositoryException {
        return partidoService.listarTodosLosPartidos().size();
    }

    public int contarJornadas() throws RepositoryException {
        return jornadaService.listarTodasLasJornadas().size();
    }

    // ================== TABLA DE POSICIONES ==================
    public ArrayList<TablaPosicion> obtenerTablaPosicionesTop5() throws RepositoryException {
        ArrayList<TablaPosicion> tablaPosiciones = new ArrayList<>();
        TablaPosicionService tablaPosicionService = new TablaPosicionService();

        ArrayList<TablaPosicion> todasLasPosiciones = tablaPosicionService.obtenerTablaPosiciones();

        // Retornar solo los primeros 5
        int limite = Math.min(5, todasLasPosiciones.size());
        for (int i = 0; i < limite; i++) {
            tablaPosiciones.add(todasLasPosiciones.get(i));
        }

        return tablaPosiciones;
    }

    // ================== PRÓXIMOS PARTIDOS ==================
    public ArrayList<Partido> obtenerProximosPartidos() throws RepositoryException {
        ArrayList<Partido> todosLosPartidos = partidoService.listarTodosLosPartidos();
        ArrayList<Partido> proximosPartidos = new ArrayList<>();

        LocalDate hoy = LocalDate.now();

        // Filtrar partidos futuros
        for (Partido partido : todosLosPartidos) {
            if (partido.getFecha() != null && !partido.getFecha().isBefore(hoy)) {
                proximosPartidos.add(partido);
            }
        }

        // Ordenar por fecha ascendente
        proximosPartidos.sort((p1, p2) -> p1.getFecha().compareTo(p2.getFecha()));

        // Retornar solo los primeros 3
        int limite = Math.min(3, proximosPartidos.size());
        ArrayList<Partido> resultado = new ArrayList<>();
        for (int i = 0; i < limite; i++) {
            resultado.add(proximosPartidos.get(i));
        }

        return resultado;
    }

    // ================== RESULTADOS RECIENTES ==================
    public ArrayList<Partido> obtenerResultadosRecientes() throws RepositoryException {
        ArrayList<Partido> todosLosPartidos = partidoService.listarTodosLosPartidos();
        ArrayList<Partido> partidosConResultado = new ArrayList<>();

        // Filtrar solo partidos con resultado final
        for (Partido partido : todosLosPartidos) {
            if (partido.getResultadoFinal() != null) {
                partidosConResultado.add(partido);
            }
        }

        // Ordenar por fecha descendente (más recientes primero)
        partidosConResultado.sort((p1, p2) -> p2.getFecha().compareTo(p1.getFecha()));

        // Retornar solo los primeros 3
        int limite = Math.min(3, partidosConResultado.size());
        ArrayList<Partido> resultado = new ArrayList<>();
        for (int i = 0; i < limite; i++) {
            resultado.add(partidosConResultado.get(i));
        }

        return resultado;
    }

    // ==================   JUGADORES   ===========================


    public ArrayList<Jugador> listarJugadores() throws RepositoryException {
        try {
            return jugadorService.listarTodosLosJugadores();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresPorEquipo(int idEquipo) throws RepositoryException {
        try {
            return jugadorService.listarJugadoresPorEquipo(idEquipo);
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    public void guardarJugador(Jugador jugador) throws RepositoryException {
        try {
             jugadorService.guardarJugador(jugador);
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public void actualizarJugador(Jugador jugador) throws RepositoryException {
        try {
            jugadorService.actualizarJugador(jugador);
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public void eliminarJugador(int id) throws RepositoryException {
        try {
            jugadorService.eliminarJugador(id);
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }
    // ─────────────── Consultas Intermedias ───────────────
    public ArrayList<Jugador> listarJugadoresConEquipo() throws RepositoryException {
        try {
            return jugadorService.listarJugadoresConEquipo();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresConContratoYEquipo() throws RepositoryException {
        try {
            return jugadorService.listarJugadoresConContratoYEquipo();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarCapitanes() throws RepositoryException {
        try {
            return jugadorService.listarCapitanes();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    // ─────────────── Consultas Avanzadas ───────────────
    public ArrayList<Jugador> listarJugadoresConSalarioSuperiorPromedio() throws RepositoryException {
        try {
            return jugadorService.listarJugadoresConSalarioSuperiorPromedio();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresConGolesEnMasDeUnPartido() throws RepositoryException {
        try {
            return jugadorService.listarJugadoresConGolesEnMasDeUnPartido();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresSinContratoActivo() throws RepositoryException {
        try {
            return jugadorService.listarJugadoresSinContratoActivo();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarDelanterosSinGoles() throws RepositoryException {
        try {
            return jugadorService.listarDelanterosSinGoles();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresConMayorSalarioPorPosicion() throws RepositoryException {
        try {
            return jugadorService.listarJugadoresConMayorSalarioPorPosicion();
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    public ArrayList<Jugador> listarJugadoresPorEquipoYPosicion(int idEquipo, PosicionJugador posicion) {
        try {
            return jugadorService.listarJugadoresPorEquipoYPosicion(idEquipo,posicion);
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jugador> listarJugadoresPorPosicion(PosicionJugador posicion) {
        try {
            return jugadorService.listarJugadoresPorPosicion(posicion);
        } catch (JugadorNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    public int obtenerIdEquipoPorNombre(String nombreEquipo) {
        try {
            return equipoService.obtenerIdPorNombre(nombreEquipo);
        } catch (EquipoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public JugadorService getJugadorService() {
        return jugadorService;
    }

    public Partido buscarPartidoPorId(int id) {
        try {
            return partidoService.buscarPartidoPorId(id);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }

    }

    public void actualizarPartido(Partido partido) {
        try {
             partidoService.actualizarPartido(partido);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public void eliminarPartido(int id) {
        try {
            partidoService.eliminarPartido(id);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Partido> obtenerPartidosPorEquipo(int idEquipo) {
        try {
            return partidoService.obtenerPartidosPorEquipo(idEquipo);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }

    }

    public ArrayList<Partido> obtenerPartidosPorJornada(int idJornada) {
        try {
            return partidoService.obtenerPartidosPorJornada(idJornada);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Partido> obtenerPartidosPorEstadio(int idEstadio) {
        try {
            return partidoService.obtenerPartidosPorEstadio(idEstadio);
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Partido> listarPartidosConEquiposYEstadio() {
        try {
            return partidoService.listarPartidosConEquiposYEstadio();
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Jornada> listarJornadas() {
        try {
            return jornadaService.listarTodasLasJornadas();
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    public ArrayList<Estadio> listarEstadios() {
        try {
            return estadioService.listarTodosLosEstadios();
        } catch (PartidoNoEncontradoException e) {
            throw new RepositoryException(e.getMessage());
        }
    }
}
