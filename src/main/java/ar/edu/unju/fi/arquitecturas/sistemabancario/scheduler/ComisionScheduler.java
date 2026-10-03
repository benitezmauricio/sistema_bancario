package ar.edu.unju.fi.arquitecturas.sistemabancario.scheduler;

import ar.edu.unju.fi.arquitecturas.sistemabancario.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Tarea programada encargada de la liquidación mensual automática de comisiones.
 * <p>
 * Se ejecuta según la expresión cron parametrizada en la configuración,
 * debitando los montos fijos de mantenimiento en todas las cuentas activas.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComisionScheduler {

    private final TransaccionService transaccionService;

    @Value("${app.comisiones.caja-ahorro}")
    private BigDecimal comisionCajaAhorro;

    @Value("${app.comisiones.cuenta-corriente}")
    private BigDecimal comisionCuentaCorriente;

    /** Ejecuta el proceso masivo de débito de comisiones en base al cron configurado. */
    @Scheduled(cron = "${app.comisiones.cron}")
    public void ejecutarLiquidacionComisiones() {
        log.info("Iniciando débito masivo mensual de comisiones...");
        try {
            transaccionService.procesarDebitoComisionesMasivo(comisionCajaAhorro, comisionCuentaCorriente);
            log.info("Débito masivo de comisiones finalizado exitosamente.");
        } catch (Exception e) {
            log.error("Error al procesar el débito masivo de comisiones: {}", e.getMessage(), e);
        }
    }
}