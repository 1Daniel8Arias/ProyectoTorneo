package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Municipio {
    private Integer idMunicipio;
    private String nombre;
    private Departamento departamento;
    private ArrayList<Ciudad> listaCiudades;
}
