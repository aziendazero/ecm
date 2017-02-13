SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

--RISPOSTA SEGRETERIA
--se il mittente della risposta è SEGRETERIA setto il flag inviato_alla_segreteria a FALSE
UPDATE comunicazione_response SET inviato_alla_segreteria = false
WHERE comunicazione_response.id IN (
SELECT DISTINCT comunicazione_response.id
	FROM comunicazione_response
	INNER JOIN account ON comunicazione_response.mittente_id = account.id
	INNER JOIN account_profile ON account.id = account_profile.account_id
	INNER JOIN profile ON account_profile.profile_id = profile.id
	WHERE profile.profile_enum IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM')
);
--per la lista dei destinatari, recuperiamo la comunicazione padre:
--1) se il mittente della comunicazione padre NON è SEGRETERIA (PROVIDER), nella lista dei destinatari metto il mittente ottenuto
INSERT INTO comunicazione_response_destinatari (comunicazione_response_id, account_id)
SELECT DISTINCT cr.id, com.mittente_id
	FROM comunicazione_response cr
	INNER JOIN account ON cr.mittente_id = account.id
	INNER JOIN account_profile ON account.id = account_profile.account_id
	INNER JOIN profile ON account_profile.profile_id = profile.id
	INNER JOIN comunicazione com ON com.id=cr.comunicazione_id
	INNER JOIN account acc_com ON com.mittente_id = acc_com.id
	INNER JOIN account_profile ap_com ON acc_com.id = ap_com.account_id
	INNER JOIN profile pr_com ON ap_com.profile_id = pr_com.id
	WHERE profile.profile_enum IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM') AND pr_com.profile_enum NOT IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM')
	ORDER BY cr.id;
--2) se il mittente della comunicazione padre è SEGRETERIA, nella lista dei destinatari metto la lista dei destinatari del padre
INSERT INTO comunicazione_response_destinatari (comunicazione_response_id, account_id)
SELECT DISTINCT cr.id, cd_com.account_id
	FROM comunicazione_response cr
	INNER JOIN account ON cr.mittente_id = account.id
	INNER JOIN account_profile ON account.id = account_profile.account_id
	INNER JOIN profile ON account_profile.profile_id = profile.id
	INNER JOIN comunicazione com ON com.id=cr.comunicazione_id
	INNER JOIN account acc_com ON com.mittente_id = acc_com.id
	INNER JOIN account_profile ap_com ON acc_com.id = ap_com.account_id
	INNER JOIN profile pr_com ON ap_com.profile_id = pr_com.id
	--elenco destinatari della comunicazione padre della risposta
	INNER JOIN comunicazione_destinatari cd_com ON cd_com.comunicazione_id=cr.comunicazione_id
	WHERE profile.profile_enum IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM') AND pr_com.profile_enum IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM')
	ORDER BY cr.id;

-- RISPOSTA PROVIDER || REFEREE || COMMISSIONE || OSSERVATORE
-- se il mittente della risposta NON è SEGRETERIA, setto inviato_alla_segreteria a TRUE
UPDATE comunicazione_response SET inviato_alla_segreteria = true
WHERE comunicazione_response.id IN (
SELECT DISTINCT comunicazione_response.id
	FROM comunicazione_response
	INNER JOIN account ON comunicazione_response.mittente_id = account.id
	INNER JOIN account_profile ON account.id = account_profile.account_id
	INNER JOIN profile ON account_profile.profile_id = profile.id
	WHERE profile.profile_enum NOT IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM')
);
-- la lista dei destinatari rimane null

--setto il flag inviato_alla_segreteria anche per le comunicazioni
--comunicazioni aperte dalla segreteria
UPDATE comunicazione SET inviato_alla_segreteria = false
WHERE comunicazione.id IN (
SELECT DISTINCT comunicazione.id
	FROM comunicazione
	INNER JOIN account ON comunicazione.mittente_id = account.id
	INNER JOIN account_profile ON account.id = account_profile.account_id
	INNER JOIN profile ON account_profile.profile_id = profile.id
	WHERE profile.profile_enum IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM')
);
--comunicazioni NON aperte dalla segreteria
UPDATE comunicazione SET inviato_alla_segreteria = true
WHERE comunicazione.id IN (
SELECT DISTINCT comunicazione.id
	FROM comunicazione
	INNER JOIN account ON comunicazione.mittente_id = account.id
	INNER JOIN account_profile ON account.id = account_profile.account_id
	INNER JOIN profile ON account_profile.profile_id = profile.id
	WHERE profile.profile_enum NOT IN ('SEGRETERIA', 'RESPONSABILE_SEGRETERIA_ECM')
);