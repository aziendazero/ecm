SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

update ecmdb.obiettivo SET nome = 'Epidemologia - prevenzione e promozione della salute con acquisizione di nozioni tecnico-professionali. (10)' where id=1039;
update ecmdb.obiettivo SET nome = 'Sicurezza alimentare e/o patologie correlate. (23)' where id=1045;
update ecmdb.obiettivo SET nome = 'Sicurezza ambientale e/o patologie correlate. (26)' where id=1046;

update ecmdb.obiettivo SET nome = 'La comunicazione efficace interna, esterna, con paziente. La privacy ed il consenso informato. (7)' where id=1059;
update ecmdb.obiettivo SET nome = 'Applicazione nella pratica quotidiana dei principi e delle procedure dell''evidence based practice (EBM - EBN - EBP). (1)' where id=1063;

update ecmdb.obiettivo SET nome = 'Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale per la formazione continua e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie con acquisizioni di nozioni tecnico-professionali. (20)' where id=1052;
update ecmdb.obiettivo SET nome = 'Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale per la formazione continua e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie con acquisizioni di nozioni di processo. (32)', codice_cogeaps = '32' where id=1062;
update ecmdb.obiettivo SET nome = 'Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale per la formazione continua e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie con acquisizioni di nozioni di sistema. (33)', codice_cogeaps = '33' where id=1070;

insert into ecmdb.obiettivo VALUES (1071,'DI_PROCESSO','14',true,'Accreditamento strutture sanitarie e dei professionisti. La cultura della qualita’. (14)');
insert into ecmdb.obiettivo VALUES (1072,'DI_PROCESSO','30',true,'Epidemologia - prevenzione e promozione della salute con acquisizione di nozioni di processo. (30)');
insert into ecmdb.obiettivo VALUES (1073,'DI_SISTEMA','31',true,'Epidemologia - prevenzione e promozione della salute con acquisizione di nozioni di sistema. (31)');