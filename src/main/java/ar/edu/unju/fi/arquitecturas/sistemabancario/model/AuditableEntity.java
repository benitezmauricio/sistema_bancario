package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Superclase abstracta que provee los atributos y listeners de auditoría JPA
 * reutilizables para todas las entidades del modelo de dominio.
 * <p>
 * Al estar anotada con {@link MappedSuperclass}, hereda sus mapeos a las subclases
 * sin generar una tabla física independiente en la base de datos.
 * </p>
 **/

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditableEntity {
    /**
     * Fecha y hora en la que la entidad fue persistida por primera vez.
     * Gestionado automáticamente por Spring Data JPA.
     */
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación realizada sobre la entidad.
     * Actualizado automáticamente en cada operación de actualización.
     */
    @LastModifiedDate
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;
}
