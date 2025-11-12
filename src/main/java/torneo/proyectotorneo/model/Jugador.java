package torneo.proyectotorneo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import torneo.proyectotorneo.model.enums.PosicionJugador;

import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jugador {
    private Integer id;
    private String nombre;
    private String apellido;
    private PosicionJugador posicion;
    private String numeroCamiseta;
    private Equipo equipo;
    private ArrayList<Contrato> listaContratos;
    private ArrayList<Sancion> listaSanciones;
    private ArrayList<Gol> listaGoles;
    private ArrayList<Tarjeta> listaTarjetas;
    private ArrayList<Sustitucion> listaSustitucionesEntradas;
    private ArrayList<Sustitucion> listaSustitucionesSalidas;


    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombreEquipo() {
        return equipo != null ? equipo.getNombre() : "Sin equipo";
    }

    public String getFechaContrato() {
        if (listaContratos == null || listaContratos.isEmpty()) {
            return "Sin contrato";
        }
        Contrato ultimo = listaContratos.get(listaContratos.size() - 1);
        return ultimo.getFechaInicio() != null ? ultimo.getFechaInicio().toString() : "N/A";
    }

}
