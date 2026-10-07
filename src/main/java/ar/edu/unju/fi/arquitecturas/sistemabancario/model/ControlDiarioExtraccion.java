package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Registra el acumulado diario de operaciones por cliente para validar topes
 * diarios de forma optimizada O(1) sin realizar consultas de agregación sobre transacciones históricas.
 */
@Entity
@Table(
        name = "control_diario_extracciones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_control_cliente_fecha",
                        columnNames = {"cliente_id", "fecha"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControlDiarioExtraccion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Cliente al que pertenece el acumulado diario. Mapeado EAGER por @SoftDelete en Cliente. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Fecha correspondiente a la jornada operativa. */
    @Column(nullable = false)
    private LocalDate fecha;

    /** Monto acumulado extraído durante la fecha indicada. */
    @Column(name = "monto_acumulado", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal montoAcumulado = BigDecimal.ZERO;
}
