package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoCuenta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuentaBancariaService {
    CuentaBancaria crearCuenta(CuentaBancaria cuenta, UUID clienteId);

    Optional<CuentaBancaria> buscarPorId(UUID id);

    Optional<CuentaBancaria> buscarPorCbu(String cbu);

    Optional<CuentaBancaria> buscarPorAlias(String alias);

    List<CuentaBancaria> buscarPorClienteId(UUID clienteId);

    void cambiarEstado(UUID cuentaId, EstadoCuenta nuevoEstado);

    void actualizarAlias(UUID cuentaId, String nuevoAlias);
}
