package ar.edu.unju.fi.arquitecturas.cuentabancaria.repository;

import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByTipo(TipoTransaccion tipo);

    List<Transaccion> findByMontoGreaterThan(float monto);
}
