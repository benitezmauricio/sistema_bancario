package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public CuentaBancaria crearCuenta(CuentaBancaria cuenta, UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cliente con ID: " + clienteId));

        if (cuentaBancariaRepository.findByCbu(cuenta.getCbu()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con el CBU: " + cuenta.getCbu());
        }

        if (cuentaBancariaRepository.findByAlias(cuenta.getAlias()).isPresent()) {
            throw new IllegalArgumentException("El alias ya está registrado: " + cuenta.getAlias());
        }

        cuenta.setTitular(cliente);
        return cuentaBancariaRepository.save(cuenta);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorId(UUID id) {
        return cuentaBancariaRepository.findById(id);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorCbu(String cbu) {
        return cuentaBancariaRepository.findByCbu(cbu);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorAlias(String alias) {
        return cuentaBancariaRepository.findByAlias(alias);
    }

    @Override
    public List<CuentaBancaria> buscarPorTitularId(UUID titularId) {
        return cuentaBancariaRepository.findByTitularId(titularId);
    }

    @Override
    @Transactional
    public void cambiarEstado(UUID cuentaId, EstadoCuenta nuevoEstado) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + cuentaId));

        cuenta.setEstadoCuenta(nuevoEstado);
        cuentaBancariaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void actualizarAlias(UUID cuentaId, String nuevoAlias) {
        if (cuentaBancariaRepository.findByAlias(nuevoAlias).isPresent()) {
            throw new IllegalArgumentException("El alias '" + nuevoAlias + "' ya pertenece a otra cuenta.");
        }

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + cuentaId));

        cuenta.setAlias(nuevoAlias);
        cuentaBancariaRepository.save(cuenta);
    }
}