package ar.edu.unju.fi.arquitecturas.sistemabancario.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
