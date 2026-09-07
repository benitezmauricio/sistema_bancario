package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Time;
import java.time.Instant;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private Date fecha;
    private Time hora;
    @Column(nullable = false)
    private float monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_transaccion", nullable = false)
    private EstadoTransaccion estadoTransaccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CuentaBancaria cuenta;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public void consultarTransaccion() {}

}
