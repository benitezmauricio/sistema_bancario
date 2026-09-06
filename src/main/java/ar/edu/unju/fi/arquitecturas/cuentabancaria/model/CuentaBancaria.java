package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@Entity
@Table(name = "cuentas_bancarias")
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
    private EstadoCuenta estado_cuenta;

    public void deposito(){
    }
    public void extraccion(){
    }
    public void transferencia_Enviada(){
    }
    public void transferencia_Recibida(){
    }

}
