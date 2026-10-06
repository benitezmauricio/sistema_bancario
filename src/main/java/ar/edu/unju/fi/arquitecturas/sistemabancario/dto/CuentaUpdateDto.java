package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Campos modificables para la actualización parcial de una cuenta bancaria")
public class CuentaUpdateDto {

    @Schema(description = "Nuevo alias para la cuenta (máximo 50 caracteres)", example = "JUAN.PEREZ.NUEVO")
    @Size(max = 50, message = "El alias no puede superar los 50 caracteres")
    private String alias;

    @Schema(description = "Nuevo estado de la cuenta", example = "BLOQUEADA")
    private EstadoCuenta estadoCuenta;

    /** Nueva lista de cotitulares (reemplaza la existente si no es null). */
    @ArraySchema(schema = @Schema(description = "UUID del cliente cotitular", example = "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22"))
    private List<UUID> cotitulares;

    // Campos aplicables exclusivamente a Caja de Ahorro
    @Schema(description = "Nuevo límite operativo (exclusivo para Caja de Ahorro)", example = "600000")
    @PositiveOrZero(message = "El cupo límite no puede ser negativo")
    private Integer cupoLimite;

    @Schema(description = "Nueva tasa de interés anual (exclusivo para Caja de Ahorro)", example = "42.5")
    @PositiveOrZero(message = "El interés anual no puede ser negativo")
    private Float interesAnual;

    // Campos aplicables exclusivamente a Cuenta Corriente
    @Schema(description = "Nuevo margen de descubierto asignado (exclusivo para Cuenta Corriente)", example = "150000.0")
    @PositiveOrZero(message = "El margen no puede ser negativo")
    private Float margen;

    @Schema(description = "Nuevo costo mensual de comisión (exclusivo para Cuenta Corriente)", example = "2000.0")
    @PositiveOrZero(message = "El costo de comisión no puede ser negativo")
    private Float costoComision;
}