package ar.edu.unju.fi.arquitecturas.sistemabancario.model;

/**
 * Estados posibles de una cuenta bancaria.
 */
public enum EstadoCuenta {
    /** La cuenta está habilitada para operar. */
    ACTIVA,
    /** La cuenta está temporalmente impedida de operar. */
    SUSPENDIDA,
    /** La cuenta está bloqueada y no puede operar. */
    BLOQUEADA;
}
