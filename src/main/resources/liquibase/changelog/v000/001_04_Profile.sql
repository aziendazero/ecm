--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;


--
-- Data for Name: profile; Type: TABLE DATA; Schema: ecmdb; Owner: ecm
--

INSERT INTO profile (id, profile_enum) VALUES (1016, 'PROVIDER');
INSERT INTO profile (id, profile_enum) VALUES (1017, 'PROVIDERUSERADMIN');
INSERT INTO profile (id, profile_enum) VALUES (1018, 'SEGRETERIA');
INSERT INTO profile (id, profile_enum) VALUES (1019, 'REFEREE');
INSERT INTO profile (id, profile_enum) VALUES (1020, 'COMMISSIONE');
INSERT INTO profile (id, profile_enum) VALUES (1021, 'OSSERVATORE');
INSERT INTO profile (id, profile_enum) VALUES (1022, 'VISUALIZZATORE');
INSERT INTO profile (id, profile_enum) VALUES (1023, 'COMPONENTE_OSSERVATORE');


--
-- PostgreSQL database dump complete
--

