package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.ExtraccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionRequestDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.TransaccionResponseDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.LimiteExtraccionExcedidoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.SaldoInsuficienteException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.*;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ControlDiarioExtraccionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.ParametroGlobalService;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final ClienteRepository clienteRepository;
    private final ControlDiarioExtraccionRepository controlDiarioExtraccionRepository;
    private final ParametroGlobalService parametroGlobalService;

    private static final BigDecimal LIMITE_TITULAR_DEFAULT = new BigDecimal("100000.00");
    private static final BigDecimal LIMITE_ADHERENTE_DEFAULT = new BigDecimal("70000.00");

    @Override
    @Transactional
    public Transaccion realizarDeposito(UUID cuentaId, BigDecimal monto) {
        validarMonto(monto, "depositar");

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con id: " + cuentaId));

        if (cuenta.getSaldo() == null) {
            throw new IllegalStateException("La cuenta bancaria no tiene saldo inicializado");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaBancariaRepository.save(cuenta);

        Transaccion transaccion = Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(TipoTransaccion.DEPOSITO)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();

        return transaccionRepository.save(transaccion);
    }

    @Override
    @Transactional
    public Transaccion realizarExtraccion(UUID cuentaId, BigDecimal monto) {
        validarMonto(monto, "extraer");

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con id: " + cuentaId));

        if (cuenta.getSaldo() == null) {
            throw new IllegalStateException("La cuenta bancaria no tiene saldo inicializado");
        }

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar la extracción");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaBancariaRepository.save(cuenta);

        Transaccion transaccion = Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(TipoTransaccion.EXTRACCION)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();

        return transaccionRepository.save(transaccion);
    }

    @Override
    @Transactional
    public TransaccionResponseDto realizarExtraccionConTope(ExtraccionRequestDto dto) {
        validarMonto(dto.getMonto(), "extraer");

        CuentaBancaria cuenta = cuentaBancariaRepository.findById(dto.getCuentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada con id: " + dto.getCuentaId()));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + dto.getClienteId()));

        if (cuenta.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw new IllegalArgumentException("La cuenta bancaria no se encuentra activa");
        }

        // Determinar rol y límite diario correspondiente
        boolean esAdherente = (cliente.getTitular() != null);
        BigDecimal limiteDiario;

        if (esAdherente) {
            // Regla: Los adherentes operan exclusivamente sobre cuentas de su titular
            if (!cuenta.getTitular().getId().equals(cliente.getTitular().getId())) {
                throw new IllegalArgumentException("El adherente solo puede operar sobre cuentas pertenecientes a su titular");
            }
            limiteDiario = parametroGlobalService.getBigDecimal("LIMITE_EXTRACCION_ADHERENTE", LIMITE_ADHERENTE_DEFAULT);
        } else {
            // Regla: El titular opera sobre su propia cuenta (o cotitular)
            boolean esTitularDeCuenta = cuenta.getTitular().getId().equals(cliente.getId());
            boolean esCotitular = cuenta.getCotitulares() != null && cuenta.getCotitulares().stream()
                    .anyMatch(c -> c.getId().equals(cliente.getId()));

            if (!esTitularDeCuenta && !esCotitular) {
                throw new IllegalArgumentException("El cliente no está autorizado a operar sobre esta cuenta bancaria");
            }
            limiteDiario = parametroGlobalService.getBigDecimal("LIMITE_EXTRACCION_TITULAR", LIMITE_TITULAR_DEFAULT);
        }

        // Control optimizado O(1) en tabla control_diario_extracciones
        LocalDate hoy = LocalDate.now();
        ControlDiarioExtraccion control = controlDiarioExtraccionRepository
                .findByClienteIdAndFecha(cliente.getId(), hoy)
                .orElseGet(() -> ControlDiarioExtraccion.builder()
                        .cliente(cliente)
                        .fecha(hoy)
                        .montoAcumulado(BigDecimal.ZERO)
                        .build());

        BigDecimal nuevoAcumulado = control.getMontoAcumulado().add(dto.getMonto());
        if (nuevoAcumulado.compareTo(limiteDiario) > 0) {
            throw new LimiteExtraccionExcedidoException(String.format(
                    "Operación denegada: supera el límite diario de extracciones. Límite: $%s, acumulado hoy: $%s, intento: $%s",
                    limiteDiario, control.getMontoAcumulado(), dto.getMonto()
            ));
        }

        // Validar saldo disponible
        if (cuenta.getSaldo() == null || cuenta.getSaldo().compareTo(dto.getMonto()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta para realizar la extracción");
        }

        // Efectuar débito
        cuenta.setSaldo(cuenta.getSaldo().subtract(dto.getMonto()));
        cuentaBancariaRepository.save(cuenta);

        // Actualizar acumulador diario
        control.setMontoAcumulado(nuevoAcumulado);
        controlDiarioExtraccionRepository.save(control);

        // Registrar transacción con el cliente ejecutor
        Transaccion transaccion = Transaccion.builder()
                .cuentaBancaria(cuenta)
                .clienteEjecutor(cliente)
                .monto(dto.getMonto())
                .tipo(TipoTransaccion.EXTRACCION)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();
        transaccionRepository.save(transaccion);

        return TransaccionResponseDto.builder()
                .mensaje("Extracción realizada exitosamente")
                .monto(dto.getMonto())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void validarMonto(BigDecimal monto, String operacion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a " + operacion + " debe ser mayor a cero");
        }
    }

    @Override
    @Transactional
    public TransaccionResponseDto realizarTransferencia(TransaccionRequestDto dto) {
        if (dto.getCuentaOrigenId().equals(dto.getCuentaDestinoId())) {
            throw new IllegalArgumentException("La cuenta de origen y de destino no pueden ser la misma");
        }

        CuentaBancaria origen = cuentaBancariaRepository.findById(dto.getCuentaOrigenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta origen no encontrada"));

        CuentaBancaria destino = cuentaBancariaRepository.findById(dto.getCuentaDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta destino no encontrada"));

        if (origen.getSaldo().compareTo(dto.getMonto()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta de origen");
        }

        origen.setSaldo(origen.getSaldo().subtract(dto.getMonto()));
        destino.setSaldo(destino.getSaldo().add(dto.getMonto()));
        cuentaBancariaRepository.save(origen);
        cuentaBancariaRepository.save(destino);

        transaccionRepository.save(crearMovimiento(origen, dto.getMonto(), TipoTransaccion.TRANSFERENCIA_ENVIADA));
        transaccionRepository.save(crearMovimiento(destino, dto.getMonto(), TipoTransaccion.TRANSFERENCIA_RECIBIDA));

        return TransaccionResponseDto.builder()
                .mensaje("Transferencia realizada exitosamente")
                .monto(dto.getMonto())
                .timestamp(LocalDateTime.now())
                .build();
    }

    //metodo auxiliar para no repetir codigo
    private Transaccion crearMovimiento(CuentaBancaria cuenta, BigDecimal monto, TipoTransaccion tipo) {
        return Transaccion.builder()
                .cuentaBancaria(cuenta)
                .monto(monto)
                .tipo(tipo)
                .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                .fecha(new Date())
                .hora(Time.valueOf(LocalTime.now()))
                .build();
    }

    @Override
    public List<Transaccion> obtenerHistorialPorCuenta(UUID cuentaId) {
        return transaccionRepository.findByCuentaBancariaId(cuentaId);
    }

    @Override
    public Transaccion buscarPorId(UUID transaccionId) {
        return transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada con id: " + transaccionId));
    }

    @Override
    @Transactional
    public void procesarDebitoComisionesMasivo(BigDecimal comisionAhorro, BigDecimal comisionCorriente) {
        int pageSize = 100;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<CuentaBancaria> paginaCuentas;

        do {
            paginaCuentas = cuentaBancariaRepository.findByEstadoCuenta(EstadoCuenta.ACTIVA, pageable);

            for (CuentaBancaria cuenta : paginaCuentas.getContent()) {
                BigDecimal comision = null;

                if (cuenta instanceof CajaDeAhorro) {
                    comision = comisionAhorro;
                } else if (cuenta instanceof CuentaCorriente) {
                    comision = comisionCorriente;
                }

                if (comision != null && comision.compareTo(BigDecimal.ZERO) > 0) {
                    cuenta.setSaldo(cuenta.getSaldo().subtract(comision));
                    cuentaBancariaRepository.save(cuenta);

                    Transaccion debito = Transaccion.builder()
                            .cuentaBancaria(cuenta)
                            .monto(comision)
                            .tipo(TipoTransaccion.DEBITO_COMISION)
                            .estadoTransaccion(EstadoTransaccion.COMPLETADA)
                            .fecha(new Date())
                            .hora(Time.valueOf(LocalTime.now()))
                            .build();

                    transaccionRepository.save(debito);
                }
            }

            pageable = paginaCuentas.nextPageable();
        } while (paginaCuentas.hasNext());
    }
}
