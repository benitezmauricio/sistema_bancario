package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.Parentesco;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa a un cliente del sistema bancario.
 *
 * <p>Un cliente puede poseer una o más cuentas bancarias, ser titular,
 * tener adherentes vinculados (grupo familiar) o actuar como adherente de otro titular.</p>
 */
@Entity
@Table(name = "clientes")
/* Activa el borrado lógico en la tabla clientes */
@SoftDelete(columnName = "fecha_baja", strategy = SoftDeleteType.TIMESTAMP)
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

    /**
     * Cliente titular al que se vincula este cliente si es adherente.
     * Es null si el cliente es titular independiente.
     * Se mapea con FetchType.EAGER debido a que Cliente tiene habilitado @SoftDelete.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "titular_id")
    private Cliente titular;

    /**
     * Lista de adherentes (cónyuge, hijos) vinculados a este titular.
     */
    @Builder.Default
    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cliente> adherentes = new ArrayList<>();

    /**
     * Vínculo de parentesco con el titular (CONYUGE, HIJO).
     * Aplica únicamente si el cliente es un adherente.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "parentesco", length = 20)
    private Parentesco parentesco;
}
