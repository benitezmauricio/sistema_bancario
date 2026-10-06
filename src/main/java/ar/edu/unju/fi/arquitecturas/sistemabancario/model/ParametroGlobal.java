package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.TipoDatoParametro;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa una configuración o regla de negocio global parametrizada en la base de datos.
 */
@Entity
@Table(name = "parametros_globales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametroGlobal extends AuditableEntity {

    /** Identificador clave único del parámetro (ej: COMISION_CAJA_AHORRO). */
    @Id
    @Column(nullable = false, length = 60)
    private String clave;

    /** Valor almacenado como texto para soportar diferentes tipos de datos. */
    @Column(nullable = false, length = 255)
    private String valor;

    /** Tipo de dato para validar y convertir el valor de forma tipada. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato", nullable = false, length = 20)
    private TipoDatoParametro tipoDato;

    /** Categoría funcional o módulo al que pertenece el parámetro (dinámico y extensible en BD). */
    @Column(nullable = false, length = 50)
    private String categoria;

    /** Unidad de referencia opcional (ej: ARS, USD, %). */
    @Column(length = 20)
    private String unidad;

    /** Descripción detallada del propósito del parámetro. */
    @Column(nullable = false, length = 255)
    private String descripcion;

    /** Indica si el parámetro se encuentra activo y aplicable. */
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
