package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos requeridos para dar de alta una nueva cuenta bancaria en el sistema")
public class CuentaRequestDto {

    @Schema(description = "Clave Bancaria Uniforme (22 dígitos numéricos)", example = "0000003100000000000099", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El CBU es obligatorio")
    @Pattern(regexp = "[0-9]{22}", message = "El CBU debe tener 22 digitos")
    private String cbu;

    @Schema(description = "Alias identificador de la cuenta (máximo 20 caracteres)", example = "JUAN.PEREZ.ARS", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El alias es obligatorio")
    @Size(max = 20, message = "El alias no puede superar los 20 caracteres")
    private String alias;

    @Schema(description = "Saldo inicial de la cuenta (no puede ser negativo)", example = "10000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El saldo es obligatorio")
    @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
    @Digits(integer = 13, fraction = 2, message = "El saldo admite 13 enteros y 2 decimales")
    private BigDecimal saldo;

    @Schema(description = "Estado inicial de la cuenta", example = "ACTIVA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El estado de cuenta es obligatorio")
    private EstadoCuenta estadoCuenta;

    @Schema(description = "UUID del cliente que será titular de la cuenta", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El titular es obligatorio")
    private UUID titular;

    @ArraySchema(schema = @Schema(description = "UUID del cliente cotitular", example = "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22"))
    @Builder.Default
    private List<@NotNull(message = "El ID del cotitular es obligatorio") UUID> cotitulares = new ArrayList<>();

    @Schema(description = "Tipo de cuenta a crear (CAJA_DE_AHORRO o CUENTA_CORRIENTE)", example = "CAJA_DE_AHORRO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @Schema(description = "Límite operativo de transacciones (requerido únicamente para CAJA_DE_AHORRO)", example = "500000")
    @PositiveOrZero(message = "El cupo limite no puede ser negativo")
    private Integer cupoLimite;

    @Schema(description = "Tasa de interés anual (requerido únicamente para CAJA_DE_AHORRO)", example = "40.0")
    @PositiveOrZero(message = "El interes anual no puede ser negativo")
    private Float interesAnual;

    @Schema(description = "Margen de descubierto permitido (requerido únicamente para CUENTA_CORRIENTE)", example = "100000.0")
    @PositiveOrZero(message = "El margen no puede ser negativo")
    private Float margen;

    @Schema(description = "Costo de mantenimiento o comisión (requerido únicamente para CUENTA_CORRIENTE)", example = "1500.0")
    @PositiveOrZero(message = "El costo de comision no puede ser negativo")
    private Float costoComision;

    @Schema(hidden = true)
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