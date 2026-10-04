package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.SaldoInsuficienteException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaCorriente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;

    @Override
    @Transactional
    public Transaccion realizarDeposito(UUID cuentaId, BigDecimal monto) {
        validarMonto(monto, "depositar");

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con id: " + cuentaId));

        if (cuenta.getSaldo() == null) {
            throw new IllegalStateException("La cuenta bancaria no tiene saldo inicializado");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaBancariaRepository.save(cuenta);

        Transaccion transaccion = Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(TipoTransaccion.DEPOSITO)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();

        return transaccionRepository.save(transaccion);
    }

    @Override
    @Transactional
    public Transaccion realizarExtraccion(UUID cuentaId, BigDecimal monto) {
        validarMonto(monto, "extraer");

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con id: " + cuentaId));

        if (cuenta.getSaldo() == null) {
            throw new IllegalStateException("La cuenta bancaria no tiene saldo inicializado");
        }

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar la extracción");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaBancariaRepository.save(cuenta);

        Transaccion transaccion = Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(TipoTransaccion.EXTRACCION)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();

        return transaccionRepository.save(transaccion);
    }
    private void validarMonto(BigDecimal monto, String operacion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a " + operacion + " debe ser mayor a cero");
        }
    }

    @Override
    @Transactional
    public TransaccionResponseDto realizarTransferencia(TransaccionRequestDto dto) {
        if (dto.getCuentaOrigenId().equals(dto.getCuentaDestinoId())) {
            throw new IllegalArgumentException("La cuenta de origen y de destino no pueden ser la misma");
        }

        CuentaBancaria origen = cuentaBancariaRepository.findById(dto.getCuentaOrigenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta origen no encontrada"));

        CuentaBancaria destino = cuentaBancariaRepository.findById(dto.getCuentaDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta destino no encontrada"));

        if (origen.getSaldo().compareTo(dto.getMonto()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta de origen");
        }

        origen.setSaldo(origen.getSaldo().subtract(dto.getMonto()));
        destino.setSaldo(destino.getSaldo().add(dto.getMonto()));
        cuentaBancariaRepository.save(origen);
        cuentaBancariaRepository.save(destino);

        transaccionRepository.save(crearMovimiento(origen, dto.getMonto(), TipoTransaccion.TRANSFERENCIA_ENVIADA));
        transaccionRepository.save(crearMovimiento(destino, dto.getMonto(), TipoTransaccion.TRANSFERENCIA_RECIBIDA));

        return TransaccionResponseDto.builder()
                .mensaje("Transferencia realizada exitosamente")
                .monto(dto.getMonto())
                .timestamp(LocalDateTime.now())
                .build();
    }

    //metodo auxiliar para no repetir codigo
    private Transaccion crearMovimiento(CuentaBancaria cuenta, BigDecimal monto, TipoTransaccion tipo) {
        return Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(tipo)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();
    }



    @Override
    public List<Transaccion> obtenerHistorialPorCuenta(UUID cuentaId) {
        return transaccionRepository.findByCuentaBancariaId(cuentaId);
    }

    @Override
    public Transaccion buscarPorId(UUID transaccionId) {
        return transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con id: " + transaccionId));
    }

    @Override
    @Transactional
    public void procesarDebitoComisionesMasivo(BigDecimal comisionAhorro, BigDecimal comisionCorriente) {
        int pageSize = 100; // Tamaño del lote ajustable
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<CuentaBancaria> paginaCuentas;

        do {
            paginaCuentas = cuentaBancariaRepository.findByEstadoCuenta(EstadoCuenta.ACTIVA, pageable);

            for (CuentaBancaria cuenta : paginaCuentas.getContent()) {
                BigDecimal comision = null;

                if (cuenta instanceof CajaDeAhorro) {
                    comision = comisionAhorro;
                } else if (cuenta instanceof CuentaCorriente) {
                    comision = comisionCorriente;
                }

                if (comision != null && comision.compareTo(BigDecimal.ZERO) > 0) {
                    cuenta.setSaldo(cuenta.getSaldo().subtract(comision));
                    cuentaBancariaRepository.save(cuenta);

                    Transaccion debito = Transaccion.builder()
                            .cuentaBancaria(cuenta)
                            .monto(comision)
                            .tipo(TipoTransaccion.DEBITO_COMISION)
                            .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                            .fecha(new Date())
                            .hora(Time.valueOf(LocalTime.now()))
                            .build();

                    transaccionRepository.save(debito);
                }
            }

            pageable = paginaCuentas.nextPageable();
        } while (paginaCuentas.hasNext());
    }
}