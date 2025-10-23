package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import torneo.proyectotorneo.model.enums.TipoSede;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoEstadio {
    private Equipo equipo;
    private Estadio estadio;
    private TipoSede sede; // LOCAL o NEUTRAL
}