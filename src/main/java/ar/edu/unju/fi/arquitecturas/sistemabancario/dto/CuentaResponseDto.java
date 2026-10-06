package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Detalle y estado de la cuenta bancaria devuelta por el sistema")
public class CuentaResponseDto {

    @Schema(description = "Identificador único de la cuenta (UUID)", example = "96d023ba-e140-42dd-9257-dd5cbdd2846e")
    private UUID id;

    @Schema(description = "Clave Bancaria Uniforme (22 dígitos)", example = "0000003100000000000099")
    private String cbu;

    @Schema(description = "Alias único de la cuenta", example = "JUAN.PEREZ.ARS")
    private String alias;

    @Schema(description = "Saldo disponible actual", example = "10000.00")
    private BigDecimal saldo;

    @Schema(description = "Estado actual de la cuenta", example = "ACTIVA")
    private EstadoCuenta estadoCuenta;

    @Schema(description = "UUID del cliente titular de la cuenta", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private UUID titular;

    @ArraySchema(schema = @Schema(description = "UUID del cliente cotitular asociado", example = "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22"))
    private List<UUID> cotitulares;

    @Schema(description = "Tipo de cuenta bancaria", example = "CAJA_DE_AHORRO")
    private CuentaRequestDto.TipoCuenta tipoCuenta;

    // Solo se completan los campos correspondientes al tipo de cuenta.
    @Schema(description = "Límite operativo de transacciones (Caja de Ahorro)", example = "500000")
    private Integer cupoLimite;

    @Schema(description = "Tasa de interés anual (Caja de Ahorro)", example = "40.0")
    private Float interesAnual;

    @Schema(description = "Margen de descubierto asignado (Cuenta Corriente)", example = "100000.0")
    private Float margen;

    @Schema(description = "Costo mensual de comisión o mantenimiento (Cuenta Corriente)", example = "1500.0")
    private Float costoComision;
}