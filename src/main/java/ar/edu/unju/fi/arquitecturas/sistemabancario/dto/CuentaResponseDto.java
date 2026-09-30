package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoCuenta;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CuentaResponseDto {
    private UUID id;
    private String cbu;
    private String alias;
    private BigDecimal saldo;
    private EstadoCuenta estadoCuenta;
    private UUID titular;
    private List<UUID> cotitulares;
    private CuentaRequestDto.TipoCuenta tipoCuenta;

    // Solo se completan los campos correspondientes al tipo de cuenta.
    private Integer cupoLimite;
    private Float interesAnual;
    private Float margen;
    private Float costoComision;
}
