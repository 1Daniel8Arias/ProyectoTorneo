package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.repository.*;

import java.util.ArrayList;

public class TorneoService {

    private final EquipoRepository equipoRepository;
    private final PartidoRepository partidoRepository;
    private final JornadaRepository jornadaRepository;
    private final ArbitroRepository arbitroRepository;
    private final JugadorRepository jugadorRepository;
    private final GolRepository golRepository;
    private final EstadioRepository estadioRepository;
    private final TecnicoRepository tecnicoRepository;


    public TorneoService() {
        this.equipoRepository = new EquipoRepository();
        this.partidoRepository = new PartidoRepository();
        this.jornadaRepository = new JornadaRepository();
        this.arbitroRepository = new ArbitroRepository();
        this.jugadorRepository = new JugadorRepository();
        this.golRepository = new GolRepository();
        this.estadioRepository = new EstadioRepository();
        this.tecnicoRepository = new TecnicoRepository();
    }

    // ================== EQUIPOS ==================
    public ArrayList<Equipo> listarEquipos() throws RepositoryException {
        return equipoRepository.listarTodos();
    }

    public void registrarEquipo(Equipo equipo) throws RepositoryException {
        equipoRepository.guardar(equipo);
    }

    public void actualizarEquipo(Equipo equipo) throws RepositoryException {
        equipoRepository.actualizar(equipo);
    }

    public void eliminarEquipo(int id) throws RepositoryException {
        equipoRepository.eliminar(id);
    }

    public ArrayList<Equipo> listarEquiposConTecnico() throws RepositoryException {
        return equipoRepository.listarEquiposConTecnico();
    }

    // ================== PARTIDOS ==================
    public ArrayList<Partido> listarPartidos() throws RepositoryException {
        return partidoRepository.listarTodos();
    }

    public void registrarPartido(Partido partido) throws RepositoryException {
        partidoRepository.guardar(partido);
    }

    // ================== JORNADAS ==================
    public ArrayList<Jornada> listarJornadas() throws RepositoryException {
        return jornadaRepository.listarTodos();
    }

    // ================== JUGADORES ==================
    public ArrayList<Jugador> listarJugadores() throws RepositoryException {
        return jugadorRepository.listarTodos();
    }

    // ================== ÁRBITROS ==================
    public ArrayList<Arbitro> listarArbitros() throws RepositoryException {
        return arbitroRepository.listarTodos();
    }

    // ================== GOLES ==================
    public ArrayList<Gol> listarGoles() throws RepositoryException {
        return golRepository.listarTodos();
    }

    // ================== ESTADIOS ==================
    public ArrayList<Estadio> listarEstadios() throws RepositoryException {
        return estadioRepository.listarTodos();
    }

    // ================== TÉCNICOS ==================
    public ArrayList<Tecnico> listarTecnicos() throws RepositoryException {
        return tecnicoRepository.listarTodos();
    }


}
