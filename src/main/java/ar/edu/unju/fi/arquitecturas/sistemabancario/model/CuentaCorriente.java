package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

/**
 * Representa una cuenta corriente bancaria.
 *
 * <p>Extiende {@link CuentaBancaria} con un margen operativo y el costo
 * de comisión correspondiente. Se almacena en la tabla unificada {@code cuentas_bancarias}.</p>
 */
@Entity
@DiscriminatorValue("CUENTA_CORRIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CuentaCorriente extends CuentaBancaria {
    /** Margen disponible para operar en la cuenta corriente. */
    @Column(name = "margen")
    private Float margen;

    /** Costo de comisión asociado a la cuenta corriente. */
    @Column(name = "costo_comision")
    private Float costoComision;

    /** Calcula la comisión que corresponde aplicar a la cuenta. */
    public void calcularComision() {}
}
