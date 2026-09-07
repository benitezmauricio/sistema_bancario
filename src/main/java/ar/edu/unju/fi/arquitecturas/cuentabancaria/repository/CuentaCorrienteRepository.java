package ar.edu.unju.fi.arquitecturas.cuentabancaria.repository;

import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Long> {

    // Encontrar cuentas corrientes con margen menor a un valor dado
    List<CuentaCorriente> findByMargenLessThan(float margen);

    List<CuentaCorriente> findByAliasContainingIgnoreCase(String alias);
}
