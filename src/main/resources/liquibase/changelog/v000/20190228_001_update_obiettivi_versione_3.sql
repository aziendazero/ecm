SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

/* inserisco obiettivo regionale n_0 */
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome, versione) VALUES (nextval('hibernate_sequence'), NULL, 0, false, 'Non rientra in uno degli obiettivi regionali', 3);

/* aggiorno la descrizione di alcuni obiettivi regionali */
UPDATE obiettivo SET nome = 'Sicurezza e igiene negli ambienti e nei luoghi di lavoro e patologie correlate. Radioprotezione' WHERE codice_cogeaps = '27' AND versione = 3 AND nazionale = true;
UPDATE obiettivo SET nome = 'Metodologie, tecniche e procedimenti di misura e indagini analitiche, diagnostiche e di screening, anche in ambito ambientale, del territorio e del patrimonio artistico e culturale. Raccolta, processamento ed elaborazione dei dati e dell’informazione' WHERE codice_cogeaps = '37' AND versione = 3 AND nazionale = true;
UPDATE obiettivo SET nome = 'Metodologia e tecniche di comunicazione, anche in relazione allo sviluppo dei programmi nazionali e regionali di prevenzione primaria' WHERE codice_cogeaps = '13' AND versione = 3 AND nazionale = true;
UPDATE obiettivo SET nome = 'Sicurezza e igiene ambientali (aria, acqua e suolo) e/o patologie correlate' WHERE codice_cogeaps = '26' AND versione = 3 AND nazionale = true;
UPDATE obiettivo SET nome = 'Accreditamento strutture sanitarie e dei professionisti. La cultura della qualità, procedure e certificazione, con acquisizione di nozioni tecnico-professionali' WHERE codice_cogeaps = '34' AND versione = 3 AND nazionale = true;

/* inserisco il codice cogeaps nella descrizione dell'obiettivo */
update ecmdb.obiettivo set nome = '(' || codice_cogeaps || ') '  || nome where versione = 3 AND codice_cogeaps <> '0';