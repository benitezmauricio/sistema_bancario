package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Clase base abstracta para las cuentas bancarias del sistema.
 *
 * <p>Utiliza herencia JPA mediante la estrategia {@link InheritanceType#JOINED}.
 * Sus subclases representan los distintos tipos de cuenta disponibles.</p>
 */
@Entity
@Table(name = "cuentas_bancarias")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public abstract class CuentaBancaria extends AuditableEntity{
    /** Identificador único de la cuenta bancaria. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Código Bancario Uniforme de la cuenta. */
    @Column(nullable = false, unique = true, length=22)
    private String cbu;

    /** Alias único utilizado para identificar la cuenta. */
    @Column(nullable = false, unique = true, length=50)
    private String alias;

    /** Saldo disponible de la cuenta. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    /** Estado actual de la cuenta bancaria. */
    @Enumerated(EnumType.STRING)
    @Column(name="estado_cuenta", nullable = false, length = 20)
    private EstadoCuenta estadoCuenta;

    /** Cliente titular de la cuenta bancaria. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Realiza un depósito en la cuenta. */
    public void depositar(){}

    /** Realiza una extracción de la cuenta. */
    public void extraer(){}

    /** Realiza una transferencia desde la cuenta. */
    public void transferir(){}

}
