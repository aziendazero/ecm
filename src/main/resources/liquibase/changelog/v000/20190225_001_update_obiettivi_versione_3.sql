SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

/* incremento gli obiettivi regionali per far spazio all'obiettivo n_1*/
UPDATE obiettivo SET codice_cogeaps = CAST(codice_cogeaps AS integer) + 1 where versione = 3 AND nazionale = false;

/* inserisco obiettivo regionale n_1 */
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome, versione) VALUES (nextval('hibernate_sequence'), NULL, 1, false, 'Non rientra in uno degli obiettivi regionali', 3);

/* aggiorno la descrizione di alcuni obiettivi regionali */
UPDATE obiettivo SET nome = 'Sicurezza e igiene negli ambienti e nei luoghi di lavoro e patologie correlate. Radioprotezione' WHERE codice_cogeaps = '27' AND versione = 3 AND nazionale = true;
UPDATE obiettivo SET nome = 'Metodologie, tecniche e procedimenti di misura e indagini analitiche, diagnostiche e di screening, anche in ambito ambientale, del territorio e del patrimonio artistico e culturale. Raccolta, processamento ed elaborazione dei dati e dell’informazione' WHERE codice_cogeaps = '37' AND versione = 3 AND nazionale = true;

/* inserisco il codice cogeaps nella descrizione dell'obiettivo */
update ecmdb.obiettivo set nome = '(' || codice_cogeaps || ') '  || nome where versione = 3;