package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TransaccionRequestDto {

    @NotNull(message = "El ID de la cuenta de origen es obligatorio")
    private UUID cuentaOrigenId;

    @NotNull(message = "El ID de la cuenta de destino es obligatorio")
    private UUID cuentaDestinoId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto a transferir debe ser mayor a cero")
    private BigDecimal monto;
}