package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.ParametroGlobal;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.TipoDatoParametro;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ParametroGlobalRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl.ParametroGlobalServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParametroGlobalServiceTest {

    @Mock
    private ParametroGlobalRepository parametroGlobalRepository;

    @InjectMocks
    private ParametroGlobalServiceImpl parametroGlobalService;

    @Test
    @DisplayName("Debe retornar BigDecimal cuando el parámetro existe y es decimal válido")
    void getBigDecimal_CuandoExiste_DebeRetornarValor() {
        ParametroGlobal param = ParametroGlobal.builder()
                .clave("COMISION_CAJA_AHORRO")
                .valor("2000.00")
                .tipoDato(TipoDatoParametro.DECIMAL)
                .categoria("COMISIONES")
                .activo(true)
                .build();

        when(parametroGlobalRepository.findByClaveAndActivoTrue("COMISION_CAJA_AHORRO"))
                .thenReturn(Optional.of(param));

        BigDecimal resultado = parametroGlobalService.getBigDecimal("COMISION_CAJA_AHORRO");

        assertNotNull(resultado);
        assertEquals(new BigDecimal("2000.00"), resultado);
        verify(parametroGlobalRepository, times(1)).findByClaveAndActivoTrue("COMISION_CAJA_AHORRO");
    }

    @Test
    @DisplayName("Debe retornar valor por defecto cuando el parámetro no existe")
    void getBigDecimal_ConValorPorDefecto_CuandoNoExiste() {
        when(parametroGlobalRepository.findByClaveAndActivoTrue("INEXISTENTE"))
                .thenReturn(Optional.empty());

        BigDecimal resultado = parametroGlobalService.getBigDecimal("INEXISTENTE", new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), resultado);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el parámetro no existe al usar getBigDecimal sin fallback")
    void getBigDecimal_CuandoNoExiste_DebeLanzarExcepcion() {
        when(parametroGlobalRepository.findByClaveAndActivoTrue("NO_EXISTE"))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                parametroGlobalService.getBigDecimal("NO_EXISTE"));
    }
}
