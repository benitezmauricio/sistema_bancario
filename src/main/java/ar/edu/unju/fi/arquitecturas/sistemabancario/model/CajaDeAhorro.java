package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Representa una caja de ahorro bancaria.
 *
 * <p>Es una especialización de {@link CuentaBancaria} que incorpora
 * información sobre el cupo límite y el interés anual.</p>
 */
@Entity
@Table(name="cajas_ahorro")
@PrimaryKeyJoinColumn(name="cuenta_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class CajaDeAhorro extends CuentaBancaria {
    /** Límite operativo de la caja de ahorro. */
    @Column(nullable = false, name = "cupo_limite")
    private Integer cupoLimite;

    /** Tasa de interés anual aplicable a la cuenta. */
    @Column(nullable = false, name = "interes_anual")
    private Float interesAnual;


    /** Calcula el interés generado por la caja de ahorro. */
    public void calcularInteres(){}
}