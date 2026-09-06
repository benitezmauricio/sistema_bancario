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
@Table(name="cuentas_corrientes")
@PrimaryKeyJoinColumn(name="cuenta_id")
public class CuentaCorriente extends CuentaBancaria {
    private float margen;
    private float costo_comision;
}
