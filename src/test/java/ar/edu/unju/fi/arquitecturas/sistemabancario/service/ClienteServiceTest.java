package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.event.ClienteCreadoEvent;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.TokenActivacion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TokenActivacionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    // Simulación de repositorios y publicador de eventos
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TokenActivacionRepository tokenActivacionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    // Inyección del servicio real con las dependencias simuladas
    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    @DisplayName("Debe retornar un cliente cuando el ID existe")
    void buscarPorId_CuandoClienteExiste_DebeRetornarCliente() {
        // 1. PREPARAR (Arrange): Definir datos de prueba y comportamiento de los mocks
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

        // 2. EJECUTAR (Act): Llamar al método bajo prueba
        Cliente resultado = clienteService.buscarPorId(clienteId).orElse(null);

        // 3. VERIFICAR (Assert): Comprobar que los resultados coincidan con lo esperado
        assertNotNull(resultado, "El cliente retornado no debería ser nulo");
        assertEquals(clienteId, resultado.getId());
        assertEquals("José Zapana", resultado.getNombre());
        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el cliente por ID no existe")
    void buscarPorId_CuandoNoExiste_DebeRetornarVacio() {
        // 1. PREPARAR (Arrange)
        UUID idInexistente = UUID.randomUUID();
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // 2. EJECUTAR (Act)
        Optional<Cliente> resultado = clienteService.buscarPorId(idInexistente);

        // 3. VERIFICAR (Assert)
        assertTrue(resultado.isEmpty());
        verify(clienteRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Debe guardar un cliente, generar su token en tabla separada y disparar evento")
    void registrarCliente_CuandoDatosSonValidos_DebeRetornarClienteGuardado() {
        // 1. PREPARAR (Arrange)
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
                .estadoCliente(EstadoCliente.PENDIENTE_ACTIVACION)
                .build();

        when(clienteRepository.findByMail(requestDto.getMail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePersistido);

        // 2. EJECUTAR (Act)
        ClienteResponseDto resultado = clienteService.registrarCliente(requestDto);

        // 3. VERIFICAR (Assert)
        assertNotNull(resultado);
        assertEquals(clienteId, resultado.getId());
        assertEquals("Carlos López", resultado.getNombre());

        // Se comprueba que se persista el cliente, el token independiente y se despache el evento
        verify(clienteRepository, times(1)).findByMail(requestDto.getMail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(tokenActivacionRepository, times(1)).save(any(TokenActivacion.class));
        verify(eventPublisher, times(1)).publishEvent(any(ClienteCreadoEvent.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el Email ya se encuentra registrado")
    void registrarCliente_CuandoEmailDuplicado_DebeLanzarExcepcion() {
        // 1. PREPARAR (Arrange)
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

        // 2. EJECUTAR (Act) y 3. VERIFICAR (Assert)
        assertThrows(IllegalArgumentException.class, () -> clienteService.registrarCliente(requestDto));
        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(tokenActivacionRepository, never()).save(any(TokenActivacion.class));
    }

    @Test
    @DisplayName("Debe activar al cliente y eliminar el token usado")
    void activarCliente_CuandoTokenEsValido_DebeActivarYEliminarToken() {
        // 1. PREPARAR (Arrange)
        String tokenStr = "token-valido-123";
        Cliente clientePendiente = Cliente.builder()
                .id(UUID.randomUUID())
                .estadoCliente(EstadoCliente.PENDIENTE_ACTIVACION)
                .build();

        TokenActivacion tokenActivacion = TokenActivacion.builder()
                .token(tokenStr)
                .cliente(clientePendiente)
                .fechaExpiracion(LocalDateTime.now().plusHours(24))
                .build();

        when(tokenActivacionRepository.findByToken(tokenStr)).thenReturn(Optional.of(tokenActivacion));

        // 2. EJECUTAR (Act)
        clienteService.activarCliente(tokenStr);

        // 3. VERIFICAR (Assert)
        assertEquals(EstadoCliente.ACTIVO, clientePendiente.getEstadoCliente());
        verify(clienteRepository, times(1)).save(clientePendiente);
        verify(tokenActivacionRepository, times(1)).delete(tokenActivacion);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el token ha expirado")
    void activarCliente_CuandoTokenExpirado_DebeLanzarExcepcion() {
        // 1. PREPARAR (Arrange)
        String tokenStr = "token-expirado";
        Cliente clientePendiente = Cliente.builder()
                .id(UUID.randomUUID())
                .estadoCliente(EstadoCliente.PENDIENTE_ACTIVACION)
                .build();

        TokenActivacion tokenVencido = TokenActivacion.builder()
                .token(tokenStr)
                .cliente(clientePendiente)
                .fechaExpiracion(LocalDateTime.now().minusHours(1))
                .build();

        when(tokenActivacionRepository.findByToken(tokenStr)).thenReturn(Optional.of(tokenVencido));

        // 2. EJECUTAR (Act) y 3. VERIFICAR (Assert)
        assertThrows(IllegalArgumentException.class, () -> clienteService.activarCliente(tokenStr));
        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(tokenActivacionRepository, never()).delete(any(TokenActivacion.class));
    }

    @Test
    @DisplayName("Debe eliminar un cliente cuando el ID existe")
    void eliminarCliente_CuandoIdExiste_DebeLlamarDeleteById() {
        // 1. PREPARAR (Arrange)
        UUID clienteId = UUID.randomUUID();
        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(clienteId);

        // 2. EJECUTAR (Act)
        clienteService.eliminarCliente(clienteId);

        // 3. VERIFICAR (Assert)
        verify(clienteRepository, times(1)).existsById(clienteId);
        verify(clienteRepository, times(1)).deleteById(clienteId);
    }
}