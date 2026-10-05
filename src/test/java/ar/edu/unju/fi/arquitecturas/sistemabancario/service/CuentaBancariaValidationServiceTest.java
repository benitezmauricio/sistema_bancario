package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.CuentaBancariaValidationServiceImpl;
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
public class CuentaBancariaValidationServiceTest {

    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CuentaBancariaValidationServiceImpl validationService;

    @Test
    @DisplayName("Debe lanzar excepción si el CBU ya existe")
    void validarCbuUnico_CuandoExiste_DebeLanzarExcepcion() {
        String cbu = "0000003100000000000001";
        when(cuentaBancariaRepository.findByCbu(cbu)).thenReturn(Optional.of(new CajaDeAhorro()));

        assertThrows(IllegalArgumentException.class, () -> validationService.validarCbuUnico(cbu));
        verify(cuentaBancariaRepository, times(1)).findByCbu(cbu);
    }

    @Test
    @DisplayName("No debe lanzar excepción si el CBU no existe")
    void validarCbuUnico_CuandoNoExiste_NoDebeLanzarExcepcion() {
        String cbu = "0000003100000000000001";
        when(cuentaBancariaRepository.findByCbu(cbu)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validationService.validarCbuUnico(cbu));
        verify(cuentaBancariaRepository, times(1)).findByCbu(cbu);
    }

    @Test
    @DisplayName("Debe validar y retornar el cliente titular cuando existe")
    void validarYObtenerTitular_CuandoExiste_DebeRetornarCliente() {
        UUID titularId = UUID.randomUUID();
        Cliente titular = Cliente.builder().id(titularId).nombre("Carlos").build();
        when(clienteRepository.findById(titularId)).thenReturn(Optional.of(titular));

        Cliente resultado = validationService.validarYObtenerTitular(titularId);

        assertNotNull(resultado);
        assertEquals(titularId, resultado.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el titular no existe")
    void validarYObtenerTitular_CuandoNoExiste_DebeLanzarExcepcion() {
        UUID titularId = UUID.randomUUID();
        when(clienteRepository.findById(titularId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> validationService.validarYObtenerTitular(titularId));
    }

    @Test
    @DisplayName("Debe validar y retornar la cuenta cuando existe")
    void validarYObtenerCuentaExistente_CuandoExiste_DebeRetornarCuenta() {
        UUID cuentaId = UUID.randomUUID();
        CuentaBancaria cuenta = new CajaDeAhorro();
        cuenta.setId(cuentaId);
        when(cuentaBancariaRepository.findById(cuentaId)).thenReturn(Optional.of(cuenta));

        CuentaBancaria resultado = validationService.validarYObtenerCuentaExistente(cuentaId);

        assertNotNull(resultado);
        assertEquals(cuentaId, resultado.getId());
    }
}
