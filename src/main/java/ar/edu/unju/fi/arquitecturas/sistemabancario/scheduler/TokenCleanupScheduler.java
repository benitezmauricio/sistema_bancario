package ar.edu.unju.fi.arquitecturas.sistemabancario.scheduler;

import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TokenActivacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final TokenActivacionRepository tokenActivacionRepository;

    // todas las noches a las 03:00 AM se ejecuta
    @Scheduled(cron = "0 0 3 * * ?")
    public void purgarTokensExpirados() {
        log.info("Iniciando purga automática de tokens de activación vencidos...");
        try {
            tokenActivacionRepository.deleteByFechaExpiracionBefore(LocalDateTime.now());
            log.info("Purga de tokens finalizada correctamente.");
        } catch (Exception e) {
            log.error("Error al purgar los tokens vencidos: {}", e.getMessage(), e);
        }
    }
}