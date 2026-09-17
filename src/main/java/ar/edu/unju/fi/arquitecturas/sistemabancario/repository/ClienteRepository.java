package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByMail(String mail);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}
