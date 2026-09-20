package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

/**
 * Tipos de operaciones que pueden registrarse en una cuenta bancaria.
 */
public enum TipoTransaccion {
    /** Ingreso de dinero a la cuenta. */
    DEPOSITO,
    /** Retiro de dinero de la cuenta. */
    EXTRACCION,
    /** Transferencia enviada desde la cuenta. */
    TRANSFERENCIA_ENVIADA,
    /** Transferencia recibida en la cuenta. */
    TRANSFERENCIA_RECIBIDA;
}
