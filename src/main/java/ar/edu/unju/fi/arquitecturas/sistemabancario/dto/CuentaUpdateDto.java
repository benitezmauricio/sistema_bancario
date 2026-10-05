package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaUpdateDto {

    @Size(max = 50, message = "El alias no puede superar los 50 caracteres")
    private String alias;

    private EstadoCuenta estadoCuenta;

    /** Nueva lista de cotitulares (reemplaza la existente si no es null). */
    private List<UUID> cotitulares;

    // Campos aplicables exclusivamente a Caja de Ahorro
    @PositiveOrZero(message = "El cupo límite no puede ser negativo")
    private Integer cupoLimite;

    @PositiveOrZero(message = "El interés anual no puede ser negativo")
    private Float interesAnual;

    // Campos aplicables exclusivamente a Cuenta Corriente
    @PositiveOrZero(message = "El margen no puede ser negativo")
    private Float margen;

    @PositiveOrZero(message = "El costo de comisión no puede ser negativo")
    private Float costoComision;
}
