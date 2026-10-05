package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.CuentaBancariaCrudServiceImpl;
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
public class CuentaBancariaCrudServiceTest {

    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;

    @InjectMocks
    private CuentaBancariaCrudServiceImpl crudService;

    @Test
    @DisplayName("Debe guardar y retornar la cuenta bancaria")
    void guardar_DebeRetornarCuentaPersistida() {
        CuentaBancaria cuenta = new CajaDeAhorro();
        when(cuentaBancariaRepository.save(cuenta)).thenReturn(cuenta);

        CuentaBancaria resultado = crudService.guardar(cuenta);

        assertNotNull(resultado);
        verify(cuentaBancariaRepository, times(1)).save(cuenta);
    }

    @Test
    @DisplayName("Debe buscar cuenta por ID")
    void buscarPorId_DebeLlamarRepositorio() {
        UUID id = UUID.randomUUID();
        CuentaBancaria cuenta = new CajaDeAhorro();
        cuenta.setId(id);
        when(cuentaBancariaRepository.findById(id)).thenReturn(Optional.of(cuenta));

        Optional<CuentaBancaria> resultado = crudService.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(cuentaBancariaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe eliminar por ID si existe")
    void eliminarPorId_CuandoExiste_DebeLlamarDelete() {
        UUID id = UUID.randomUUID();
        CuentaBancaria cuenta = new CajaDeAhorro();
        cuenta.setId(id);
        when(cuentaBancariaRepository.findById(id)).thenReturn(Optional.of(cuenta));
        doNothing().when(cuentaBancariaRepository).delete(cuenta);

        assertDoesNotThrow(() -> crudService.eliminarPorId(id));
        verify(cuentaBancariaRepository, times(1)).delete(cuenta);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar por ID si no existe")
    void eliminarPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        UUID id = UUID.randomUUID();
        when(cuentaBancariaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> crudService.eliminarPorId(id));
        verify(cuentaBancariaRepository, never()).delete(any());
    }
}
