-- Corregido TITULRES -> TITULARES
insert into TITULARES (nombre) VALUES ('Daniel');
insert into TITULARES (nombre) VALUES ('Cristina');

-- Corregido titular -> titular_id
-- Corregido 'Daniel' -> 1 (El ID generado para el primer insert)
insert into DISPOSITIVOS (marca, modelo, numero_Serie, titular_id, fabricante, tipo, uuid)
VALUES ('Samsung', 'Galaxy S10', '123456789', 1, 'Samsung', 'Movil', UUID());

-- Corregido titular -> titular_id
-- Corregido 'Maria' -> 2 (El ID generado para el segundo insert)
insert into DISPOSITIVOS (marca, modelo, numero_Serie, titular_id, fabricante, tipo, uuid)
VALUES ('Apple', 'iPhone 12', '987654321', 2, 'Apple', 'Movil', UUID());
