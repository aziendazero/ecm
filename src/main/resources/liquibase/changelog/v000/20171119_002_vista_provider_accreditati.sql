SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

CREATE OR REPLACE VIEW V_AUDIT_REPORT_PROVIDER AS 
select p.* 
from audit_report_provider p 
inner join accreditamento a 
ON p.provider_id = a.provider_id 
AND p.data_inizio between coalesce(a.data_inizio_accreditamento, now()) and 
coalesce(a.data_fine_accreditamento, now()) 
WHERE p.status in ('ACCREDITATO_PROVVISORIAMENTE', 'ACCREDITATO_STANDARD', 'VALIDATO');
