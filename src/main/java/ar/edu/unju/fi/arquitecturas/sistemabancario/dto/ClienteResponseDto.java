package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.Parentesco;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDto {
    private UUID id;
    private String nombre;
    private String cuil;
    private String mail;
    private String telefono;
    private String direccion;
    private Parentesco parentesco;
    private UUID titularId;

    @Builder.Default
    private List<UUID> adherentesIds = new ArrayList<>();
}
