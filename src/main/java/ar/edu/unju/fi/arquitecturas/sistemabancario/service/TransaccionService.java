package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransaccionService {

    Transaccion realizarDeposito(UUID cuentaId, BigDecimal monto);

    Transaccion realizarExtraccion(UUID cuentaId, BigDecimal monto);

    TransaccionResponseDto realizarTransferencia(TransaccionRequestDto dto);

    List<Transaccion> obtenerHistorialPorCuenta(UUID cuentaId);

    Transaccion buscarPorId(UUID transaccionId);
}