SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

update ecmdb.provider SET can_insert_relazione_annuale = TRUE where can_insert_relazione_annuale is null;
