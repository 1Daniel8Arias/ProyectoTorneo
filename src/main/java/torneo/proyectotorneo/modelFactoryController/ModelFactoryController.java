package torneo.proyectotorneo.modelFactoryController;

import torneo.proyectotorneo.exeptions.RepositoryException;
import torneo.proyectotorneo.model.*;
import torneo.proyectotorneo.service.TorneoService;

import java.util.ArrayList;

public class ModelFactoryController {

    private static ModelFactoryController instance;
    private final TorneoService torneoService;



    public static ModelFactoryController getInstance() {
        if (instance == null) {
            instance = new ModelFactoryController();
        }
        return instance;
    }

    private ModelFactoryController() {
        this.torneoService = new TorneoService();
    }

    public TorneoService getTorneoService() {
        return torneoService;
    }

    private void cargarDatosIniciales() {
    }

    public ArrayList<Equipo> obtenerEquipos() throws RepositoryException {
        return torneoService.listarEquipos();
    }

    public void registrarEquipo(Equipo equipo) throws RepositoryException {
        torneoService.registrarEquipo(equipo);
    }

    public ArrayList<Partido> obtenerPartidos() throws RepositoryException {
        return torneoService.listarPartidos();
    }



    public Usuario obtenerUsuario(String usuario, String contrasenia) {
        return torneoService.obtenerUsuario( usuario,  contrasenia);
    }
}
