package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TablaPosicion {
    private Integer idTabla;
    private int ganados;
    private int empates;
    private int perdidos;
    private int golesAFavor;
    private int golesEnContra;
    private int diferenciaGoles;
    private int puntos;
    private Equipo equipo;
}
