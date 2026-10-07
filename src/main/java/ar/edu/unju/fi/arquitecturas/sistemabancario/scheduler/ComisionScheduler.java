package ar.edu.unju.fi.arquitecturas.sistemabancario.scheduler;

import ar.edu.unju.fi.arquitecturas.sistemabancario.service.ParametroGlobalService;
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
 * debitando los montos fijos de mantenimiento en todas las cuentas activas
 * obtenidos dinámicamente de la tabla de parámetros globales (con fallback a la configuración local).
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComisionScheduler {

    private final TransaccionService transaccionService;
    private final ParametroGlobalService parametroGlobalService;

    @Value("${app.comisiones.caja-ahorro:2000.00}")
    private BigDecimal comisionCajaAhorroDefault;

    @Value("${app.comisiones.cuenta-corriente:5000.00}")
    private BigDecimal comisionCuentaCorrienteDefault;

    /** Ejecuta el proceso masivo de débito de comisiones en base al cron configurado. */
    @Scheduled(cron = "${app.comisiones.cron}")
    public void ejecutarLiquidacionComisiones() {
        log.info("Iniciando débito masivo mensual de comisiones...");
        try {
            BigDecimal comisionAhorro = parametroGlobalService.getBigDecimal(
                    "COMISION_CAJA_AHORRO", comisionCajaAhorroDefault);
            BigDecimal comisionCorriente = parametroGlobalService.getBigDecimal(
                    "COMISION_CUENTA_CORRIENTE", comisionCuentaCorrienteDefault);

            log.info("Montos aplicados: Caja de Ahorro = ${}, Cuenta Corriente = ${}",
                    comisionAhorro, comisionCorriente);

            transaccionService.procesarDebitoComisionesMasivo(comisionAhorro, comisionCorriente);
            log.info("Débito masivo de comisiones finalizado exitosamente.");
        } catch (Exception e) {
            log.error("Error al procesar el débito masivo de comisiones: {}", e.getMessage(), e);
        }
    }
}
