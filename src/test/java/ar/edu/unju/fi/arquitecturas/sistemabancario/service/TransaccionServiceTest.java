package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.TransaccionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransaccionServiceTest {

    // 1. Simulacion de repositorios
    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;

    // 2. Inyeccion del servicio real con los repositorios simulados
    @InjectMocks
    private TransaccionServiceImpl transaccionService;

    @Test
    @DisplayName("Debe realizar depósito incrementando el saldo y guardando la transacción")
    void realizarDeposito_CuandoMontoEsValido_DebeIncrementarSaldo() {
        // 1-ARRANGE (preparar cuenta y simular busqueda)
        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        cuenta.setSaldo(new BigDecimal("100.00"));

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaBancariaRepository.save(any(CuentaBancaria.class))).thenReturn(cuenta);
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2-ACT (depositar 50)
        Transaccion resultado = transaccionService.realizarDeposito(cuentaId, new BigDecimal("50.00"));

        // 3-ASSERT (verificar que el saldo subio a 150)
        assertNotNull(resultado);
        assertEquals(new BigDecimal("150.00"), cuenta.getSaldo());
        assertEquals(TipoTransaccion.DEPOSITO, resultado.getTipo());
        assertEquals(EstadoTransaccion.COMPLETADA, resultado.getEstadoTransaccion());
        verify(cuentaBancariaRepository, times(1)).save(cuenta);
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar extraer más dinero del saldo disponible")
    void realizarExtraccion_CuandoSaldoInsuficiente_DebeLanzarExcepcion() {
        // 1-ARRANGE (preparar cuenta con poco saldo)
        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        cuenta.setSaldo(new BigDecimal("50.00"));

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));

        // 2-ACT y 3-ASSERT (extraer 200 debe fallar)
        assertThrows(RuntimeException.class, () -> transaccionService.realizarExtraccion(cuentaId, new BigDecimal("200.00")));
        verify(cuentaBancariaRepository, never()).save(any(CuentaBancaria.class));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debe realizar transferencia descontando del origen y sumando al destino")
    void realizarTransferencia_CuandoDatosSonValidos_DebeActualizarAmbosSaldos() {
        // 1-ARRANGE (preparar dos cuentas)
        UUID origenId = UUID.randomUUID();
        UUID destinoId = UUID.randomUUID();

        CajaDeAhorro origen = new CajaDeAhorro();
        origen.setId(origenId);
        origen.setSaldo(new BigDecimal("500.00"));

        CajaDeAhorro destino = new CajaDeAhorro();
        destino.setId(destinoId);
        destino.setSaldo(new BigDecimal("100.00"));

        when(cuentaBancariaRepository.findById(origenId)).thenReturn(Optional.of(origen));
        when(cuentaBancariaRepository.findById(destinoId)).thenReturn(Optional.of(destino));

        // 2-ACT (transferir 200 de origen a destino)
        transaccionService.realizarTransferencia(origenId, destinoId, new BigDecimal("200.00"));

        // 3-ASSERT (verificar los nuevos saldos: 300 y 300)
        assertEquals(new BigDecimal("300.00"), origen.getSaldo());
        assertEquals(new BigDecimal("300.00"), destino.getSaldo());
        verify(cuentaBancariaRepository, times(1)).save(origen);
        verify(cuentaBancariaRepository, times(1)).save(destino);
        verify(transaccionRepository, times(2)).save(any(Transaccion.class));
    }
}