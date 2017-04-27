SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

CREATE TABLE ecmdb.anagrafica_tipi
(
  tipo character varying(255) NOT NULL,
  codice character varying(255) NOT NULL,
  descrizione character varying(255),
  ordine integer,
  CONSTRAINT anagrafica_tipi_pk PRIMARY KEY (tipo, codice)
);

INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'ISTITUTI_RICOVERO', 'Istituti di ricovero e cura a carattere scientifico (IRCCS)', 14);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'AZIENDE_SANITARIE', 'Aziende Sanitarie (Aziende Usl, Aziende Ospedaliere, Policlinici)', 2);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'UNIVERSITA', 'Università, facoltà e dipartimenti universitari', 1);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'ISTITUTI_SCIENTIFICI', 'Istituti scientifici del servizio sanitario nazionale', 3);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'ISTITUTI_CONSIGLIO', 'Istituti del Consiglio Nazionale delle Ricerche', 4);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'SOCIETA_SCIENTIFICHE', 'Società scientifiche e associazioni professionali in campo sanitario', 6);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'COLLEGI', 'Ordini e Collegi delle Professioni Sanitarie', 7);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'FONDAZIONI', 'Fondazioni a carattere scientifico', 8);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'CASE_EDITRICI', 'Case editrici scientifiche', 9);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'PUBBLICI', 'Società, Agenzie ed Enti Pubblici', 10);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'PRIVATI', 'Società, Agenzie ed Enti Privati', 11);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'RICOVERO_PUBBLICHE', 'Strutture di ricovero pubbliche', 12);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'RICOVERO_PRIVATE', 'Strutture di ricovero private', 13);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'ZOOPROFILATTICO', 'Istituto zooprofilattico', 5);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'ENTE_FORMAZIONE', 'Ente di formazione a partecipazione prevalentemente pubblica regionale o provinciale', 15);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('TipoOrganizzatore', 'OSPEDALI_CLASSIFICATI', 'Ospedali classificati ex. Art. 1 legge 132 1968', 16);

 
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'INSERITO', 'Inserito - Domanda in stato di bozza', 1);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'VALIDATO', 'Domanda inviata', 2);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'ACCREDITATO_PROVVISORIAMENTE', 'Accreditamento provvisorio accettato', 3);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'DINIEGO', 'Accreditamento rifiutato', 4);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'ACCREDITATO_STANDARD', 'Accreditamento standard accettato', 5);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'SOSPESO', 'Accreditamento temporaneamente sospeso', 6);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('ProviderStatoEnum', 'CANCELLATO', 'Accreditamento cancellato', 7);


INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFADEnum', 'QUESTIONARIO', 'Questionario (test)', 1);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFADEnum', 'ESAME_ORALE', 'Esame orale', 2);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFADEnum', 'ESAME_PRATICO', 'Esame pratico', 3);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFADEnum', 'PROVA_SCRITTA', 'Prova scritta (comprende anche il project work, l''elaborato e le domande aperte)', 4);



INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFSCEnum', 'QUESTIONARIO', 'Questionario', 1);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFSCEnum', 'ESAME_ORALE', 'Esame orale', 2);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFSCEnum', 'ESAME_PRATICO', 'Esame pratico', 3);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFSCEnum', 'PROVA_SCRITTA', 'Prova scritta', 4);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFSCEnum', 'RELAZIONE_FIRMATA', 'Relazione firmata dal responsabile o dal coordinatore del progetto', 5);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoFSCEnum', 'RAPPORTO_CONCLUSIVO', 'Rapporto conclusivo di training individualizzato da parte del tutor', 6);



INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoRESEnum', 'QUESTIONARIO', 'Questionario (test)', 1);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoRESEnum', 'ESAME_ORALE', 'Esame orale', 2);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoRESEnum', 'ESAME_PRATICO', 'Esame pratico', 3);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoRESEnum', 'PROVA_SCRITTA', 'Prova scritta (comprende anche il project work, l''elaborato e le domande aperte)', 4);
INSERT INTO ecmdb.anagrafica_tipi(tipo, codice, descrizione, ordine) VALUES ('VerificaApprendimentoRESEnum', 'AUTOCERTFICAZIONE', 'Autocertificazione del partecipante', 5);