package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, UUID> {

    // Encontrar cuentas corrientes con margen menor a un valor dado
    List<CuentaCorriente> findByMargenLessThan(Float margen);

    List<CuentaCorriente> findByAliasContainingIgnoreCase(String alias);
}
