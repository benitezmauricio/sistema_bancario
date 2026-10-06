package ar.edu.unju.fi.arquitecturas.sistemabancario.exception;

public class LimiteExtraccionExcedidoException extends RuntimeException {
    public LimiteExtraccionExcedidoException(String message) {
        super(message);
    }
}
