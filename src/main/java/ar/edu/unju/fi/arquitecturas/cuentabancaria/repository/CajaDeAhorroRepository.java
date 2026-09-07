package ar.edu.unju.fi.arquitecturas.cuentabancaria.repository;

import ar.edu.unju.fi.arquitecturas.cuentabancaria.model.CajaDeAhorro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CajaDeAhorroRepository extends JpaRepository<CajaDeAhorro, Long> {

    List<CajaDeAhorro> findByInteresAnualGreaterThan(float interesAnual);

    List<CajaDeAhorro> findByIdGreaterThan(Long id);
}
