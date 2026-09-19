package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;

import java.util.List;
import java.util.UUID;

public interface TransaccionService {

    Transaccion realizarDeposito(UUID cuentaId, float monto);

    Transaccion realizarExtraccion(UUID cuentaId, float monto);

    void realizarTransferencia(UUID cuentaOrigenId, UUID cuentaDestinoId, float monto);

    List<Transaccion> obtenerHistorialPorCuenta(UUID cuentaId);

    Transaccion buscarPorId(UUID transaccionId);
}