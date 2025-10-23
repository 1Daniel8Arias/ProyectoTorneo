package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sancion {
    private Integer idSancion;
    private LocalDate fecha;
    private String motivo;
    private int duracion;
    private String tipo;
    private Jugador jugador;
}
