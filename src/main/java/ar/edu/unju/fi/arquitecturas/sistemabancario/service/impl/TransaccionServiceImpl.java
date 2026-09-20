package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
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

    @Override
    @Transactional
    public void realizarTransferencia(UUID cuentaOrigenId, UUID cuentaDestinoId, BigDecimal monto) {
        validarMonto(monto, "transferir");

        if (cuentaOrigenId.equals(cuentaDestinoId)) {
            throw new IllegalArgumentException("La cuenta de origen y destino no pueden ser la misma");
        }

        CuentaBancaria origen = cuentaBancariaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

        CuentaBancaria destino = cuentaBancariaRepository.findById(cuentaDestinoId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (origen.getSaldo() == null || destino.getSaldo() == null) {
            throw new IllegalStateException("Las cuentas deben tener el saldo inicializado");
        }

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente en la cuenta de origen");
        }

        origen.setSaldo(origen.getSaldo().subtract(monto));
        destino.setSaldo(destino.getSaldo().add(monto));

        cuentaBancariaRepository.save(origen);
        cuentaBancariaRepository.save(destino);

        Date fecha = new Date();
        Time hora = Time.valueOf(LocalTime.now());

        Transaccion transaccionDebito = Transaccion.builder()
                .cuentaBancaria(origen)
                .monto(monto)
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(fecha)
                .hora(hora)
                .build();

        Transaccion transaccionCredito = Transaccion.builder()
                .cuentaBancaria(destino)
                .monto(monto)
                .tipo(TipoTransaccion.TRANSFERENCIA_RECIBIDA)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(fecha)
                .hora(hora)
                .build();

        transaccionRepository.save(transaccionDebito);
        transaccionRepository.save(transaccionCredito);
    }

    private void validarMonto(BigDecimal monto, String operacion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a " + operacion + " debe ser mayor a cero");
        }
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
}