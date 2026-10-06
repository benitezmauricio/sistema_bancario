package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase base abstracta para las cuentas bancarias del sistema.
 *
 * <p>Utiliza herencia JPA mediante la estrategia {@link InheritanceType#SINGLE_TABLE}.
 * Todas las subclases se almacenan en una única tabla {@code cuentas_bancarias}, diferenciadas
 * por la columna discriminadora {@code tipo_cuenta}.</p>
 */
@Entity
@Table(name = "cuentas_bancarias")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cuenta", discriminatorType = DiscriminatorType.STRING, length = 30)
/* Activa el borrado lógico de la tabla **/
@SoftDelete(columnName = "fecha_baja", strategy = SoftDeleteType.TIMESTAMP)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CuentaBancaria extends AuditableEntity {
    /** Identificador único de la cuenta bancaria. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Código Bancario Uniforme de la cuenta. */
    @Column(nullable = false, unique = true, length = 22)
    private String cbu;

    /** Alias único utilizado para identificar la cuenta. */
    @Column(nullable = false, unique = true, length = 50)
    private String alias;

    /** Saldo disponible de la cuenta. */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    /** Estado actual de la cuenta bancaria. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", nullable = false, length = 20)
    private EstadoCuenta estadoCuenta;

    /** Cliente titular principal de la cuenta bancaria. Mapeado EAGER por @SoftDelete en Cliente. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "titular_id", nullable = false)
    private Cliente titular;

    /** Clientes cotitulares adicionales adheridos a la cuenta. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cuentas_cotitulares",
            joinColumns = @JoinColumn(name = "cuenta_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private List<Cliente> cotitulares = new ArrayList<>();

    /** Historial de transacciones de la cuenta. */
    @OneToMany(mappedBy = "cuentaBancaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones = new ArrayList<>();

}
