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
-- Data for Name: obiettivo; Type: TABLE DATA; Schema: ecmdb; Owner: ecm
--

INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1024, NULL, '1', false, 'Non rientra in uno degli obiettivi regionali');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1025, NULL, '2', false, 'Appropriatezza delle prestazioni sanitarie in conformità ai LEA');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1026, NULL, '3', false, 'Governo clinico, qualità e sicurezza del paziente');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1027, NULL, '4', false, 'Adozione di linee guida basate sull’evidenza, qualità dei sistemi e dei processi clinico assistenziali');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1028, NULL, '5', false, 'Umanizzazione delle cure/relazione/comunicazione');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1029, NULL, '6', false, 'Cure palliative e terapia del dolore');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1030, NULL, '7', false, 'Prevenzione e presa in carico della persona affetta da cronicità');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1031, NULL, '8', false, 'Integrazione professionale tra ospedale e territorio, con sviluppo e implementazione dei PDTA (prioritariamente su BPCO, scompenso cardiaco, fibrillazione atriale, diabete, sclerosi multipla)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1032, NULL, '9', false, 'Malattia di Alzheimer e altri tipi di declino cognitivo e demenze');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1033, NULL, '10', false, 'Promozione corretti stili di vita');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1034, NULL, '11', false, 'Promozione dell’appropriatezza prescrittiva dei farmaci');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1035, NULL, '12', false, 'Interventi socio-sanitari (famiglia, infanzia, adolescenza, giovani, anziani, disabilità, dipendenze, salute mentale)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1036, NULL, '13', false, 'Cultura del lavoro in team multiprofessionale e adozioni di modelli di lavoro in rete');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1037, NULL, '14', false, 'Sicurezza degli operatori nell''ambiente di lavoro (T.U. 81/2008)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1038, NULL, '15', false, 'Valorizzazione delle risorse umane');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1039, 'TECNICO_PROFESSIONALI', '10', true, 'Epidemiologia- prevenzione e primozione della salute. (ob.10)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1040, 'TECNICO_PROFESSIONALI', '17', true, 'Argomenti di carattere generale: informatica e lingua inglese scientifica di livello avanzato. (17)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1041, 'TECNICO_PROFESSIONALI', '18', true, 'Contenuti tecnico-professionali (conoschenze e competenze) specifici di ciascuna professione, di ciascuna specializzazione e di ciascuna attivita ultraspecialistica. malattie rare. (18)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1042, 'TECNICO_PROFESSIONALI', '19', true, 'Medicine non convenzionali: valutazione dell’efficacia in ragione degli esiti e degli ambniti di complementarieta’. (19)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1043, 'TECNICO_PROFESSIONALI', '21', true, 'Trattamento del dolore acuto e cronico. palliazione. (21)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1044, 'TECNICO_PROFESSIONALI', '22', true, 'Fragilita’ (minori, anziani, tossico-dipendenti. salute mentale): tutela degli aspetti assistenziali e socio-assistenziali. (22)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1045, 'TECNICO_PROFESSIONALI', '23', true, 'Sicurezza alimentare. (23)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1046, 'TECNICO_PROFESSIONALI', '26', true, 'Sicurezza ambientale. (26)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1047, 'TECNICO_PROFESSIONALI', '27', true, 'Sicurezza negli ambienti e nei luoghi di lavoro e patologie correlate.(27)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1048, 'TECNICO_PROFESSIONALI', '24', true, 'Sanita’ veterinaria. (24)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1049, 'TECNICO_PROFESSIONALI', '25', true, 'Farmacoepidemiologia, farmacoeconomia, farmacovigilanza.(25)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1050, 'TECNICO_PROFESSIONALI', '28', true, 'Implementazione della cultura e della sicurezza in ,ateire di donazione trapianto.(28)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1051, 'TECNICO_PROFESSIONALI', '29', true, 'Innovazione tecnologicca: valutazione. miglioramento dei processi di gestione delle tecnologie biomediche e dei dispositivi medici. Health technology assessment.(29)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1052, 'TECNICO_PROFESSIONALI', '20', true, 'Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale ECM e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie.(20)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1053, 'DI_PROCESSO', '3', true, 'Documentazione clinica, percorsi clinico-assistenziali diagnostici e riabilitativi, profili di assistenza-profili di cura. (ob.3)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1054, 'DI_PROCESSO', '4', true, 'Appropriatezza prestazioni sanitarie nei LEA. Sistemi di valutazione, verifica e miglioramento dell’efficienza e dell’efficacia. (4)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1055, 'DI_PROCESSO', '8', true, 'Integrazione interprofessionale e multiprofessionale, interistituzionale. (8)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1056, 'DI_PROCESSO', '9', true, 'Integrazione fra assistenza territoriale ed ospedaliera. (9)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1057, 'DI_PROCESSO', '11', true, 'Management sanitario. innovazione gestionale e sperimentazione di modelli organizzativi e gestionali. (11)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1058, 'DI_PROCESSO', '12', true, 'Aspetti relazionali comunicazione interna, esterna, con paziente e umanizzazione delle cure. (12)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1059, 'DI_PROCESSO', '7', true, 'La comunicazione efficace la privacy ed il consenso informato. (7)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1060, 'DI_PROCESSO', '13', true, 'Metoddologia e tecniche di comunicazione sociale per lo sviluppo dei programmi nazionali e regionali di prevenzione primaria. (13)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1061, 'DI_PROCESSO', '15', true, 'Multiculturalita’ e cultura dell’accoglienza nell’attivita’ sanitaria. (15)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1062, 'DI_PROCESSO', '20', true, 'Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale ECM e dalle regioni/province autonome per far fronte a specficfiche emergenze sanitarie. (20)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1063, 'DI_SISTEMA', '1', true, 'Applicazione nella pratica quotidiana dei principi e delle prodcedure dell’evidenze based practice (EBM. EBN. EBP). (ob.1)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1064, 'DI_SISTEMA', '2', true, 'Linee guida protocolli-procedure. (ob.2)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1065, 'DI_SISTEMA', '5', true, 'Principi, procedure e strumenti per il governo clinico delle attività sanitarie. (5)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1066, 'DI_SISTEMA', '6', true, 'La sicurezza del paziente. risk management. (6)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1067, 'DI_SISTEMA', '10', true, 'Epidemiologia – prevenziaone e promozione della salute. (10)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1068, 'DI_SISTEMA', '16', true, 'Etica, bioetica e deontologia. (16)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1069, 'DI_SISTEMA', '17', true, 'Argomenti di carattere generale: informatica ed ingleser scientifico livello avanzato; normativa in materia sanitaria: i principi etici e civili del ssn. (17)');
INSERT INTO obiettivo (id, categoria, codice_cogeaps, nazionale, nome) VALUES (1070, 'DI_SISTEMA', '20', true, 'Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale ECM e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie. (20)');

