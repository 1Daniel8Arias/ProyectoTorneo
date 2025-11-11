package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class TorneoService {

    private  EquipoService EquipoService;
    private  PartidoService PartidoService;
    private  JornadaService JornadaService;
    private  ArbitroService ArbitroService;
    private  JugadorService JugadorService;
    private  GolService GolService;
    private  EstadioService EstadioService;
    private  TecnicoService TecnicoService;
    private UsuarioService UsuarioService;


    public TorneoService() {
        this.EquipoService = new EquipoService();
        this.PartidoService = new PartidoService();
        this.JornadaService = new JornadaService();
        this.ArbitroService = new ArbitroService();
        this.JugadorService = new JugadorService();
        this.GolService = new GolService();
        this.EstadioService = new EstadioService();
        this.TecnicoService = new TecnicoService();
        this.UsuarioService= new UsuarioService();
    }

    // ================== EQUIPOS ==================
    public ArrayList<Equipo> listarEquipos() throws RepositoryException {
        return EquipoService.listarTodosLosEquipos();
    }

    public void registrarEquipo(Equipo equipo) throws RepositoryException {
        EquipoService.registrarEquipo(equipo);
    }

    public void actualizarEquipo(Equipo equipo) throws RepositoryException {
        EquipoService.actualizarEquipo(equipo);
    }

    public void eliminarEquipo(int id) throws RepositoryException {
        EquipoService.eliminarEquipo(id);
    }

    public ArrayList<Equipo> listarEquiposConTecnico() throws RepositoryException {
        return EquipoService.listarEquiposConTecnico();
    }

    // ================== PARTIDOS ==================
    public ArrayList<Partido> listarPartidos() throws RepositoryException {
        return PartidoService.listarTodosLosPartidos();
    }

    public void registrarPartido(Partido partido) throws RepositoryException {
        PartidoService.guardar(partido);
    }

    public Usuario obtenerUsuario(String usuario, String contrasenia) {
        
        return UsuarioService.buscarUsuario(usuario,contrasenia);
    }

    // ================== ESTADÍSTICAS GENERALES ==================
    public int contarEquipos() throws RepositoryException {
        return EquipoService.listarTodosLosEquipos().size();
    }

    public int contarJugadores() throws RepositoryException {
        return JugadorService.listarTodosLosJugadores().size();
    }

    public int contarPartidosJugados() throws RepositoryException {
        return PartidoService.listarTodosLosPartidos().size();
    }

    public int contarJornadas() throws RepositoryException {
        return JornadaService.listarTodasLasJornadas().size();
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
        ArrayList<Partido> todosLosPartidos = PartidoService.listarTodosLosPartidos();
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
        ArrayList<Partido> todosLosPartidos = PartidoService.listarTodosLosPartidos();
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

}
