package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Contrato {
    private Integer idContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double salario;
    private Jugador jugador;
}
