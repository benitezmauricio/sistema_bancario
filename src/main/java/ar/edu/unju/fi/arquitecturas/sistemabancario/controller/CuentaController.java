package ar.edu.unju.fi.arquitecturas.sistemabancario.controller;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaUpdateDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaBancariaService cuentaService;

    /**
     * Registra una nueva cuenta bancaria en el sistema.
     */
    @PostMapping
    public ResponseEntity<CuentaResponseDto> crearCuenta(@Valid @RequestBody CuentaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crearCuenta(request));
    }

    /**
     * Lista cuentas bancarias con paginación, opcionalmente filtradas por estado.
     */
    @GetMapping
    public ResponseEntity<Page<CuentaResponseDto>> listarCuentas(
            @RequestParam(required = false) EstadoCuenta estado,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(cuentaService.listarCuentas(estado, pageable));
    }

    /**
     * Obtiene una cuenta bancaria individual por su ID único (UUID).
     */
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cuentaService.buscarDetallePorId(id));
    }

    /**
     * Obtiene una cuenta bancaria individual por su CBU (22 dígitos).
     */
    @GetMapping("/cbu/{cbu}")
    public ResponseEntity<CuentaResponseDto> buscarPorCbu(@PathVariable String cbu) {
        return ResponseEntity.of(cuentaService.buscarDetallePorCbu(cbu));
    }

    /**
     * Obtiene una cuenta bancaria individual por su Alias.
     */
    @GetMapping("/alias/{alias}")
    public ResponseEntity<CuentaResponseDto> buscarPorAlias(@PathVariable String alias) {
        return ResponseEntity.ok(cuentaService.buscarDetallePorAlias(alias));
    }

    /**
     * Modifica parcialmente una cuenta bancaria por su ID.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> actualizarPorId(
            @PathVariable UUID id,
            @Valid @RequestBody CuentaUpdateDto dto) {
        return ResponseEntity.ok(cuentaService.actualizarCuentaPorId(id, dto));
    }

    /**
     * Modifica parcialmente una cuenta bancaria por su CBU.
     */
    @PatchMapping("/cbu/{cbu}")
    public ResponseEntity<CuentaResponseDto> actualizarPorCbu(
            @PathVariable String cbu,
            @Valid @RequestBody CuentaUpdateDto dto) {
        return ResponseEntity.ok(cuentaService.actualizarCuentaPorCbu(cbu, dto));
    }

    /**
     * Realiza la baja lógica (Soft Delete) de una cuenta bancaria por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable UUID id) {
        cuentaService.eliminarCuentaPorId(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Realiza la baja lógica (Soft Delete) de una cuenta bancaria por su CBU.
     */
    @DeleteMapping("/cbu/{cbu}")
    public ResponseEntity<Void> eliminarPorCbu(@PathVariable String cbu) {
        cuentaService.eliminarCuentaPorCbu(cbu);
        return ResponseEntity.noContent().build();
    }
}
