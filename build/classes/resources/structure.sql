CREATE TABLE IF NOT EXISTS PACIENTES (
    dni    VARCHAR(10) NOT NULL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL, /* formato yyyy-MM-dd */
    nhosp INT DEFAULT 0
);
