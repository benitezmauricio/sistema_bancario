package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CajaDeAhorro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CajaDeAhorroRepository extends JpaRepository<CajaDeAhorro, UUID> {

    List<CajaDeAhorro> findByInteresAnualGreaterThan(Float interesAnual);

}
