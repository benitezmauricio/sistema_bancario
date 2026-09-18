package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public Cliente registrarCliente(Cliente cliente) {
        if (clienteRepository.findByMail(cliente.getMail()).isPresent()){
            throw new IllegalArgumentException("Ya existe un cliente registrado con el correo: " + cliente.getMail());
        }
        return clienteRepository.save(cliente);
    }

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
