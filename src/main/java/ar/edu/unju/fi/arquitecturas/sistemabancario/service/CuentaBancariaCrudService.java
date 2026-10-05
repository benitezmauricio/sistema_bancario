package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.enums.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio helper responsable exclusivo de las operaciones CRUD y persistencia
 * directa de {@link CuentaBancaria}.
 */
public interface CuentaBancariaCrudService {

    /**
     * Persiste o actualiza una cuenta bancaria.
     */
    CuentaBancaria guardar(CuentaBancaria cuenta);

    /**
     * Busca una cuenta bancaria por su ID único.
     */
    Optional<CuentaBancaria> buscarPorId(UUID id);

    /**
     * Busca una cuenta bancaria por su ID o lanza {@link RecursoNoEncontradoException}.
     */
    CuentaBancaria buscarPorIdOError(UUID id);

    /**
     * Busca una cuenta bancaria por su CBU.
     */
    Optional<CuentaBancaria> buscarPorCbu(String cbu);

    /**
     * Busca una cuenta bancaria por su CBU o lanza {@link RecursoNoEncontradoException}.
     */
    CuentaBancaria buscarPorCbuOError(String cbu);

    /**
     * Busca una cuenta bancaria por su alias.
     */
    Optional<CuentaBancaria> buscarPorAlias(String alias);

    /**
     * Busca una cuenta bancaria por su alias o lanza {@link RecursoNoEncontradoException}.
     */
    CuentaBancaria buscarPorAliasOError(String alias);

    /**
     * Obtiene todas las cuentas bancarias asociadas a un titular.
     */
    List<CuentaBancaria> buscarPorTitularId(UUID titularId);

    /**
     * Obtiene todas las cuentas bancarias de forma paginada.
     */
    Page<CuentaBancaria> listarTodas(Pageable pageable);

    /**
     * Obtiene cuentas bancarias filtradas por su estado de forma paginada.
     */
    Page<CuentaBancaria> listarPorEstado(EstadoCuenta estado, Pageable pageable);

    /**
     * Elimina una cuenta bancaria por su ID (ejecuta soft delete si está configurado en la entidad).
     *
     * @throws RecursoNoEncontradoException si no existe.
     */
    void eliminarPorId(UUID id);

    /**
     * Elimina una cuenta bancaria por su CBU (ejecuta soft delete).
     *
     * @throws RecursoNoEncontradoException si no existe.
     */
    void eliminarPorCbu(String cbu);

    /**
     * Verifica si existe una cuenta bancaria con el ID dado.
     */
    boolean existePorId(UUID id);
}
