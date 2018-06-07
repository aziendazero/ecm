SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

drop table if exists ecmdb.eventofsc_verifica_apprendimento_backup;
create table ecmdb.eventofsc_verifica_apprendimento_backup as
select * from ecmdb.eventofsc_verifica_apprendimento;

update ecmdb.eventofsc_verifica_apprendimento 
set verifica_apprendimento = 'RAPPORTO_CONCLUSIVO_V1'
where eventofsc_id in ( 
select distinct e.id from ecmdb.evento e, ecmdb.eventofsc_verifica_apprendimento eva 
where e.id = eva.eventofsc_id and eva.verifica_apprendimento='RAPPORTO_CONCLUSIVO' and e.versione = 1
) and verifica_apprendimento ='RAPPORTO_CONCLUSIVO';

update ecmdb.eventofsc_verifica_apprendimento 
set verifica_apprendimento = 'RELAZIONE_FIRMATA_V1'
where eventofsc_id in ( 
select e.id from ecmdb.evento e, ecmdb.eventofsc_verifica_apprendimento eva 
where e.id = eva.eventofsc_id and eva.verifica_apprendimento='RELAZIONE_FIRMATA' and e.versione = 1
) and verifica_apprendimento ='RELAZIONE_FIRMATA';
