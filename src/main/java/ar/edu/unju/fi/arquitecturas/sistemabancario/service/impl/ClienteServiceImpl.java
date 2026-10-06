package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.event.ClienteCreadoEvent;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.TokenActivacion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TokenActivacionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final TokenActivacionRepository tokenActivacionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ClienteResponseDto registrarCliente(ClienteRequestDto dto) {
        if (clienteRepository.findByMail(dto.getMail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el correo: " + dto.getMail());
        }

        Cliente titular = null;
        if (dto.getTitularId() != null) {
            titular = clienteRepository.findById(dto.getTitularId())
                    .orElseThrow(() -> new IllegalArgumentException("No existe el cliente titular con ID: " + dto.getTitularId()));
            if (dto.getParentesco() == null) {
                throw new IllegalArgumentException("El parentesco es obligatorio para registrar un adherente");
            }
        }

        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .cuil(dto.getCuil())
                .mail(dto.getMail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .estadoCliente(EstadoCliente.PENDIENTE_ACTIVACION)
                .titular(titular)
                .parentesco(dto.getParentesco())
                .build();

        Cliente persistido = clienteRepository.save(cliente);

        String tokenString = UUID.randomUUID().toString();
        TokenActivacion tokenActivacion = TokenActivacion.builder()
                .token(tokenString)
                .cliente(persistido)
                .fechaExpiracion(LocalDateTime.now().plusHours(24))
                .build();
        tokenActivacionRepository.save(tokenActivacion);

        eventPublisher.publishEvent(new ClienteCreadoEvent(persistido, tokenString));

        return ClienteResponseDto.builder()
                .id(persistido.getId())
                .nombre(persistido.getNombre())
                .cuil(persistido.getCuil())
                .mail(persistido.getMail())
                .telefono(persistido.getTelefono())
                .direccion(persistido.getDireccion())
                .parentesco(persistido.getParentesco())
                .titularId(persistido.getTitular() != null ? persistido.getTitular().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public void activarCliente(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de activación no puede estar vacío");
        }

        TokenActivacion tokenActivacion = tokenActivacionRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de activación inválido o inexistente"));

        if (tokenActivacion.estaExpirado()) {
            throw new IllegalArgumentException("El token de activación ha expirado");
        }

        Cliente cliente = tokenActivacion.getCliente();
        if (cliente.getEstadoCliente() == EstadoCliente.ACTIVO) {
            throw new IllegalArgumentException("El cliente ya se encuentra activo");
        }

        cliente.setEstadoCliente(EstadoCliente.ACTIVO);
        clienteRepository.save(cliente);

        tokenActivacionRepository.delete(tokenActivacion);
    }

    //implementar con red only--
    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> buscarPorMail(String mail) {
        return clienteRepository.findByMail(mail);
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(UUID id, Cliente clienteActualizado) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el cliente con ID: " + id));

        if (!existente.getMail().equalsIgnoreCase(clienteActualizado.getMail())
                && clienteRepository.findByMail(clienteActualizado.getMail()).isPresent()) {
            throw new IllegalArgumentException("El correo ya se encuentra en uso: " + clienteActualizado.getMail());
        }

        existente.setNombre(clienteActualizado.getNombre());
        existente.setCuil(clienteActualizado.getCuil());
        existente.setMail(clienteActualizado.getMail());
        existente.setTelefono(clienteActualizado.getTelefono());
        existente.setDireccion(clienteActualizado.getDireccion());
        existente.setParentesco(clienteActualizado.getParentesco());
        existente.setTitular(clienteActualizado.getTitular());

        return clienteRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminarCliente(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar: no existe cliente con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
