SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

--Barduz sarebbe fiero di queste query :)

--GESTIONE DEI MITTENTI
--update colonna fake_account_comunicazione per il gruppo provider in comunicazione
UPDATE comunicazione
SET fake_account_comunicazioni_id = result.fake_id
FROM (
	SELECT fake.id AS fake_id, com.id AS com_id
	FROM comunicazione com
	INNER JOIN account a ON com.mittente_id = a.id
	INNER JOIN account fake ON a.provider_id = fake.provider_id
	WHERE fake.fake_account_comunicazioni = true AND a.fake_account_comunicazioni = false
) AS result
WHERE comunicazione.id = result.com_id;

--update colonna fake_account_comunicazione per il gruppo segreteria in comunicazione
UPDATE comunicazione
SET fake_account_comunicazioni_id = fake_account.id
FROM (
	SELECT fake.*
	FROM account fake
	WHERE fake.username = 'segreteriacomunicazioni'
) AS fake_account
WHERE comunicazione.id IN (SELECT c.id FROM comunicazione c WHERE c.inviato_alla_segreteria = false);

--update colonna fake_account_comunicazione per il gruppo provider in comunicazione_response
UPDATE comunicazione_response
SET fake_account_comunicazioni_id = result.fake_id
FROM (
	SELECT fake.id AS fake_id, com_res.id AS com_res_id
	FROM comunicazione_response com_res
	INNER JOIN account a ON com_res.mittente_id = a.id
	INNER JOIN account fake ON a.provider_id = fake.provider_id
	WHERE fake.fake_account_comunicazioni = true AND a.fake_account_comunicazioni = false
) AS result
WHERE comunicazione_response.id = result.com_res_id;

--update colonna fake_account_comunicazione per il gruppo segreteria in comunicazione_response
UPDATE comunicazione_response
SET fake_account_comunicazioni_id = fake_account.id
FROM (
	SELECT fake.*
	FROM account fake
	WHERE fake.username = 'segreteriacomunicazioni'
) AS fake_account
WHERE comunicazione_response.id IN (SELECT cr.id FROM comunicazione_response cr WHERE cr.inviato_alla_segreteria = false);

--GESTIONE DEI DESTINATARI
--sostituisco il provideruseradmin con il fake_account per il gruppo provider in comunicazione_destinatari
UPDATE comunicazione_destinatari
SET account_id = result.fake_id
FROM (
	SELECT fake.id AS fake_id, com_dest.comunicazione_id AS com_dest_id, a.id AS account_to_substitute_id
	FROM comunicazione_destinatari com_dest
	INNER JOIN account a ON com_dest.account_id = a.id
	INNER JOIN account fake ON a.provider_id = fake.provider_id
	WHERE fake.fake_account_comunicazioni = true AND a.fake_account_comunicazioni = false
) AS result
WHERE comunicazione_destinatari.comunicazione_id = result.com_dest_id AND comunicazione_destinatari.account_id = result.account_to_substitute_id;

--rimuovo dai destinatari tutti gli utenti di tipo segreteria in comunicazione_destinatari
DELETE FROM comunicazione_destinatari
USING (
	SELECT DISTINCT a.*
	FROM account a
	INNER JOIN account_profile ap ON ap.account_id = a.id
	INNER JOIN profile p ON ap.profile_id = p.id
	WHERE p.profile_enum = 'SEGRETERIA' OR p.profile_enum = 'RESPONSABILE_SEGRETERIA_ECM' OR p.profile_enum = 'SEGRETERIA_ACCOUNT_COMUNICAZIONI'
) AS toBeRemoved
WHERE comunicazione_destinatari.account_id IN (toBeRemoved.id);

--aggiungo nei destinatari il fake_account segreteria in comunicazione_destinatari
INSERT INTO comunicazione_destinatari (comunicazione_id, account_id)
SELECT c.id, (SELECT fake.id FROM account fake WHERE fake.username = 'segreteriacomunicazioni') FROM comunicazione c WHERE c.inviato_alla_segreteria = true;

--sostituisco il provideruseradmin con il fake_account per il gruppo provider in comunicazione_response_destinatari
UPDATE comunicazione_response_destinatari
SET account_id = result.fake_id
FROM (
	SELECT fake.id AS fake_id, com_res_dest.account_id AS com_res_dest_id, a.id AS account_to_substitute_id
	FROM comunicazione_response_destinatari com_res_dest
	INNER JOIN account a ON com_res_dest.account_id = a.id
	INNER JOIN account fake ON a.provider_id = fake.provider_id
	WHERE fake.fake_account_comunicazioni = true AND a.fake_account_comunicazioni = false
) AS result
WHERE comunicazione_response_destinatari.comunicazione_response_id = result.com_res_dest_id AND comunicazione_response_destinatari.account_id = result.account_to_substitute_id;

--rimuovo dai destinatari tutti gli utenti di tipo segreteria in comunicazione_response_destinatari
DELETE FROM comunicazione_response_destinatari
USING (
	SELECT DISTINCT a.*
	FROM account a
	INNER JOIN account_profile ap ON ap.account_id = a.id
	INNER JOIN profile p ON ap.profile_id = p.id
	WHERE p.profile_enum = 'SEGRETERIA' OR p.profile_enum = 'RESPONSABILE_SEGRETERIA_ECM' OR p.profile_enum = 'SEGRETERIA_ACCOUNT_COMUNICAZIONI'
) AS toBeRemoved
WHERE comunicazione_response_destinatari.account_id IN (toBeRemoved.id);

--aggiungo nei destinatari il fake_account segreteria in comunicazione_response_destinatari
INSERT INTO comunicazione_response_destinatari (comunicazione_response_id, account_id)
SELECT cr.id, (SELECT fake.id FROM account fake WHERE fake.username = 'segreteriacomunicazioni') FROM comunicazione_response cr WHERE cr.inviato_alla_segreteria = true;




