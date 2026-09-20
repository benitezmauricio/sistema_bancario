package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransaccionService {

    Transaccion realizarDeposito(UUID cuentaId, BigDecimal monto);

    Transaccion realizarExtraccion(UUID cuentaId, BigDecimal monto);

    void realizarTransferencia(UUID cuentaOrigenId, UUID cuentaDestinoId, BigDecimal monto);

    List<Transaccion> obtenerHistorialPorCuenta(UUID cuentaId);

    Transaccion buscarPorId(UUID transaccionId);
}