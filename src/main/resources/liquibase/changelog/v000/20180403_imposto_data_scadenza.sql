SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

UPDATE accreditamento SET data_scadenza=(data_inizio_conteggio + 180 * INTERVAL '1 day')::date WHERE data_scadenza IS NOT NULL AND data_inizio_conteggio IS NOT NULL;

UPDATE accreditamento SET data_scadenza=NULL WHERE data_scadenza IS NOT NULL AND data_inizio_conteggio IS NULL AND durata_procedimento IS NOT NULL;

