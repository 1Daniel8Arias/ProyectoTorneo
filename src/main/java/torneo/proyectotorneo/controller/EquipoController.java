package torneo.proyectotorneo.controller;

import torneo.proyectotorneo.model.Equipo;
import torneo.proyectotorneo.modelFactoryController.ModelFactoryController;

import java.util.List;

public class EquipoController {

    private final ModelFactoryController modelFactory;

    public EquipoController() {
        this.modelFactory = ModelFactoryController.getInstance();
    }

    public List<Equipo> obtenerEquipos() {
        return this.modelFactory.obtenerEquipos();
    }
}
