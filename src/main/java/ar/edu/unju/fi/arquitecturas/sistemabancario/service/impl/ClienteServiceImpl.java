package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.event.ClienteCreadoEvent;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
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
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ClienteResponseDto registrarCliente(ClienteRequestDto dto) {
        if (clienteRepository.findByMail(dto.getMail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el correo: " + dto.getMail());
        }

        // Crear en estado PENDIENTE_ACTIVACION con token de 24 horas
        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .cuil(dto.getCuil())
                .mail(dto.getMail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .estadoCliente(EstadoCliente.PENDIENTE_ACTIVACION)
                .tokenActivacion(UUID.randomUUID().toString())
                .fechaExpiracionToken(LocalDateTime.now().plusHours(24))
                .build();

        Cliente persistido = clienteRepository.save(cliente);

        eventPublisher.publishEvent(new ClienteCreadoEvent(persistido));

        return ClienteResponseDto.builder()
                .id(persistido.getId())
                .nombre(persistido.getNombre())
                .cuil(persistido.getCuil())
                .mail(persistido.getMail())
                .telefono(persistido.getTelefono())
                .direccion(persistido.getDireccion())
                .build();
    }

    @Override
    @Transactional
    public void activarCliente(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de activación no puede estar vacío");
        }

        Cliente cliente = clienteRepository.findByTokenActivacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de activación inválido o inexistente"));

        if (cliente.getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token de activación ha expirado");
        }

        if (cliente.getEstadoCliente() == EstadoCliente.ACTIVO) {
            throw new IllegalArgumentException("El cliente ya se encuentra activo");
        }

        cliente.setEstadoCliente(EstadoCliente.ACTIVO);
        cliente.setTokenActivacion(null); // clear token para evitar reusarlo
        clienteRepository.save(cliente);
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
