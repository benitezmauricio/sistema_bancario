package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraccionRequestDto {

    @NotNull(message = "El ID de la cuenta bancaria es obligatorio")
    private UUID cuentaId;

    @NotNull(message = "El ID del cliente solicitante es obligatorio")
    private UUID clienteId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto a extraer debe ser mayor a cero")
    private BigDecimal monto;
}
