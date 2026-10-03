-- Desactivar temporalmente restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE cuentas_cotitulares;
TRUNCATE TABLE transacciones;
TRUNCATE TABLE cajas_ahorro;
TRUNCATE TABLE cuentas_corrientes;
TRUNCATE TABLE cuentas_bancarias;
TRUNCATE TABLE clientes;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Inserción de clientes
INSERT INTO clientes
(id, nombre, cuil, mail, telefono, direccion, estado_cliente, fecha_creacion, fecha_ultima_modificacion)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), 'Juan Pérez', '20-301112-2',
     'juan.perez@email.com', '4221122', 'Av. Belgrano 123', 'ACTIVO', NOW(), NOW()),
    (UUID_TO_BIN('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), 'María Gómez', '27-312223-3',
     'maria.gomez@email.com', '4233344', 'Calle Lavalle 456', 'ACTIVO', NOW(), NOW());
-- 2. Inserción de cuentas bancarias
INSERT INTO cuentas_bancarias
    (id, alias, cbu, estado_cuenta, saldo, titular_id, fecha_creacion, fecha_ultima_modificacion)
VALUES
    (UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), 'JUAN.PEREZ.ARS',
     '0000003100000000000001', 'ACTIVA', 150000.50,
     UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), NOW(), NOW()),
    (UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), 'MARIA.GOMEZ.ARS',
     '0000003100000000000002', 'ACTIVA', 320000.75,
     UUID_TO_BIN('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), NOW(), NOW());

-- Registro de las subclases de cuenta
INSERT INTO cajas_ahorro
    (cuenta_id, cupo_limite, interes_anual)
VALUES
    (UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), 500000, 35.5);

INSERT INTO cuentas_corrientes
    (cuenta_id, margen, costo_comision)
VALUES
    (UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), 100000.0, 1500.0);

-- 3. Inserción de transacciones
INSERT INTO transacciones
    (id, fecha, hora, monto, tipo_transaccion, estado_transaccion,
     cuenta_bancaria_id, fecha_creacion, fecha_ultima_modificacion)
VALUES
    (UUID_TO_BIN('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380a55'), CURDATE(), CURTIME(),
     50000.00, 'DEPOSITO', 'COMPLETADA',
     UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), NOW(), NOW()),
    (UUID_TO_BIN('f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a66'), CURDATE(), CURTIME(),
     12000.50, 'EXTRACCION', 'COMPLETADA',
     UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), NOW(), NOW());
