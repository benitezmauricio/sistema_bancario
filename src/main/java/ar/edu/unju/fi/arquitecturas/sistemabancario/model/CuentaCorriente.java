package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Representa una cuenta corriente bancaria.
 *
 * <p>Extiende {@link CuentaBancaria} con un margen operativo y el costo
 * de comisión correspondiente.</p>
 */
@Entity
@Table(name="cuentas_corrientes")
@PrimaryKeyJoinColumn(name="cuenta_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CuentaCorriente extends CuentaBancaria {
    /** Margen disponible para operar en la cuenta corriente. */
    @Column(nullable = false)
    private Float margen;

    /** Costo de comisión asociado a la cuenta corriente. */
    @Column(name = "costo_comision", nullable = false)
    private Float costoComision;

    /** Calcula la comisión que corresponde aplicar a la cuenta. */
    public void calcularComision(){}

    /** Aplica la comisión calculada a la cuenta corriente. */
    public void aplicarComision(){}
}
