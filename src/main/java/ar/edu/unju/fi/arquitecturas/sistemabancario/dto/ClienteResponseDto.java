package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ClienteResponseDto {
    private UUID id;
    private String nombre;
    private String cuil;
    private String mail;
    private String telefono;
    private String direccion;
}