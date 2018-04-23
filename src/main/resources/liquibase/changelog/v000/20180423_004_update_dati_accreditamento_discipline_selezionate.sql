SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

INSERT INTO dati_accreditamento_discipline_selezionate (dati_accreditamento_Id, disciplina_id)
SELECT DISTINCT dati_accreditamento_Id, 1172 FROM dati_accreditamento_discipline_selezionate;
