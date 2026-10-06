package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaUpdateDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.mapper.CuentaMapper;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaCorriente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaCrudService;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaService;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaValidationService;
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
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    private final CuentaBancariaCrudService crudService;
    private final CuentaBancariaValidationService validationService;

    @Override
    @Transactional
    public CuentaResponseDto crearCuenta(CuentaRequestDto request) {
        CuentaBancaria cuenta = switch (request.getTipoCuenta()) {
            case CAJA_DE_AHORRO ->
                    new CajaDeAhorro(request.getCupoLimite(), request.getInteresAnual());
            case CUENTA_CORRIENTE ->
                    new CuentaCorriente(request.getMargen(), request.getCostoComision());
        };

        cuenta.setCbu(request.getCbu());
        cuenta.setAlias(request.getAlias());
        cuenta.setSaldo(request.getSaldo());
        cuenta.setEstadoCuenta(request.getEstadoCuenta());

        List<Cliente> cotitulares = validationService.validarYObtenerCotitulares(request.getCotitulares());
        cuenta.setCotitulares(cotitulares);

        return CuentaMapper.toResponse(crearCuenta(cuenta, request.getTitular()));
    }

    @Override
    public Optional<CuentaResponseDto> buscarDetallePorCbu(String cbu) {
        return crudService.buscarPorCbu(cbu).map(CuentaMapper::toResponse);
    }

    @Override
    public CuentaResponseDto buscarDetallePorId(UUID id) {
        CuentaBancaria cuenta = crudService.buscarPorIdOError(id);
        return CuentaMapper.toResponse(cuenta);
    }

    @Override
    public CuentaResponseDto buscarDetallePorAlias(String alias) {
        CuentaBancaria cuenta = crudService.buscarPorAliasOError(alias);
        return CuentaMapper.toResponse(cuenta);
    }

    @Override
    public Page<CuentaResponseDto> listarCuentas(EstadoCuenta estado, Pageable pageable) {
        Page<CuentaBancaria> pagina = (estado != null)
                ? crudService.listarPorEstado(estado, pageable)
                : crudService.listarTodas(pageable);
        return pagina.map(CuentaMapper::toResponse);
    }

    @Override
    @Transactional
    public CuentaResponseDto actualizarCuentaPorId(UUID id, CuentaUpdateDto dto) {
        CuentaBancaria cuenta = crudService.buscarPorIdOError(id);
        return aplicarActualizacionYGuardar(cuenta, dto);
    }

    @Override
    @Transactional
    public CuentaResponseDto actualizarCuentaPorCbu(String cbu, CuentaUpdateDto dto) {
        CuentaBancaria cuenta = crudService.buscarPorCbuOError(cbu);
        return aplicarActualizacionYGuardar(cuenta, dto);
    }

    private CuentaResponseDto aplicarActualizacionYGuardar(CuentaBancaria cuenta, CuentaUpdateDto dto) {
        validationService.validarCamposTipoCuenta(cuenta, dto);

        if (dto.getAlias() != null && !dto.getAlias().equalsIgnoreCase(cuenta.getAlias())) {
            validationService.validarAliasUnicoParaActualizacion(cuenta.getId(), dto.getAlias());
            cuenta.setAlias(dto.getAlias());
        }

        if (dto.getEstadoCuenta() != null) {
            cuenta.setEstadoCuenta(dto.getEstadoCuenta());
        }

        if (dto.getCotitulares() != null) {
            List<Cliente> nuevosCotitulares = validationService.validarYObtenerCotitulares(dto.getCotitulares());
            cuenta.setCotitulares(nuevosCotitulares);
        }

        if (cuenta instanceof CajaDeAhorro ahorro) {
            if (dto.getCupoLimite() != null) {
                ahorro.setCupoLimite(dto.getCupoLimite());
            }
            if (dto.getInteresAnual() != null) {
                ahorro.setInteresAnual(dto.getInteresAnual());
            }
        } else if (cuenta instanceof CuentaCorriente corriente) {
            if (dto.getMargen() != null) {
                corriente.setMargen(dto.getMargen());
            }
            if (dto.getCostoComision() != null) {
                corriente.setCostoComision(dto.getCostoComision());
            }
        }

        CuentaBancaria guardada = crudService.guardar(cuenta);
        return CuentaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void eliminarCuentaPorId(UUID id) {
        crudService.eliminarPorId(id);
    }

    @Override
    @Transactional
    public void eliminarCuentaPorCbu(String cbu) {
        crudService.eliminarPorCbu(cbu);
    }

    @Override
    @Transactional
    public CuentaBancaria crearCuenta(CuentaBancaria cuenta, UUID clienteId) {
        Cliente titular = validationService.validarYObtenerTitular(clienteId);
        validationService.validarCbuUnico(cuenta.getCbu());
        validationService.validarAliasUnico(cuenta.getAlias());

        cuenta.setTitular(titular);
        return crudService.guardar(cuenta);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorId(UUID id) {
        return crudService.buscarPorId(id);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorCbu(String cbu) {
        return crudService.buscarPorCbu(cbu);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorAlias(String alias) {
        return crudService.buscarPorAlias(alias);
    }

    @Override
    public List<CuentaBancaria> buscarPorTitularId(UUID titularId) {
        return crudService.buscarPorTitularId(titularId);
    }

    @Override
    @Transactional
    public void cambiarEstado(UUID cuentaId, EstadoCuenta nuevoEstado) {
        CuentaBancaria cuenta = validationService.validarYObtenerCuentaExistente(cuentaId);
        cuenta.setEstadoCuenta(nuevoEstado);
        crudService.guardar(cuenta);
    }

    @Override
    @Transactional
    public void actualizarAlias(UUID cuentaId, String nuevoAlias) {
        validationService.validarAliasUnicoParaActualizacion(cuentaId, nuevoAlias);
        CuentaBancaria cuenta = validationService.validarYObtenerCuentaExistente(cuentaId);
        cuenta.setAlias(nuevoAlias);
        crudService.guardar(cuenta);
    }
}
