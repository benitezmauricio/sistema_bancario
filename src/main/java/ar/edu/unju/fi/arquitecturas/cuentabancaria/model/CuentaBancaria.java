package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cuenta_bancaria")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer cbu;
    @Column(nullable = false, unique = true, length=50)
    private String alias;
    @Column(nullable = false)
    private float saldo;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_cuenta", nullable = false)
    private EstadoCuenta estadoCuenta;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @OneToMany(
            mappedBy = "cuenta",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(
            mappedBy = "cuenta",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Transaccion> transacciones = new ArrayList<>();

    public void deposito(){
    }
    public void extraccion(){
    }
    public void transferencia_Enviada(){
    }
    public void transferencia_Recibida(){
    }

}
