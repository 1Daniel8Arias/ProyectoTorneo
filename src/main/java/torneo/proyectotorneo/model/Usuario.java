package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import torneo.proyectotorneo.model.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private TipoUsuario rol; // enum
}
