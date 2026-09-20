package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Representa una operación realizada sobre una cuenta bancaria.
 *
 * <p>Registra el importe, el tipo, el estado y los datos temporales
 * de la operación.</p>
 */
@Entity
@Getter
@Setter
@Table(name = "transacciones")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaccion extends AuditableEntity{

    /** Identificador único de la transacción. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Fecha en la que se realizó la transacción. */
    private Date fecha;

    /** Hora en la que se realizó la transacción. */
    private Time hora;

    /** Importe asociado a la transacción. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    /** Tipo de operación realizada. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transaccion", nullable = false, length = 30)
    private TipoTransaccion tipo;

    /** Estado actual de la transacción. */
    @Enumerated(EnumType.STRING)
    @Column(name="estado_transaccion", nullable = false, length = 20)
    private EstadoTransaccion estadoTransaccion;


    /** Cuenta bancaria sobre la que se realizó la operación. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_bancaria_id", nullable = false)
    private CuentaBancaria cuentaBancaria;

    /** Consulta la información de la transacción. */
    public void consultarTransaccion() {}
}
