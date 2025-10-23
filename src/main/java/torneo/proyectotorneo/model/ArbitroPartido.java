package torneo.proyectotorneo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArbitroPartido {
    private Partido partido;
    private Arbitro arbitro;
    private String tipo;
}
