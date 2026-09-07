package ar.edu.unju.fi.arquitecturas.cuentabancaria.repository;

import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {

    Optional<CuentaBancaria> findByEstadoCuenta(EstadoCuenta estadoCuenta);

    List<CuentaBancaria> findByAliasContainingIgnoreCase(String alias);
}
