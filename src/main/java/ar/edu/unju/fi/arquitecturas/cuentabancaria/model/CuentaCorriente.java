package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="cuenta_corriente")
@PrimaryKeyJoinColumn(name="cuenta_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaCorriente extends CuentaBancaria {
    @Column(nullable = false)
    private Float margen;

    @Column(nullable = false, name = "costo_comision")
    private Float costoComision;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public void calcularComision(){}
    public void aplicarComision(){}
}
