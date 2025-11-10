package torneo.proyectotorneo.service;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.repository.*;

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
}
