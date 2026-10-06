package ar.edu.unju.fi.arquitecturas.sistemabancario.event;

import ar.edu.unju.fi.arquitecturas.sistemabancario.helper.EmailHelper;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventListener {

    private final EmailHelper emailHelper;

    @Async
    @EventListener
    public void onClienteCreado(ClienteCreadoEvent event) {
        Cliente cliente = event.getCliente();
        log.info("Recibido evento asíncrono para cliente ID: {}. Iniciando envío de correo...", cliente.getId());
        emailHelper.enviarCorreoActivacion(
                cliente.getMail(),
                cliente.getNombre(),
                event.getToken()
        );
    }
}