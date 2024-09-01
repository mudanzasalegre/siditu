-- Creación de la base de datos
CREATE DATABASE siditu CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Uso de la base de datos recién creada
USE siditu;

-- Creación de la tabla empleados
CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    puede_ir_a_quirofano BOOLEAN NOT NULL,
    equipo INT DEFAULT NULL -- Número del equipo (1 a 6), puede ser NULL
);

-- Creación de la tabla turnos
CREATE TABLE turnos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rueda INT NOT NULL, -- 1 o 2 dependiendo de la subdivisión
    intervalo INT NOT NULL, -- 1 para normal, 2 para subdividido
    empleado_id BIGINT,
    fecha DATE NOT NULL,
    turno CHAR(1) NOT NULL, -- 'M' para mañana, 'N' para noche
	quirofano BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE
);

-- Creación de la tabla para registrar las puntuaciones
CREATE TABLE puntuaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    turno_id BIGINT NOT NULL, -- Referencia al turno que otorga el punto
    motivo VARCHAR(50) NOT NULL, -- 'preseleccion' o 'elegido'
    FOREIGN KEY (turno_id) REFERENCES turnos(id) ON DELETE CASCADE
);
