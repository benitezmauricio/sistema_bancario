package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.*;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Transaccion realizarDeposito(UUID cuentaId, float monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a cero");
        }

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con id: " + cuentaId));

        cuenta.setSaldo(cuenta.getSaldo() + monto);
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
    public Transaccion realizarExtraccion(UUID cuentaId, float monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a extraer debe ser mayor a cero");
        }

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con id: " + cuentaId));

        if (cuenta.getSaldo() < monto) {
            throw new RuntimeException("Saldo insuficiente para realizar la extracción");
        }

        cuenta.setSaldo(cuenta.getSaldo() - monto);
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
    public void realizarTransferencia(UUID cuentaOrigenId, UUID cuentaDestinoId, float monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a transferir debe ser mayor a cero");
        }
        if (cuentaOrigenId.equals(cuentaDestinoId)) {
            throw new IllegalArgumentException("La cuenta de origen y destino no pueden ser la misma");
        }

        CuentaBancaria origen = cuentaBancariaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

        CuentaBancaria destino = cuentaBancariaRepository.findById(cuentaDestinoId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (origen.getSaldo() < monto) {
            throw new RuntimeException("Saldo insuficiente en la cuenta de origen");
        }

        origen.setSaldo(origen.getSaldo() - monto);
        destino.setSaldo(destino.getSaldo() + monto);

        cuentaBancariaRepository.save(origen);
        cuentaBancariaRepository.save(destino);

        Transaccion transaccionDebito = Transaccion.builder()
                .cuentaBancaria(origen)
                .monto(monto)
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();

        Transaccion transaccionCredito = Transaccion.builder()
                .cuentaBancaria(destino)
                .monto(monto)
                .tipo(TipoTransaccion.TRANSFERENCIA_RECIBIDA)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();

        transaccionRepository.save(transaccionDebito);
        transaccionRepository.save(transaccionCredito);
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