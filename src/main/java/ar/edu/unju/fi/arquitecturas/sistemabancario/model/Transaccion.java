package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Time;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Date fecha;
    private Time hora;
    @Column(nullable = false)
    private float monto;
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_cuenta", nullable = false)
    private EstadoTransaccion estado_transaccion;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public void consultarTransaccion() {}

}
