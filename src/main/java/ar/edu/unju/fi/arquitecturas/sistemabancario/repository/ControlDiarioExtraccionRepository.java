package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.ControlDiarioExtraccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ControlDiarioExtraccionRepository extends JpaRepository<ControlDiarioExtraccion, UUID> {

    Optional<ControlDiarioExtraccion> findByClienteIdAndFecha(UUID clienteId, LocalDate fecha);
}
