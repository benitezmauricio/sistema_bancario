package ar.edu.unju.fi.arquitecturas.sistemabancario.controller;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaBancariaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponseDto> crearCuenta(@Valid @RequestBody CuentaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crearCuenta(request));
    }

    @GetMapping("/{cbu}")
    public ResponseEntity<CuentaResponseDto> buscarPorCbu(@PathVariable String cbu) {
        return ResponseEntity.of(cuentaService.buscarDetallePorCbu(cbu));
    }
}
