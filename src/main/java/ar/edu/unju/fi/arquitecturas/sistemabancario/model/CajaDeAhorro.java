package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

/**
 * Representa una caja de ahorro bancaria.
 *
 * <p>Especialización de {@link CuentaBancaria} que incorpora
 * información sobre el cupo límite y el interés anual. Se almacena
 * en la tabla unificada {@code cuentas_bancarias}.</p>
 */
@Entity
@DiscriminatorValue("CAJA_DE_AHORRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CajaDeAhorro extends CuentaBancaria {
    /** Límite operativo de la caja de ahorro. */
    @Column(name = "cupo_limite")
    private Integer cupoLimite;

    /** Tasa de interés anual aplicable a la cuenta. */
    @Column(name = "interes_anual")
    private Float interesAnual;
}
