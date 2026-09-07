package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="cajas_de_ahorro")
@PrimaryKeyJoinColumn(name="cuenta_id")
public class CajaDeAhorro extends CuentaBancaria {
    private Integer cupo_limite;
    private float interes_anual;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    public void calcularInteres(){
    }
}