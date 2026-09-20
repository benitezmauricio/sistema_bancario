package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

/**
 * Estados posibles de una transacción bancaria.
 */
public enum EstadoTransaccion {
    /** La transacción fue iniciada, pero aún no finalizó. */
    PENDIENTE,
    /** La transacción finalizó correctamente. */
    COMPLETADA,
    /** La transacción no pudo ejecutarse. */
    RECHAZADA,
    /** La transacción completada fue anulada posteriormente. */
    REVERTIDA;
}
