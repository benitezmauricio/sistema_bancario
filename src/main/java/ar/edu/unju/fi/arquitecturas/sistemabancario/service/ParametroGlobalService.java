package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.model.ParametroGlobal;

import java.math.BigDecimal;
import java.util.List;

public interface ParametroGlobalService {

    BigDecimal getBigDecimal(String clave);

    BigDecimal getBigDecimal(String clave, BigDecimal valorPorDefecto);

    String getString(String clave);

    Boolean getBoolean(String clave);

    ParametroGlobal buscarPorClave(String clave);

    List<ParametroGlobal> listarTodos();

    List<ParametroGlobal> listarPorCategoria(String categoria);

    ParametroGlobal actualizarValor(String clave, String nuevoValor);
}
