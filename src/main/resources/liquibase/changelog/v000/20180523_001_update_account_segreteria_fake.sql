SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

UPDATE account SET email='formazione.sviluppo@azero.veneto.it' WHERE cognome = 'ECM' and email = 'segreteria@ecm.it';