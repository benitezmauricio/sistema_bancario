package ar.edu.unju.fi.arquitecturas.sistemabancario.controller;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping("/transferir")
    public ResponseEntity<TransaccionResponseDto> realizarTransferencia(
            @Valid @RequestBody TransaccionRequestDto request) {

        TransaccionResponseDto respuesta = transaccionService.realizarTransferencia(request);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}