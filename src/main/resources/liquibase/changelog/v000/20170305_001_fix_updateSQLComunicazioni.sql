SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

DROP TABLE IF EXISTS utentiSegreteria;

CREATE TABLE utentiSegreteria (id) AS (
	SELECT DISTINCT a.id
		FROM account a
		INNER JOIN account_profile ap ON ap.account_id = a.id
		INNER JOIN profile p ON ap.profile_id = p.id
		WHERE p.profile_enum = 'SEGRETERIA' OR p.profile_enum = 'RESPONSABILE_SEGRETERIA_ECM' OR p.profile_enum = 'SEGRETERIA_ACCOUNT_COMUNICAZIONI'
);

--RISPOSTA SEGRETERIA
--se il mittente della risposta è SEGRETERIA setto il flag inviato_alla_segreteria a FALSE
UPDATE comunicazione_response SET inviato_alla_segreteria = false
	WHERE comunicazione_response.mittente_id IN (SELECT * FROM utentiSegreteria);

--per la lista dei destinatari, recuperiamo la comunicazione padre:
--1) se il mittente della comunicazione padre NON è SEGRETERIA (PROVIDER), nella lista dei destinatari metto il mittente ottenuto
INSERT INTO comunicazione_response_destinatari (comunicazione_response_id, account_id)
SELECT DISTINCT cr.id, com.mittente_id
	FROM comunicazione_response cr
	INNER JOIN comunicazione com ON com.id=cr.comunicazione_id
	WHERE cr.mittente_id IN (SELECT * FROM utentiSegreteria)
	AND com.mittente_id NOT IN (SELECT * FROM utentiSegreteria)
	AND NOT EXISTS (SELECT * FROM comunicazione_response_destinatari crd WHERE crd.comunicazione_response_id = cr.id AND crd.account_id = com.mittente_id)
	ORDER BY cr.id;

--2) se il mittente della comunicazione padre è SEGRETERIA, nella lista dei destinatari metto la lista dei destinatari del padre
INSERT INTO comunicazione_response_destinatari (comunicazione_response_id, account_id)
SELECT DISTINCT cr.id, cd_com.account_id
	FROM comunicazione_response cr
	INNER JOIN comunicazione com ON com.id=cr.comunicazione_id
	--elenco destinatari della comunicazione padre della risposta
	INNER JOIN comunicazione_destinatari cd_com ON cd_com.comunicazione_id=cr.comunicazione_id
	WHERE com.mittente_id IN (SELECT * FROM utentiSegreteria) AND cr.mittente_id IN (SELECT * FROM utentiSegreteria)
	AND NOT EXISTS (SELECT * FROM comunicazione_response_destinatari crd WHERE crd.comunicazione_response_id = cr.id AND crd.account_id = cd_com.account_id)
	ORDER BY cr.id;


-- RISPOSTA PROVIDER || REFEREE || COMMISSIONE || OSSERVATORE
-- se il mittente della risposta NON è SEGRETERIA, setto inviato_alla_segreteria a TRUE
UPDATE comunicazione_response SET inviato_alla_segreteria = true
	WHERE comunicazione_response.mittente_id NOT IN (SELECT * FROM utentiSegreteria);
-- la lista dei destinatari rimane null

--setto il flag inviato_alla_segreteria anche per le comunicazioni
--comunicazioni aperte dalla segreteria
UPDATE comunicazione SET inviato_alla_segreteria = false
	WHERE comunicazione.mittente_id IN (SELECT * FROM utentiSegreteria);

--comunicazioni NON aperte dalla segreteria
UPDATE comunicazione SET inviato_alla_segreteria = true
	WHERE comunicazione.mittente_id NOT IN (SELECT * FROM utentiSegreteria);

DROP TABLE IF EXISTS utentiSegreteria;