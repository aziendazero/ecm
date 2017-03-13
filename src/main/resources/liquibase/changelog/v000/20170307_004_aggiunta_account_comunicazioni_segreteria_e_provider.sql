SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

-- account fake per comunicazioni segreteria
INSERT INTO account (id, change_password, codice_fiscale, cognome, data_scadenza_password, email, enabled, expires_date, locked, nome, note, password, username, username_workflow, valutazioni_non_date, provider_id, fake_account_comunicazioni)
VALUES (nextval('hibernate_sequence'), FALSE, null, 'ECM', null, 'segreteria@ecm.it', TRUE, null, FALSE, 'Segreteria', null, '$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.', 'segreteriacomunicazioni', null, 0, null, TRUE);

-- account fake per comunicazioni provider
INSERT INTO account (id, change_password, codice_fiscale, cognome, data_scadenza_password, email, enabled, expires_date, locked, nome, note, password, username, username_workflow, valutazioni_non_date, provider_id, fake_account_comunicazioni)
SELECT nextval('hibernate_sequence'), FALSE, null, 'Provider', null, CONCAT('provider', id, '@comunicazioni.it'), TRUE, null, FALSE, 'Comunicazioni', null, '$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.', CONCAT('provider', id, 'comunicazioni'), null, 0, id, TRUE
FROM provider;

-- inserimento profilo SEGRETERIA_ACCOUNT_COMUNICAZIONI per account comunicazioni segreteria
INSERT INTO account_profile (account_id, profile_id)
SELECT a.id, '996' FROM account a WHERE a.fake_account_comunicazioni = TRUE AND a.username = 'segreteriacomunicazioni';

-- inserimento profilo SEGRETERIA_ACCOUNT_COMUNICAZIONI per account comunicazioni segreteria
INSERT INTO account_profile (account_id, profile_id)
SELECT a.id, '995' FROM account a WHERE a.fake_account_comunicazioni = TRUE AND a.provider_id IS NOT NULL;
