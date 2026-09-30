package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoCuenta;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaRequestDto {

    @NotBlank(message = "El CBU es obligatorio")
    @Pattern(regexp = "[0-9]{22}", message = "El CBU debe tener 22 digitos")
    private String cbu;

    @NotBlank(message = "El alias es obligatorio")
    @Size(max = 20, message = "El alias no puede superar los 20 caracteres")
    private String alias;

    @NotNull(message = "El saldo es obligatorio")
    @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
    @Digits(integer = 13, fraction = 2, message = "El saldo admite 13 enteros y 2 decimales")
    private BigDecimal saldo;

    @NotNull(message = "El estado de cuenta es obligatorio")
    private EstadoCuenta estadoCuenta;

    @NotNull(message = "El titular es obligatorio")
    private UUID titular;

    @Builder.Default
    private List<@NotNull(message = "El ID del cotitular es obligatorio") UUID> cotitulares = new ArrayList<>();

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @PositiveOrZero(message = "El cupo limite no puede ser negativo")
    private Integer cupoLimite;

    @PositiveOrZero(message = "El interes anual no puede ser negativo")
    private Float interesAnual;

    @PositiveOrZero(message = "El margen no puede ser negativo")
    private Float margen;

    @PositiveOrZero(message = "El costo de comision no puede ser negativo")
    private Float costoComision;

    @AssertTrue(message = "Ahorro requiere cupoLimite e interesAnual; corriente requiere margen y costoComision. No envie campos del otro tipo")
    public boolean isCondicionesValidas() {
        if (tipoCuenta == null) {
            return true; // @NotNull informa el tipo faltante.
        }
        return switch (tipoCuenta) {
            case CAJA_DE_AHORRO -> cupoLimite != null && interesAnual != null
                    && margen == null && costoComision == null;
            case CUENTA_CORRIENTE -> margen != null && costoComision != null
                    && cupoLimite == null && interesAnual == null;
        };
    }

    public enum TipoCuenta {
        CAJA_DE_AHORRO,
        CUENTA_CORRIENTE
    }
}
