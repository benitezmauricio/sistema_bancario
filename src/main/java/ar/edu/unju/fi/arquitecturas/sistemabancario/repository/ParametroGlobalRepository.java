package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.ParametroGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametroGlobalRepository extends JpaRepository<ParametroGlobal, String> {

    Optional<ParametroGlobal> findByClaveAndActivoTrue(String clave);

    List<ParametroGlobal> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);
}
