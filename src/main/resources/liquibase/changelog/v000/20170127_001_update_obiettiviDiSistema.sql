SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1067, 'DI SISTEMA', '17', true, 'Argomenti di carattere generale: informatica e lingua inglese scientifica di livello avanzato. Normativa in materia sanitaria : i principi etici e civili del SSN (17)');

UPDATE evento SET obiettivo_nazionale_id = 1067 WHERE obiettivo_nazionale_id = 1040;

UPDATE evento_piano_formativo SET obiettivo_nazionale_id = 1067 WHERE obiettivo_nazionale_id = 1040;

DELETE FROM obiettivo WHERE id = 1040;
