package ar.edu.unju.fi.arquitecturas.cuentabancaria.repository;

import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByMail(String mail);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}
