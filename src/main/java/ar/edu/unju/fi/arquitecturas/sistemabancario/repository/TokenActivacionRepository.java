package ar.edu.unju.fi.arquitecturas.sistemabancario.repository;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.TokenActivacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenActivacionRepository extends JpaRepository<TokenActivacion, UUID> {
    Optional<TokenActivacion> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByFechaExpiracionBefore(LocalDateTime fechaLimite);
}