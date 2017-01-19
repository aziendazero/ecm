SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

UPDATE evento SET obiettivo_nazionale_id = 1039 WHERE obiettivo_nazionale_id = 1067;
UPDATE evento SET obiettivo_nazionale_id = 1040 WHERE obiettivo_nazionale_id = 1069;

UPDATE evento_piano_formativo SET obiettivo_nazionale_id = 1039 WHERE obiettivo_nazionale_id = 1067;
UPDATE evento_piano_formativo SET obiettivo_nazionale_id = 1040 WHERE obiettivo_nazionale_id = 1069;

DELETE FROM obiettivo WHERE id = 1067;
DELETE FROM obiettivo WHERE id = 1069;