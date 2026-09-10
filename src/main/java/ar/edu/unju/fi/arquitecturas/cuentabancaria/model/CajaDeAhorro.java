package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="caja_ahorro")
@PrimaryKeyJoinColumn(name="cuenta_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CajaDeAhorro extends CuentaBancaria {
    @Column(nullable = false, name = "cupo_limite")
    private Integer cupoLimite;

    @Column(nullable = false, name = "interes_anual")
    private Float interesAnual;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public void calcularInteres(){}
}