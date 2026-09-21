-- Desactivar temporalmente restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE transaccion;
TRUNCATE TABLE caja_ahorro;
TRUNCATE TABLE cuenta_corriente;
TRUNCATE TABLE cuenta_bancaria;
TRUNCATE TABLE cliente;

SET FOREIGN_KEY_CHECKS = 1;

--1. INSERCIÓN DE CLIENTES
INSERT INTO cliente (id, nombre, cuil, mail, telefono, direccion, created_date, last_modified_date) VALUES
                                                                                                        (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), 'Juan Pérez', 20301112, 'juan.perez@email.com', 4221122, 'Av. Belgrano 123', NOW(), NOW()),
                                                                                                        (UUID_TO_BIN('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), 'María Gómez', 27312223, 'maria.gomez@email.com', 4233344, 'Calle Lavalle 456', NOW(), NOW());

-- 2. INSERCIÓN DE CUENTAS BANCARIAS
INSERT INTO cuenta_bancaria (id, alias, cbu, estado_cuenta, saldo, titular_id, created_date, last_modified_date) VALUES
                                                                                                                     (UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), 'JUAN.PEREZ.ARS', '0000003100000000000001', 'ACTIVA', 150000.50, UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), NOW(), NOW()),
                                                                                                                     (UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), 'MARIA.GOMEZ.ARS', '0000003100000000000002', 'ACTIVA', 320000.75, UUID_TO_BIN('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), NOW(), NOW());

-- Registro de las subclases de cuenta (Caja de Ahorro para Juan, Cuenta Corriente para María)
INSERT INTO caja_ahorro (cuenta_id, cupo_limite, interes_anual, created_date, last_modified_date) VALUES
    (UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), 500000, 35.5, NOW(), NOW());

INSERT INTO cuenta_corriente (cuenta_id, margen, costo_comision, created_date, last_modified_date) VALUES
    (UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), 100000.0, 1500.0, NOW(), NOW());

-- 3. INSERCIÓN DE TRANSACCIONES
INSERT INTO transaccion (id, fecha, hora, monto, tipo, estado_cuenta, cuenta_bancaria_id, created_date, last_modified_date) VALUES
                                                                                                                                (UUID_TO_BIN('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380a55'), CURDATE(), CURTIME(), 50000.00, 'DEPOSITO', 'COMPLETADA', UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), NOW(), NOW()),
                                                                                                                                (UUID_TO_BIN('f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a66'), CURDATE(), CURTIME(), 12000.50, 'EXTRACCION', 'COMPLETADA', UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), NOW(), NOW());