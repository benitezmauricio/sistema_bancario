package ar.edu.unju.fi.arquitecturas.sistemabancario.service.impl;

import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.ParametroGlobal;
import ar.edu.unju.fi.arquitecturas.sistemabancario.repository.ParametroGlobalRepository;
import ar.edu.unju.fi.arquitecturas.sistemabancario.service.ParametroGlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParametroGlobalServiceImpl implements ParametroGlobalService {

    private final ParametroGlobalRepository parametroGlobalRepository;

    @Override
    public BigDecimal getBigDecimal(String clave) {
        ParametroGlobal parametro = buscarPorClave(clave);
        try {
            return new BigDecimal(parametro.getValor());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("El parámetro '" + clave + "' no contiene un valor numérico decimal válido: " + parametro.getValor());
        }
    }

    @Override
    public BigDecimal getBigDecimal(String clave, BigDecimal valorPorDefecto) {
        return parametroGlobalRepository.findByClaveAndActivoTrue(clave)
                .map(param -> {
                    try {
                        return new BigDecimal(param.getValor());
                    } catch (NumberFormatException e) {
                        return valorPorDefecto;
                    }
                })
                .orElse(valorPorDefecto);
    }

    @Override
    public String getString(String clave) {
        return buscarPorClave(clave).getValor();
    }

    @Override
    public Boolean getBoolean(String clave) {
        return Boolean.parseBoolean(buscarPorClave(clave).getValor());
    }

    @Override
    public ParametroGlobal buscarPorClave(String clave) {
        return parametroGlobalRepository.findByClaveAndActivoTrue(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parámetro global activo no encontrado para la clave: " + clave));
    }

    @Override
    public List<ParametroGlobal> listarTodos() {
        return parametroGlobalRepository.findAll();
    }

    @Override
    public List<ParametroGlobal> listarPorCategoria(String categoria) {
        return parametroGlobalRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria);
    }

    @Override
    @Transactional
    public ParametroGlobal actualizarValor(String clave, String nuevoValor) {
        if (nuevoValor == null || nuevoValor.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo valor para el parámetro '" + clave + "' no puede ser nulo o vacío");
        }

        ParametroGlobal parametro = parametroGlobalRepository.findById(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el parámetro con clave: " + clave));

        parametro.setValor(nuevoValor.trim());
        return parametroGlobalRepository.save(parametro);
    }
}
