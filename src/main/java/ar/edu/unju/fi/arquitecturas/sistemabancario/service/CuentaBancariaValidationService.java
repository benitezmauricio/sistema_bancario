package ar.edu.unju.fi.arquitecturas.sistemabancario.service;

import ar.edu.unju.fi.arquitecturas.sistemabancario.dto.CuentaUpdateDto;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.Cliente;
import ar.edu.unju.fi.arquitecturas.sistemabancario.model.CuentaBancaria;

import java.util.List;
import java.util.UUID;

/**
 * Servicio encargado de validar las reglas de negocio vinculadas a las cuentas bancarias.
 */
public interface CuentaBancariaValidationService {

    /**
     * Valida que el CBU no esté ya registrado en el sistema.
     *
     * @param cbu CBU a verificar.
     * @throws IllegalArgumentException si el CBU ya se encuentra registrado.
     */
    void validarCbuUnico(String cbu);

    /**
     * Valida que el alias no esté ya registrado en el sistema.
     *
     * @param alias Alias a verificar.
     * @throws IllegalArgumentException si el alias ya está registrado.
     */
    void validarAliasUnico(String alias);

    /**
     * Valida que un nuevo alias no esté en uso por otra cuenta al momento de actualizar.
     *
     * @param cuentaId   ID de la cuenta a actualizar.
     * @param nuevoAlias Nuevo alias solicitado.
     * @throws IllegalArgumentException si el alias pertenece a otra cuenta.
     */
    void validarAliasUnicoParaActualizacion(UUID cuentaId, String nuevoAlias);

    /**
     * Valida la existencia del titular en la base de datos y lo retorna.
     *
     * @param titularId ID del cliente titular.
     * @return Entidad {@link Cliente} encontrada.
     * @throws ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException si el titular no existe.
     */
    Cliente validarYObtenerTitular(UUID titularId);

    /**
     * Valida la existencia de la lista de cotitulares y los retorna.
     *
     * @param cotitularesIds Lista de identificadores de los cotitulares.
     * @return Lista de entidades {@link Cliente} validadas.
     * @throws ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException si alguno de los cotitulares no existe.
     */
    List<Cliente> validarYObtenerCotitulares(List<UUID> cotitularesIds);

    /**
     * Valida que la cuenta exista y la retorna para su manipulación.
     *
     * @param cuentaId ID de la cuenta bancaria.
     * @return Entidad {@link CuentaBancaria} encontrada.
     * @throws ar.edu.unju.fi.arquitecturas.sistemabancario.exception.RecursoNoEncontradoException si la cuenta no existe.
     */
    CuentaBancaria validarYObtenerCuentaExistente(UUID cuentaId);

    /**
     * Valida la coherencia de los campos de actualización según el tipo de cuenta concreto.
     *
     * @param cuenta Entidad cuenta bancaria actual.
     * @param dto    Datos solicitados para actualizar.
     * @throws IllegalArgumentException si se envían campos incompatibles con el tipo de cuenta.
     */
    void validarCamposTipoCuenta(CuentaBancaria cuenta, CuentaUpdateDto dto);
}
