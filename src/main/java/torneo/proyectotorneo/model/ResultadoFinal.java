package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoFinal {
    private Integer idResultadoFinal;
    private int golesLocal;
    private int golesVisitante;
    private Partido partido;
}
