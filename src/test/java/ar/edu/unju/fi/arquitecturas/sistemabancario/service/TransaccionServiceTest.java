package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ExtraccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.LimiteExtraccionExcedidoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.*;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.Parentesco;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ControlDiarioExtraccionRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ControlDiarioExtraccionRepository controlDiarioExtraccionRepository;

    @Mock
    private ParametroGlobalService parametroGlobalService;

    @InjectMocks
    private TransaccionServiceImpl transaccionService;

    @Test
    @DisplayName("Debe realizar depósito incrementando el saldo y guardando la transacción")
    void realizarDeposito_CuandoMontoEsValido_DebeIncrementarSaldo() {
        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        cuenta.setSaldo(new BigDecimal("100.00"));

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaBancariaRepository.save(any(CuentaBancaria.class))).thenReturn(cuenta);
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = transaccionService.realizarDeposito(cuentaId, new BigDecimal("50.00"));

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
        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        cuenta.setSaldo(new BigDecimal("50.00"));

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));

        assertThrows(RuntimeException.class, () -> transaccionService.realizarExtraccion(cuentaId, new BigDecimal("200.00")));
        verify(cuentaBancariaRepository, never()).save(any(CuentaBancaria.class));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debe realizar transferencia descontando del origen y sumando al destino")
    void realizarTransferencia_CuandoDatosSonValidos_DebeActualizarAmbosSaldos() {
        UUID origenId = UUID.randomUUID();
        UUID destinoId = UUID.randomUUID();

        CajaDeAhorro origen = new CajaDeAhorro();
        origen.setId(origenId);
        origen.setSaldo(new BigDecimal("500.00"));

        CajaDeAhorro destino = new CajaDeAhorro();
        destino.setId(destinoId);
        destino.setSaldo(new BigDecimal("100.00"));

        TransaccionRequestDto requestDto = TransaccionRequestDto.builder()
                .cuentaOrigenId(origenId)
                .cuentaDestinoId(destinoId)
                .monto(new BigDecimal("200.00"))
                .build();

        when(cuentaBancariaRepository.findById(origenId)).thenReturn(Optional.of(origen));
        when(cuentaBancariaRepository.findById(destinoId)).thenReturn(Optional.of(destino));

        TransaccionResponseDto respuesta = transaccionService.realizarTransferencia(requestDto);

        assertNotNull(respuesta);
        assertEquals(new BigDecimal("300.00"), origen.getSaldo());
        assertEquals(new BigDecimal("300.00"), destino.getSaldo());
        assertEquals(new BigDecimal("200.00"), respuesta.getMonto());
        verify(cuentaBancariaRepository, times(1)).save(origen);
        verify(cuentaBancariaRepository, times(1)).save(destino);
        verify(transaccionRepository, times(2)).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debe realizar extracción con tope para titular cuando no supera límite global")
    void realizarExtraccionConTope_Titular_CuandoDentroDeLimite_DebeCompletar() {
        UUID titularId = UUID.randomUUID();
        Cliente titular = Cliente.builder().id(titularId).nombre("Juan").build();

        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        cuenta.setTitular(titular);
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);
        cuenta.setSaldo(new BigDecimal("150000.00"));

        ExtraccionRequestDto dto = ExtraccionRequestDto.builder()
                .cuentaId(cuentaId)
                .clienteId(titularId)
                .monto(new BigDecimal("50000.00"))
                .build();

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        when(clienteRepository.findById(titularId)).thenReturn(Optional.of(titular));
        when(parametroGlobalService.getBigDecimal(eq("LIMITE_EXTRACCION_TITULAR"), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("100000.00"));
        when(controlDiarioExtraccionRepository.findByClienteIdAndFecha(eq(titularId), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        TransaccionResponseDto respuesta = transaccionService.realizarExtraccionConTope(dto);

        assertNotNull(respuesta);
        assertEquals(new BigDecimal("100000.00"), cuenta.getSaldo());
        verify(controlDiarioExtraccionRepository, times(1)).save(any(ControlDiarioExtraccion.class));
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debe rechazar extracción cuando el adherente supera su límite global diario ($70.000)")
    void realizarExtraccionConTope_Adherente_CuandoSuperaLimite_DebeLanzarExcepcion() {
        UUID titularId = UUID.randomUUID();
        Cliente titular = Cliente.builder().id(titularId).nombre("Juan").build();

        UUID adherenteId = UUID.randomUUID();
        Cliente adherente = Cliente.builder()
                .id(adherenteId)
                .nombre("Lucas")
                .titular(titular)
                .parentesco(Parentesco.HIJO)
                .build();

        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        cuenta.setTitular(titular);
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);
        cuenta.setSaldo(new BigDecimal("150000.00"));

        ExtraccionRequestDto dto = ExtraccionRequestDto.builder()
                .cuentaId(cuentaId)
                .clienteId(adherenteId)
                .monto(new BigDecimal("80000.00"))
                .build();

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));
        when(clienteRepository.findById(adherenteId)).thenReturn(Optional.of(adherente));
        when(parametroGlobalService.getBigDecimal(eq("LIMITE_EXTRACCION_ADHERENTE"), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("70000.00"));
        when(controlDiarioExtraccionRepository.findByClienteIdAndFecha(eq(adherenteId), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        assertThrows(LimiteExtraccionExcedidoException.class, () ->
                transaccionService.realizarExtraccionConTope(dto));

        verify(cuentaBancariaRepository, never()).save(any(CuentaBancaria.class));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debe procesar débito de comisiones en cuentas activas registrando DEBITO_COMISION")
    void procesarDebitoComisionesMasivo_DebeDescontarSaldoYRegistrarTransaccion() {
        CajaDeAhorro cajaAhorro = new CajaDeAhorro();
        cajaAhorro.setId(UUID.randomUUID());
        cajaAhorro.setSaldo(new BigDecimal("10000.00"));
        cajaAhorro.setEstadoCuenta(EstadoCuenta.ACTIVA);

        CuentaCorriente cuentaCorriente = new CuentaCorriente();
        cuentaCorriente.setId(UUID.randomUUID());
        cuentaCorriente.setSaldo(new BigDecimal("20000.00"));
        cuentaCorriente.setEstadoCuenta(EstadoCuenta.ACTIVA);

        org.springframework.data.domain.Page<CuentaBancaria> paginaMock =
                new org.springframework.data.domain.PageImpl<>(List.of(cajaAhorro, cuentaCorriente));

        when(cuentaBancariaRepository.findByEstadoCuenta(eq(EstadoCuenta.ACTIVA), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(paginaMock);

        BigDecimal comisionAhorro = new BigDecimal("2000.00");
        BigDecimal comisionCorriente = new BigDecimal("5000.00");

        transaccionService.procesarDebitoComisionesMasivo(comisionAhorro, comisionCorriente);

        assertEquals(new BigDecimal("8000.00"), cajaAhorro.getSaldo());
        assertEquals(new BigDecimal("15000.00"), cuentaCorriente.getSaldo());

        verify(cuentaBancariaRepository, times(1)).save(cajaAhorro);
        verify(cuentaBancariaRepository, times(1)).save(cuentaCorriente);
        verify(transaccionRepository, times(2)).save(any(Transaccion.class));
    }
}
