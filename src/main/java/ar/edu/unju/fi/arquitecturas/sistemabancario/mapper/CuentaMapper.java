package ar.edu.unju.fi.arquitecturas.sistemabancario.mapper;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaRequestDto.TipoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaCorriente;

public final class CuentaMapper {
    private CuentaMapper() {
    }

    public static CuentaResponseDto toResponse(CuentaBancaria cuenta) {
        CuentaResponseDto.CuentaResponseDtoBuilder response = CuentaResponseDto.builder()
                .id(cuenta.getId())
                .cbu(cuenta.getCbu())
                .alias(cuenta.getAlias())
                .saldo(cuenta.getSaldo())
                .estadoCuenta(cuenta.getEstadoCuenta())
                .titular(cuenta.getTitular().getId())
                .cotitulares(cuenta.getCotitulares().stream()
                        .map(Cliente::getId)
                        .toList());

        if (cuenta instanceof CajaDeAhorro ahorro) {
            response.tipoCuenta(TipoCuenta.CAJA_DE_AHORRO)
                    .cupoLimite(ahorro.getCupoLimite())
                    .interesAnual(ahorro.getInteresAnual());
        } else if (cuenta instanceof CuentaCorriente corriente) {
            response.tipoCuenta(TipoCuenta.CUENTA_CORRIENTE)
                    .margen(corriente.getMargen())
                    .costoComision(corriente.getCostoComision());
        } else {
            throw new IllegalArgumentException("Tipo de cuenta no soportado");
        }

        return response.build();
    }
}
