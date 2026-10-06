package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaUpdateDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaCorriente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuentaBancariaValidationServiceImpl implements CuentaBancariaValidationService {

    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public void validarCbuUnico(String cbu) {
        if (cuentaBancariaRepository.findByCbu(cbu).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con el CBU: " + cbu);
        }
    }

    @Override
    public void validarAliasUnico(String alias) {
        if (cuentaBancariaRepository.findByAlias(alias).isPresent()) {
            throw new IllegalArgumentException("El alias ya está registrado: " + alias);
        }
    }

    @Override
    public void validarAliasUnicoParaActualizacion(UUID cuentaId, String nuevoAlias) {
        cuentaBancariaRepository.findByAlias(nuevoAlias).ifPresent(cuentaExistente -> {
            if (!cuentaExistente.getId().equals(cuentaId)) {
                throw new IllegalArgumentException("El alias '" + nuevoAlias + "' ya pertenece a otra cuenta.");
            }
        });
    }

    @Override
    public Cliente validarYObtenerTitular(UUID titularId) {
        return clienteRepository.findById(titularId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente titular con ID: " + titularId));
    }

    @Override
    public List<Cliente> validarYObtenerCotitulares(List<UUID> cotitularesIds) {
        List<UUID> ids = Optional.ofNullable(cotitularesIds).orElseGet(List::of);
        List<Cliente> cotitulares = new ArrayList<>();

        for (UUID cotitularId : ids.stream().distinct().toList()) {
            Cliente cotitular = clienteRepository.findById(cotitularId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cotitular con ID: " + cotitularId));
            cotitulares.add(cotitular);
        }

        return cotitulares;
    }

    @Override
    public CuentaBancaria validarYObtenerCuentaExistente(UUID cuentaId) {
        return cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con ID: " + cuentaId));
    }

    @Override
    public void validarCamposTipoCuenta(CuentaBancaria cuenta, CuentaUpdateDto dto) {
        if (cuenta instanceof CajaDeAhorro) {
            if (dto.getMargen() != null || dto.getCostoComision() != null) {
                throw new IllegalArgumentException("No se pueden definir 'margen' o 'costoComision' en una Caja de Ahorro.");
            }
        } else if (cuenta instanceof CuentaCorriente) {
            if (dto.getCupoLimite() != null || dto.getInteresAnual() != null) {
                throw new IllegalArgumentException("No se pueden definir 'cupoLimite' o 'interesAnual' en una Cuenta Corriente.");
            }
        }
    }
}
