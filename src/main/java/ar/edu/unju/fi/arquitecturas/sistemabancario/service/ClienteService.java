package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteService {
    ClienteResponseDto registrarCliente(ClienteRequestDto dto);
    Optional<Cliente> buscarPorId(UUID id);
    Optional<Cliente> buscarPorMail(String mail);
    List<Cliente> buscarPorNombre(String nombre);
    Cliente actualizarCliente(UUID id, Cliente clienteActualizado);
    void eliminarCliente(UUID id);
}
