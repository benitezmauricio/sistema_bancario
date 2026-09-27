package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.ClienteServiceImpl;
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
public class ClienteServiceTest {

    // 1. Simulacion del repositorio
    @Mock
    private ClienteRepository clienteRepository;

    // 2. Inyeccion del servicio real con los repositorios simulados
    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    @DisplayName("Debe retornar un cliente cuando el ID existe")
    void buscarPorId_CuandoClienteExiste_DebeRetornarCliente() {
        // 1-ARRANGE
        UUID clienteId = UUID.randomUUID();
        Cliente clienteEsperado = Cliente.builder()
                .id(clienteId)
                .nombre("José Zapana")
                .cuil("20351234")
                .mail("jose@email.com")
                .telefono("12345678")
                .direccion("Calle Falsa 123")
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEsperado));

        // 2-ACT
        Cliente resultado = clienteService.buscarPorId(clienteId).orElse(null);

        // 3-ASSERT
        assertNotNull(resultado, "El cliente retornado no debería ser nulo");
        assertEquals(clienteId, resultado.getId());
        assertEquals("José Zapana", resultado.getNombre());
        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el cliente por ID no existe")
    void buscarPorId_CuandoNoExiste_DebeRetornarVacio() {
        // 1-ARRANGE
        UUID idInexistente = UUID.randomUUID();
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // 2-ACT
        Optional<Cliente> resultado = clienteService.buscarPorId(idInexistente);

        // 3-ASSERT
        assertTrue(resultado.isEmpty());
        verify(clienteRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Debe guardar un cliente cuando los datos son válidos y no duplicados")
    void registrarCliente_CuandoDatosSonValidos_DebeRetornarClienteGuardado() {
        // 1-ARRANGE
        ClienteRequestDto requestDto = ClienteRequestDto.builder()
                .nombre("Carlos López")
                .cuil("20323334")
                .mail("carlos@email.com")
                .telefono("12345678")
                .direccion("Calle Falsa 123")
                .build();

        UUID clienteId = UUID.randomUUID();
        Cliente clientePersistido = Cliente.builder()
                .id(clienteId)
                .nombre("Carlos López")
                .cuil("20323334")
                .mail("carlos@email.com")
                .telefono("12345678")
                .direccion("Calle Falsa 123")
                .build();

        when(clienteRepository.findByMail(requestDto.getMail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePersistido);

        // 2-ACT
        ClienteResponseDto resultado = clienteService.registrarCliente(requestDto);

        // 3-ASSERT
        assertNotNull(resultado);
        assertEquals(clienteId, resultado.getId());
        assertEquals("Carlos López", resultado.getNombre());
        verify(clienteRepository, times(1)).findByMail(requestDto.getMail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el Email ya se encuentra registrado")
    void registrarCliente_CuandoEmailDuplicado_DebeLanzarExcepcion() {
        // 1-ARRANGE
        ClienteRequestDto requestDto = ClienteRequestDto.builder()
                .nombre("Carlos López")
                .cuil("20323334")
                .mail("carlos@email.com")
                .telefono("12345678")
                .direccion("Calle Falsa 123")
                .build();

        Cliente clienteExistente = Cliente.builder()
                .id(UUID.randomUUID())
                .mail("carlos@email.com")
                .build();

        when(clienteRepository.findByMail(requestDto.getMail())).thenReturn(Optional.of(clienteExistente));

        // 2-ACT y 3-ASSERT
        assertThrows(IllegalArgumentException.class, () -> clienteService.registrarCliente(requestDto));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}