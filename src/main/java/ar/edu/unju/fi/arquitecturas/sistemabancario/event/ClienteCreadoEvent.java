package ar.edu.unju.fi.arquitecturas.sistemabancario.event;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* Evento disparado tras el registro inicial de un nuevo cliente */
@Getter
@RequiredArgsConstructor
public class ClienteCreadoEvent {
    private final Cliente cliente;
}