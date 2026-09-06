package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

@Entity
@Table(name="cajas_de_ahorro")
@PrimaryKeyJoinColumn(name="cuenta_id")
public class CajaDeAhorro extends CuentaBancaria {
    private Integer cupo_limite;
    private float interes_anual;

    public void calcularInteres(){
    }
}