package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, UUID> {

    List<Transaccion> findByTipo(TipoTransaccion tipo);

    List<Transaccion> findByMontoGreaterThan(float monto);

    List<Transaccion> findByCuentaBancariaId(UUID cuentaId);
}
