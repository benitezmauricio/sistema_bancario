package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.CuentaBancariaServiceImpl;
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
public class CuentaBancariaServiceTest {

    // 1. Simulación de los servicios especializados inyectados
    @Mock
    private CuentaBancariaCrudService crudService;

    @Mock
    private CuentaBancariaValidationService validationService;

    // 2. Inyección del servicio orquestador con sus dependencias simuladas
    @InjectMocks
    private CuentaBancariaServiceImpl cuentaBancariaService;

    @Test
    @DisplayName("Debe crear una cuenta bancaria cuando el cliente existe y los datos son válidos")
    void crearCuenta_CuandoDatosSonValidos_DebeRetornarCuentaGuardada() {
        // 1-ARRANGE (preparar datos)
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .nombre("Juan Pérez")
                .build();

        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setCbu("0000003100000000000001");
        cuenta.setAlias("JUAN.BANCO");
        cuenta.setSaldo(new BigDecimal("1500.00"));
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);

        when(validationService.validarYObtenerTitular(clienteId)).thenReturn(cliente);
        doNothing().when(validationService).validarCbuUnico(cuenta.getCbu());
        doNothing().when(validationService).validarAliasUnico(cuenta.getAlias());
        when(crudService.guardar(any(CuentaBancaria.class))).thenReturn(cuenta);

        // 2-ACT (ejecutar método)
        CuentaBancaria resultado = cuentaBancariaService.crearCuenta(cuenta, clienteId);

        // 3-ASSERT (verificar resultados)
        assertNotNull(resultado);
        assertEquals("JUAN.BANCO", resultado.getAlias());
        assertEquals(cliente, resultado.getTitular());
        verify(validationService, times(1)).validarYObtenerTitular(clienteId);
        verify(validationService, times(1)).validarCbuUnico(cuenta.getCbu());
        verify(validationService, times(1)).validarAliasUnico(cuenta.getAlias());
        verify(crudService, times(1)).guardar(cuenta);
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cuenta si el CBU ya se encuentra registrado")
    void crearCuenta_CuandoCbuDuplicado_DebeLanzarExcepcion() {
        // 1-ARRANGE (preparar datos)
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder().id(clienteId).build();

        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setCbu("0000003100000000000001");

        when(validationService.validarYObtenerTitular(clienteId)).thenReturn(cliente);
        doThrow(new IllegalArgumentException("Ya existe una cuenta con el CBU: " + cuenta.getCbu()))
                .when(validationService).validarCbuUnico(cuenta.getCbu());

        // 2-ACT y 3-ASSERT (verificar que lance la excepción)
        assertThrows(IllegalArgumentException.class, () -> cuentaBancariaService.crearCuenta(cuenta, clienteId));
        verify(crudService, never()).guardar(any(CuentaBancaria.class));
    }

    @Test
    @DisplayName("Debe retornar la cuenta cuando el ID existe")
    void buscarPorId_CuandoExiste_DebeRetornarCuenta() {
        // 1-ARRANGE (preparar datos)
        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);

        when(crudService.buscarPorId(cuentaId)).thenReturn(Optional.of(cuenta));

        // 2-ACT (ejecutar método)
        Optional<CuentaBancaria> resultado = cuentaBancariaService.buscarPorId(cuentaId);

        // 3-ASSERT (verificar resultados)
        assertTrue(resultado.isPresent());
        assertEquals(cuentaId, resultado.get().getId());
        verify(crudService, times(1)).buscarPorId(cuentaId);
    }
}
