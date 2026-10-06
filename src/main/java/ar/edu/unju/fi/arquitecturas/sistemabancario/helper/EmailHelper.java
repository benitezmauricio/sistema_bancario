package ar.edu.unju.fi.arquitecturas.sistemabancario.helper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/* Helper reutilizable para la construcción y despacho de correos en formato HTML */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailHelper {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@banco.com}")
    private String remitente;

    public void enviarCorreoActivacion(String destinatario, String nombre, String token) {
        String urlActivacion = "http://localhost:8080/api/v1/clientes/activar?token=" + token;

        String contenidoHtml = """
            <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px;">
                <h2>¡Bienvenido al Sistema Bancario, %s!</h2>
                <p>Tu solicitud de alta fue registrada con éxito.</p>
                <p>Para activar tu cuenta y comenzar a operar, haz clic en el siguiente enlace:</p>
                <div style="margin: 25px 0;">
                    <a href="%s" style="background-color: #007bff; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                        Activar mi Cuenta
                    </a>
                </div>
                <p style="color: #666; font-size: 12px;">Este enlace tiene una validez de 24 horas.</p>
            </div>
            """.formatted(nombre, urlActivacion);

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Activación de Cuenta - Sistema Bancario");
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);
            log.info("Correo de activación enviado exitosamente a {}", destinatario);
        } catch (Exception e) {
            log.error("Fallo al construir o enviar el correo a {}: {}", destinatario, e.getMessage());
        }
    }
}