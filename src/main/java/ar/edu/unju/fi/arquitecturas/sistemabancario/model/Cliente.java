package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa a un cliente del sistema bancario.
 *
 * <p>Un cliente puede poseer una o más cuentas bancarias y sus datos
 * de auditoría se heredan de {@link AuditableEntity}.</p>
 */
@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Cliente extends AuditableEntity {

    /** Identificador único del cliente. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Nombre completo del cliente. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Clave Única de Identificación Laboral del cliente. */
    @Column(nullable = false, unique = true, length = 11)
    private String cuil;

    /** Correo electrónico del cliente. */
    @Column(nullable = false, unique = true, length = 100)
    private String mail;

    /** Número telefónico de contacto del cliente. */
    @Column(nullable = false)
    private String telefono;

    /** Domicilio declarado por el cliente. */
    @Column(nullable = false)
    private String direccion;

    /** Cuentas en las que participa como cotitular. */
    @Builder.Default
    @ManyToMany(mappedBy = "cotitulares", fetch = FetchType.LAZY)
    private List<CuentaBancaria> cuentasCotitular = new ArrayList<>();

    /** Registra un nuevo cliente en el sistema. */
    public void registrarCLiente(){}

    /** Registra un cotitular asociado al cliente. */
    public void registrarCotitular(){}
}

