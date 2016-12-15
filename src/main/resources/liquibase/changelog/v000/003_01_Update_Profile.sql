SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

/*
 * Situazione INIZIALE
 * OSSERVATORE (1021)
 * VISUALIZZATORE (1022)
 * COMPONENTE_OSSERVATORE (1023)
 *
 * Situazione FINALE
 * COMPONENTE_OSSERVATORIO (1023)
 */

/*
 * ELIMNAZIONE RELAZIONE ACCOUNT - PROFILI -> che hanno (OSSERVATORE e VISUALIZZATORE)
 * */
delete from ecmdb.account_profile where profile_id IN (
	select id from ecmdb.profile where profile_enum = 'OSSERVATORE' or profile_enum = 'VISUALIZZATORE'
);

/*
 * ELIMINO RELAZIONE PROFILI - RUOLI -> che hanno (OSSERVATORE e VISUALIZZATORE)
 * ed elimino i profili
 * */
delete from ecmdb.profile_role where profile_id IN (
	select id from ecmdb.profile where profile_enum = 'OSSERVATORE' or profile_enum = 'VISUALIZZATORE'
);
delete from ecmdb.profile where profile_enum = 'OSSERVATORE' or profile_enum = 'VISUALIZZATORE';


/*
 * ASSEGNARE A COMPONENTE_OSSERVATORE stessi RUOLI che aveva OSSERVATORE)
 * */
INSERT INTO profile_role (profile_id, role_id) VALUES (1023, 1002);
INSERT INTO profile_role (profile_id, role_id) VALUES (1023, 1006);

/*
 * RINOMINO COMPONENTE_OSSERVATORE -> COMPONENTE_OSSERVATORIO
 * */
UPDATE ecmdb.profile SET profile_enum = 'COMPONENTE_OSSERVATORIO' WHERE id = 1023;