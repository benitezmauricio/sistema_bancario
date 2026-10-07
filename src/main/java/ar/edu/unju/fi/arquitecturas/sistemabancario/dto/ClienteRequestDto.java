package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.Parentesco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El CUIL es obligatorio")
    @Size(min = 11, max = 11, message = "El CUIL debe tener 11 dígitos")
    private String cuil;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String mail;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    /** ID del titular al que se asocia este cliente en caso de ser adherente (opcional). */
    private UUID titularId;

    /** Parentesco con el titular si es un adherente (CONYUGE, HIJO) (opcional). */
    private Parentesco parentesco;
}