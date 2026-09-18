# Sistema Bancario

Proyecto de backend bancario desarrollado con Spring Boot y MySQL para la cátedra de Arquitectura de Software (UNJu).

## Tecnologías

* Java 21
* Spring Boot 3
* Spring Data JPA / Hibernate
* MySQL 8 (Docker)
* Maven
* Lombok

## Arquitectura del código

El proyecto está dividido en capas usando interfaces para desacoplar la lógica:

* `model`: Entidades JPA de la base de datos.
* `repository`: Repositorios JPA que extienden de `JpaRepository`.
* `service`: Interfaces donde se definen los métodos de negocio.
* `service.impl`: Clases que implementan los servicios con sus validaciones y transacciones.
