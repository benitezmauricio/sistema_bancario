package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, UUID> {

    Optional<CuentaBancaria> findByEstadoCuenta(EstadoCuenta estadoCuenta);
    Page<CuentaBancaria> findByEstadoCuenta(EstadoCuenta estadoCuenta, Pageable pageable);

    List<CuentaBancaria> findByAliasContainingIgnoreCase(String alias);

    Optional<CuentaBancaria> findByCbu(String cbu);
    Optional<CuentaBancaria> findByAlias(String alias);
    List<CuentaBancaria> findByTitularId(UUID titularId);

    // buscar cuentas donde un cliente figure como cotitular
    List<CuentaBancaria> findByCotitulares_Id(UUID cotitularId);
}
