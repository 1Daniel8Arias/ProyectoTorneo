package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tecnico {
    private Integer id;
    private String nombre;
    private String apellido;
    private Equipo equipo;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombreEquipo() {
        return equipo != null ? equipo.getNombre() : "Sin equipo";
    }
}
