package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cuenta_bancaria")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length=22)
    private String cbu;

    @Column(nullable = false, unique = true, length=50)
    private String alias;

    @Column(nullable = false)
    private Float saldo;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_cuenta", nullable = false)
    private EstadoCuenta estadoCuenta;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    //Atributos referidos a relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public void depositar(){}
    public void extraer(){}
    public void transferir(){}

}
