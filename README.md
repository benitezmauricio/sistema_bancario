# 🏦 Sistema Bancario

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/Licencia-MIT-F7DF1E?style=for-the-badge)

Backend bancario con **Spring Boot** y **MySQL** que gestiona clientes, cuentas (caja de ahorro y cuenta corriente) y transacciones. Realizado en el marco de la cursada de **Desarrollo y Arquitecturas Avanzadas de Software** (Universidad Nacional de Jujuy).

**Integrantes:** Mauricio Fernando Benitez · Luis Carlos Manuel Mamaní

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|------------|-----|
| Java 25 | Lenguaje |
| Spring Boot 4.1.1 | Framework base |
| Spring Data JPA / Hibernate | Persistencia |
| MySQL 8 (Docker) | Base de datos |
| Maven (wrapper) | Build |
| Lombok | Reducción de código repetitivo |

## 🧱 Arquitectura

Arquitectura en capas, con interfaces que desacoplan la lógica de su implementación.

```text
sistemabancario/
├── config/          # Auditoría JPA
├── model/           # Entidades JPA y enumeraciones
├── repository/      # Repositorios (JpaRepository)
├── service/         # Interfaces de negocio
└── service/impl/    # Implementaciones con validaciones y transacciones
```

## 🗄️ Modelo de dominio

```mermaid
classDiagram
    class AuditableEntity {
        <<abstract>>
        LocalDateTime fechaCreacion
        LocalDateTime fechaUltimaModificacion
    }
    class Cliente {
        UUID id
        String nombre
        String cuil
        String mail
        String telefono
        String direccion
        registrarCliente()
        registrarCotitular()
    }
    class CuentaBancaria {
        <<abstract>>
        UUID id
        String cbu
        String alias
        BigDecimal saldo
        EstadoCuenta estadoCuenta
        depositar()
        extraer()
        transferir()
    }
    class CajaDeAhorro {
        Integer cupoLimite
        Float interesAnual
        calcularInteres()
    }
    class CuentaCorriente {
        Float margen
        Float costoComision
        calcularComision()
        aplicarComision()
    }
    class Transaccion {
        UUID id
        Date fecha
        Time hora
        BigDecimal monto
        TipoTransaccion tipo
        EstadoTransaccion estadoTransaccion
        consultarTransaccion()
    }

    AuditableEntity <|-- Cliente
    AuditableEntity <|-- CuentaBancaria
    AuditableEntity <|-- Transaccion
    CuentaBancaria <|-- CajaDeAhorro
    CuentaBancaria <|-- CuentaCorriente
    Cliente "1" --> "0..*" CuentaBancaria : titular
    Cliente "0..*" -- "0..*" CuentaBancaria : cotitulares
    CuentaBancaria "1" --> "0..*" Transaccion : registra
```

## 🚀 Puesta en marcha

**Requisitos:** JDK 25 y Docker (Maven no hace falta, se usa el wrapper).

**1. Levantar MySQL** (crea el esquema y el usuario):

```bash
docker run --name mysql-8.4.0 -p 3306:3306 -d \
  -e MYSQL_ROOT_PASSWORD=<root> \
  -e MYSQL_DATABASE=sistema_bancario \
  -e MYSQL_USER=admin_banco \
  -e MYSQL_PASSWORD=<contraseña> \
  mysql:8.4.0
```

**2. Definir las variables de entorno** que lee la aplicación:

| Variable | Valor |
|----------|-------|
| `DB_URL` | `jdbc:mysql://localhost:3306/sistema_bancario?serverTimezone=UTC` |
| `DB_USERNAME` | `admin_banco` |
| `DB_PASSWORD` | la definida en el paso 1 |

**3. Ejecutar:**

```bash
./mvnw spring-boot:run
```

> [!TIP]
> Las tablas se crean solas al iniciar (`ddl-auto: update`).

## 🧪 Pruebas

```bash
./mvnw test
```

> [!WARNING]
> Las pruebas usan una base MySQL real y leen `src/test/resources/application.yaml`, que está en el `.gitignore`. Cada integrante debe crearlo localmente con su propia conexión.

## 📄 Licencia

Licencia MIT. Ver el archivo [LICENSE](LICENSE).