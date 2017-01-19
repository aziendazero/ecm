SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

INSERT INTO profile (id, profile_enum) VALUES (998, 'RESPONSABILE_SEGRETERIA_ECM');
INSERT INTO profile (id, profile_enum) VALUES (997, 'PROVIDER_VISUALIZZATORE');

INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1010);
INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1011);
INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1012);
INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1002);
INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1003);
INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1006);
INSERT INTO profile_role (profile_id, role_id) VALUES (998, 1007);

INSERT INTO profile_role (profile_id, role_id) VALUES (997, 1000);
INSERT INTO profile_role (profile_id, role_id) VALUES (997, 1004);


