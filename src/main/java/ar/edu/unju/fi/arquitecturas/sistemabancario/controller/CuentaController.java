package ar.edu.unju.fi.arquitecturas.sistemabancario.controller;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaUpdateDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.CuentaBancariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Cuentas Bancarias", description = "Endpoints para la gestión, consulta, actualización y baja lógica de cuentas bancarias")
public class CuentaController {

    private final CuentaBancariaService cuentaService;

    /**
     * Registra una nueva cuenta bancaria en el sistema.
     */
    @Operation(summary = "Registrar una nueva cuenta bancaria", description = "Crea y da de alta una nueva cuenta bancaria (Caja de Ahorro o Cuenta Corriente) vinculada a un cliente titular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta bancaria creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o error en las validaciones de negocio", content = @Content),
            @ApiResponse(responseCode = "404", description = "Titular o cotitular no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CuentaResponseDto> crearCuenta(@Valid @RequestBody CuentaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crearCuenta(request));
    }

    /**
     * Lista cuentas bancarias con paginación, opcionalmente filtradas por estado.
     */
    @Operation(summary = "Listar cuentas bancarias", description = "Obtiene un listado paginado de cuentas bancarias, permitiendo filtrar opcionalmente por estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de cuentas obtenido exitosamente")
    })
    @GetMapping
    public ResponseEntity<Page<CuentaResponseDto>> listarCuentas(
            @Parameter(description = "Filtrar por estado de la cuenta (opcional)", example = "ACTIVA")
            @RequestParam(required = false) EstadoCuenta estado,
            @ParameterObject @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(cuentaService.listarCuentas(estado, pageable));
    }

    /**
     * Obtiene una cuenta bancaria individual por su ID único (UUID).
     */
    @Operation(summary = "Buscar cuenta por ID", description = "Devuelve el detalle de una cuenta bancaria a partir de su UUID identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada con el ID provisto", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> buscarPorId(
            @Parameter(description = "Identificador único de la cuenta (UUID)", example = "96d023ba-e140-42dd-9257-dd5cbdd2846e")
            @PathVariable UUID id) {
        return ResponseEntity.ok(cuentaService.buscarDetallePorId(id));
    }

    /**
     * Obtiene una cuenta bancaria individual por su CBU (22 dígitos).
     */
    @Operation(summary = "Buscar cuenta por CBU", description = "Devuelve el detalle de una cuenta bancaria a partir de su CBU de 22 dígitos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada con el CBU provisto", content = @Content)
    })
    @GetMapping("/cbu/{cbu}")
    public ResponseEntity<CuentaResponseDto> buscarPorCbu(
            @Parameter(description = "Clave Bancaria Uniforme (22 dígitos)", example = "0000003100000000000099")
            @PathVariable String cbu) {
        return ResponseEntity.of(cuentaService.buscarDetallePorCbu(cbu));
    }

    /**
     * Obtiene una cuenta bancaria individual por su Alias.
     */
    @Operation(summary = "Buscar cuenta por Alias", description = "Devuelve el detalle de una cuenta bancaria a partir de su Alias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada con el Alias provisto", content = @Content)
    })
    @GetMapping("/alias/{alias}")
    public ResponseEntity<CuentaResponseDto> buscarPorAlias(
            @Parameter(description = "Alias único de la cuenta", example = "PRUEBA.SWAGGER.ARS")
            @PathVariable String alias) {
        return ResponseEntity.ok(cuentaService.buscarDetallePorAlias(alias));
    }

    /**
     * Modifica parcialmente una cuenta bancaria por su ID.
     */
    @Operation(summary = "Actualizar cuenta por ID", description = "Modifica parcialmente los datos de una cuenta bancaria a partir de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> actualizarPorId(
            @Parameter(description = "Identificador único de la cuenta (UUID)", example = "96d023ba-e140-42dd-9257-dd5cbdd2846e")
            @PathVariable UUID id,
            @Valid @RequestBody CuentaUpdateDto dto) {
        return ResponseEntity.ok(cuentaService.actualizarCuentaPorId(id, dto));
    }

    /**
     * Modifica parcialmente una cuenta bancaria por su CBU.
     */
    @Operation(summary = "Actualizar cuenta por CBU", description = "Modifica parcialmente los datos de una cuenta bancaria a partir de su CBU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuentaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content)
    })
    @PatchMapping("/cbu/{cbu}")
    public ResponseEntity<CuentaResponseDto> actualizarPorCbu(
            @Parameter(description = "Clave Bancaria Uniforme (22 dígitos)", example = "0000003100000000000099")
            @PathVariable String cbu,
            @Valid @RequestBody CuentaUpdateDto dto) {
        return ResponseEntity.ok(cuentaService.actualizarCuentaPorCbu(cbu, dto));
    }

    /**
     * Realiza la baja lógica (Soft Delete) de una cuenta bancaria por su ID.
     */
    @Operation(summary = "Eliminar cuenta por ID", description = "Efectúa la baja lógica (Soft Delete) de la cuenta bancaria especificada por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada lógicamente con éxito"),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(
            @Parameter(description = "Identificador único de la cuenta (UUID)", example = "96d023ba-e140-42dd-9257-dd5cbdd2846e")
            @PathVariable UUID id) {
        cuentaService.eliminarCuentaPorId(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Realiza la baja lógica (Soft Delete) de una cuenta bancaria por su CBU.
     */
    @Operation(summary = "Eliminar cuenta por CBU", description = "Efectúa la baja lógica (Soft Delete) de la cuenta bancaria especificada por su CBU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada lógicamente con éxito"),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content)
    })
    @DeleteMapping("/cbu/{cbu}")
    public ResponseEntity<Void> eliminarPorCbu(
            @Parameter(description = "Clave Bancaria Uniforme (22 dígitos)", example = "0000003100000000000099")
            @PathVariable String cbu) {
        cuentaService.eliminarCuentaPorCbu(cbu);
        return ResponseEntity.noContent().build();
    }
}