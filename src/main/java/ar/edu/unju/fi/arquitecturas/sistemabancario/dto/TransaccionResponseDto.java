package ar.edu.unju.fi.arquitecturas.sistemabancario.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransaccionResponseDto {
    private String mensaje;
    private BigDecimal monto;
    private LocalDateTime timestamp;
}