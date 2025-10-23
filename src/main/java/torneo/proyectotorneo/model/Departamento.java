package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {
    private Integer idDepartamento;
    private String nombre;
    private ArrayList<Municipio> listaMunicipios;
    private ArrayList<Estadio>listaEstadios;
}
