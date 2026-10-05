package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuentaBancariaCrudServiceImpl implements CuentaBancariaCrudService {

    private final CuentaBancariaRepository cuentaBancariaRepository;

    @Override
    @Transactional
    public CuentaBancaria guardar(CuentaBancaria cuenta) {
        return cuentaBancariaRepository.save(cuenta);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorId(UUID id) {
        return cuentaBancariaRepository.findById(id);
    }

    @Override
    public CuentaBancaria buscarPorIdOError(UUID id) {
        return cuentaBancariaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con ID: " + id));
    }

    @Override
    public Optional<CuentaBancaria> buscarPorCbu(String cbu) {
        return cuentaBancariaRepository.findByCbu(cbu);
    }

    @Override
    public CuentaBancaria buscarPorCbuOError(String cbu) {
        return cuentaBancariaRepository.findByCbu(cbu)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con CBU: " + cbu));
    }

    @Override
    public Optional<CuentaBancaria> buscarPorAlias(String alias) {
        return cuentaBancariaRepository.findByAlias(alias);
    }

    @Override
    public CuentaBancaria buscarPorAliasOError(String alias) {
        return cuentaBancariaRepository.findByAlias(alias)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con Alias: " + alias));
    }

    @Override
    public List<CuentaBancaria> buscarPorTitularId(UUID titularId) {
        return cuentaBancariaRepository.findByTitularId(titularId);
    }

    @Override
    public Page<CuentaBancaria> listarTodas(Pageable pageable) {
        return cuentaBancariaRepository.findAll(pageable);
    }

    @Override
    public Page<CuentaBancaria> listarPorEstado(EstadoCuenta estado, Pageable pageable) {
        return cuentaBancariaRepository.findByEstadoCuenta(estado, pageable);
    }

    @Override
    @Transactional
    public void eliminarPorId(UUID id) {
        CuentaBancaria cuenta = buscarPorIdOError(id);
        cuentaBancariaRepository.delete(cuenta);
    }

    @Override
    @Transactional
    public void eliminarPorCbu(String cbu) {
        CuentaBancaria cuenta = buscarPorCbuOError(cbu);
        cuentaBancariaRepository.delete(cuenta);
    }

    @Override
    public boolean existePorId(UUID id) {
        return cuentaBancariaRepository.existsById(id);
    }
}
