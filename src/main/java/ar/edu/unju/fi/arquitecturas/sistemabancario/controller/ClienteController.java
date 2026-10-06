package ar.edu.unju.fi.arquitecturas.sistemabancario.controller;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDto> registrarCliente(@Valid @RequestBody ClienteRequestDto request) {
        ClienteResponseDto response = clienteService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/activar")
    public ResponseEntity<Map<String, String>> activarCliente(@RequestParam String token) {
        clienteService.activarCliente(token);
        return ResponseEntity.ok(Map.of("mensaje", "Cliente activado con éxito"));
    }
}