package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Cliente {

    private String nombre;
    private Integer cuil;
    private String mail;
    private Integer telefono;
    private String direccion;

    public void registrarCLiente(){}

    public void registrarCotitular(){}
}


