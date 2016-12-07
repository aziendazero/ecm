SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

INSERT INTO profile (id, profile_enum) VALUES (999, 'REFERENTE_INFORMATICO');
INSERT INTO profile_role (profile_id, role_id) VALUES (999, 1002);
INSERT INTO profile_role (profile_id, role_id) VALUES (999, 1006);