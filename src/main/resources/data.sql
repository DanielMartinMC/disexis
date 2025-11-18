--- 0. LIMPIEZA TOTAL (Para evitar errores de duplicados al reiniciar)
DELETE FROM USER_ROLES;
DELETE FROM DISPOSITIVOS;
DELETE FROM USUARIOS;
DELETE FROM TITULARES;

--- 1. TABLAS PADRE (No dependen de nadie)
INSERT INTO TITULARES (id, nombre) VALUES (1, 'Titular Uno');
INSERT INTO TITULARES (id, nombre) VALUES (2, 'Titular Dos');
INSERT INTO TITULARES (id, nombre) VALUES (3, 'Titular Tres');

--- 2. TABLAS HIJAS (Dependen de TITULARES)
-- Usuarios
INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, titular_id)
VALUES (1, 'Admin', 'Admin Admin', 'admin', 'admin@prueba.net', '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2', 1);

INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, titular_id)
VALUES (2, 'Jose', 'Jose User', 'jose', 'user@prueba.net', '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.', 2);

INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, titular_id)
VALUES (3, 'Test', 'Test Test', 'test', 'test@prueba.net', '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu', NULL);

INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, titular_id)
VALUES (4, 'María', 'María Otro', 'maría', 'otro@prueba.net', '$2a$12$3Q4.UZbvBMBEvIwwjGEjae/zrIr6S50NusUlBcCNmBd2382eyU0bS', 3);

-- Dispositivos (Ahora sí, porque los titulares 1 y 2 ya existen)
INSERT INTO DISPOSITIVOS (marca, modelo, numero_Serie, titular_id, fabricante, tipo, uuid)
VALUES ('Samsung', 'Galaxy S10', '123456789', 1, 'Samsung', 'Movil', UUID());

INSERT INTO DISPOSITIVOS (marca, modelo, numero_Serie, titular_id, fabricante, tipo, uuid)
VALUES ('Apple', 'iPhone 12', '987654321', 2, 'Apple', 'Movil', UUID());

--- 3. RELACIONES ADICIONALES
INSERT INTO USER_ROLES (user_id, roles) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLES (user_id, roles) VALUES (1, 'USER');
INSERT INTO USER_ROLES (user_id, roles) VALUES (2, 'USER');
INSERT INTO USER_ROLES (user_id, roles) VALUES (3, 'USER');
INSERT INTO USER_ROLES (user_id, roles) VALUES (4, 'USER');