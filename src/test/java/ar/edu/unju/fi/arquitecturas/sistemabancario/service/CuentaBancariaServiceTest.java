package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.CuentaBancariaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CuentaBancariaServiceTest {

    // 1. Simulacion de repositorios
    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    // 2. Inyeccion del servicio real con los repositorios simulados
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
        cuenta.setSaldo(1500f);
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaBancariaRepository.findByCbu(cuenta.getCbu())).thenReturn(Optional.empty());
        when(cuentaBancariaRepository.findByAlias(cuenta.getAlias())).thenReturn(Optional.empty());
        when(cuentaBancariaRepository.save(any(CuentaBancaria.class))).thenReturn(cuenta);

        // 2-ACT (ejecutar metodo)
        CuentaBancaria resultado = cuentaBancariaService.crearCuenta(cuenta, clienteId);

        // 3-ASSERT (verificar resultados)
        assertNotNull(resultado);
        assertEquals("JUAN.BANCO", resultado.getAlias());
        assertEquals(cliente, resultado.getCliente());
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(cuentaBancariaRepository, times(1)).save(cuenta);
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cuenta si el CBU ya se encuentra registrado")
    void crearCuenta_CuandoCbuDuplicado_DebeLanzarExcepcion() {
        // 1-ARRANGE (preparar datos)
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder().id(clienteId).build();

        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setCbu("0000003100000000000001");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaBancariaRepository.findByCbu(cuenta.getCbu())).thenReturn(Optional.of(cuenta));

        // 2-ACT y 3-ASSERT (verificar que lance la excepcion)
        assertThrows(IllegalArgumentException.class, () -> cuentaBancariaService.crearCuenta(cuenta, clienteId));
        verify(cuentaBancariaRepository, never()).save(any(CuentaBancaria.class));
    }

    @Test
    @DisplayName("Debe retornar la cuenta cuando el ID existe")
    void buscarPorId_CuandoExiste_DebeRetornarCuenta() {
        // 1-ARRANGE (preparar datos)
        UUID cuentaId = UUID.randomUUID();
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);

        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));

        // 2-ACT (ejecutar metodo)
        Optional<CuentaBancaria> resultado = cuentaBancariaService.buscarPorId(cuentaId);

        // 3-ASSERT (verificar resultados)
        assertTrue(resultado.isPresent());
        assertEquals(cuentaId, resultado.get().getId());
        verify(cuentaBancariaRepository, times(1)).findById(cuentaId);
    }
}