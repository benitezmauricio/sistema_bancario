-- Desactivar temporalmente restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE cuentas_cotitulares;
TRUNCATE TABLE transacciones;
TRUNCATE TABLE cuentas_bancarias;
TRUNCATE TABLE clientes;
TRUNCATE TABLE parametros_globales;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Inserción de clientes (10 clientes para pruebas)
INSERT INTO clientes
    (id, nombre, cuil, mail, telefono, direccion, fecha_creacion, fecha_ultima_modificacion)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), 'Juan Pérez', '20-301112-2',
     'juan.perez@email.com', '4221122', 'Av. Belgrano 123', NOW(), NOW()),
    (UUID_TO_BIN('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), 'María Gómez', '27-312223-3',
     'maria.gomez@email.com', '4233344', 'Calle Lavalle 456', NOW(), NOW()),
    (UUID_TO_BIN('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'), 'Carlos Rodríguez', '20-345678-4',
     'carlos.rodriguez@email.com', '4255566', 'San Martín 789', NOW(), NOW()),
    (UUID_TO_BIN('a2eebc99-9c0b-4ef8-bb6d-6bb9bd380a04'), 'Ana Martínez', '27-356789-5',
     'ana.martinez@email.com', '4266677', 'Güemes 321', NOW(), NOW()),
    (UUID_TO_BIN('a3eebc99-9c0b-4ef8-bb6d-6bb9bd380a05'), 'Lucas Fernández', '20-367890-6',
     'lucas.fernandez@email.com', '4277788', 'Alvear 654', NOW(), NOW()),
    (UUID_TO_BIN('a4eebc99-9c0b-4ef8-bb6d-6bb9bd380a06'), 'Sofía López', '27-378901-7',
     'sofia.lopez@email.com', '4288899', 'Independencia 987', NOW(), NOW()),
    (UUID_TO_BIN('a5eebc99-9c0b-4ef8-bb6d-6bb9bd380a07'), 'Matías Díaz', '20-389012-8',
     'matias.diaz@email.com', '4299900', 'Senador Pérez 147', NOW(), NOW()),
    (UUID_TO_BIN('a6eebc99-9c0b-4ef8-bb6d-6bb9bd380a08'), 'Valentina Morales', '27-390123-9',
     'valentina.morales@email.com', '4211100', 'Necochea 258', NOW(), NOW()),
    (UUID_TO_BIN('a7eebc99-9c0b-4ef8-bb6d-6bb9bd380a09'), 'Gonzalo Romero', '20-401234-0',
     'gonzalo.romero@email.com', '4222211', 'Gorriti 369', NOW(), NOW()),
    (UUID_TO_BIN('a8eebc99-9c0b-4ef8-bb6d-6bb9bd380a10'), 'Camila Torres', '27-412345-1',
     'camila.torres@email.com', '4233322', 'Balcarce 741', NOW(), NOW());

-- 2. Inserción de cuentas bancarias (5 cuentas para pruebas en tabla unificada)
INSERT INTO cuentas_bancarias
    (id, tipo_cuenta, alias, cbu, estado_cuenta, saldo, titular_id, cupo_limite, interes_anual, margen, costo_comision, fecha_creacion, fecha_ultima_modificacion)
VALUES
    (UUID_TO_BIN('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), 'CAJA_DE_AHORRO', 'JUAN.PEREZ.ARS',
     '0000003100000000000001', 'ACTIVA', 150000.50,
     UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), 500000, 35.5, NULL, NULL, NOW(), NOW()),
    (UUID_TO_BIN('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), 'CUENTA_CORRIENTE', 'MARIA.GOMEZ.ARS',
     '0000003100000000000002', 'ACTIVA', 320000.75,
     UUID_TO_BIN('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), NULL, NULL, 100000.0, 1500.0, NOW(), NOW()),
    (UUID_TO_BIN('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a55'), 'CAJA_DE_AHORRO', 'CARLOS.RODRIGUEZ',
     '0000003100000000000003', 'ACTIVA', 210500.00,
     UUID_TO_BIN('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'), 600000, 38.0, NULL, NULL, NOW(), NOW()),
    (UUID_TO_BIN('d2eebc99-9c0b-4ef8-bb6d-6bb9bd380a66'), 'CUENTA_CORRIENTE', 'ANA.MARTINEZ.CC',
     '0000003100000000000004', 'ACTIVA', 540000.00,
     UUID_TO_BIN('a2eebc99-9c0b-4ef8-bb6d-6bb9bd380a04'), NULL, NULL, 250000.0, 2200.0, NOW(), NOW()),
    (UUID_TO_BIN('c3eebc99-9c0b-4ef8-bb6d-6bb9bd380a77'), 'CAJA_DE_AHORRO', 'LUCAS.FERNANDEZ',
     '0000003100000000000005', 'ACTIVA', 98000.25,
     UUID_TO_BIN('a3eebc99-9c0b-4ef8-bb6d-6bb9bd380a05'), 400000, 35.0, NULL, NULL, NOW(), NOW());

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

-- 4. Inserción de parámetros globales del sistema
INSERT INTO parametros_globales
    (clave, valor, tipo_dato, categoria, unidad, descripcion, activo, fecha_creacion, fecha_ultima_modificacion)
VALUES
    ('COMISION_CAJA_AHORRO', '2000.00', 'DECIMAL', 'COMISIONES', 'ARS', 'Mantenimiento mensual fijo para Caja de Ahorro', 1, NOW(), NOW()),
    ('COMISION_CUENTA_CORRIENTE', '5000.00', 'DECIMAL', 'COMISIONES', 'ARS', 'Mantenimiento mensual fijo para Cuenta Corriente', 1, NOW(), NOW()),
    ('LIMITE_EXTRACCION_TITULAR', '100000.00', 'DECIMAL', 'LIMITES', 'ARS', 'Límite global diario acumulado para extracciones de titulares', 1, NOW(), NOW()),
    ('LIMITE_EXTRACCION_ADHERENTE', '70000.00', 'DECIMAL', 'LIMITES', 'ARS', 'Límite global diario acumulado para extracciones de adherentes', 1, NOW(), NOW());
