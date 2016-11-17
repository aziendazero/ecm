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
-- Data for Name: role; Type: TABLE DATA; Schema: ecmdb; Owner: ecm
--

INSERT INTO role (id, description, name) VALUES (1000, 'ACCREDITAMENTO (LETTURA)', 'ACCREDITAMENTO_SHOW');
INSERT INTO role (id, description, name) VALUES (1001, 'ACCREDITAMENTO (SCRITTURA)', 'ACCREDITAMENTO_EDIT');
INSERT INTO role (id, description, name) VALUES (1002, 'ACCREDITAMENTO (LETTURA TUTTI)', 'ACCREDITAMENTO_SHOW_ALL');
INSERT INTO role (id, description, name) VALUES (1003, 'ACCREDITAMENTO (SCRITTURA TUTTI)', 'ACCREDITAMENTO_EDIT_ALL');
INSERT INTO role (id, description, name) VALUES (1004, 'PROVIDER (LETTURA)', 'PROVIDER_SHOW');
INSERT INTO role (id, description, name) VALUES (1005, 'PROVIDER (SCRITTURA)', 'PROVIDER_EDIT');
INSERT INTO role (id, description, name) VALUES (1006, 'PROVIDER (LETTURA TUTTI)', 'PROVIDER_SHOW_ALL');
INSERT INTO role (id, description, name) VALUES (1007, 'PROVIDER (SCRITTURA TUTTI)', 'PROVIDER_EDIT_ALL');
INSERT INTO role (id, description, name) VALUES (1008, 'UTENTI (LETTURA)', 'USER_SHOW');
INSERT INTO role (id, description, name) VALUES (1009, 'UTENTI (SCRITTURA)', 'USER_EDIT');
INSERT INTO role (id, description, name) VALUES (1010, 'UTENTI (LETTURA TUTTI)', 'USER_SHOW_ALL');
INSERT INTO role (id, description, name) VALUES (1011, 'UTENTI (SCRITTURA TUTTI)', 'USER_EDIT_ALL');
INSERT INTO role (id, description, name) VALUES (1012, 'UTENTI (CREAZIONE)', 'USER_CREATE');
INSERT INTO role (id, description, name) VALUES (1013, 'PROVIDER (LETTURA UTENTI DEL PROVIDER)', 'PROVIDER_USER_SHOW');
INSERT INTO role (id, description, name) VALUES (1014, 'PROVIDER (MODIFICA UTENTI DEL PROVIDER)', 'PROVIDER_USER_EDIT');
INSERT INTO role (id, description, name) VALUES (1015, 'PROVIDER (CREAZIONE UTENTI DEL PROVIDER)', 'PROVIDER_USER_CREATE');

