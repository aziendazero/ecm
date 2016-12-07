SET search_path = ecmdb, pg_catalog;

INSERT INTO ecmdb.dati_accreditamento_files (dati_accreditamento_id, files_id)
SELECT a.dati_accreditamento_id, pF.files_id FROM ecmdb.accreditamento as a
INNER JOIN ecmdb.provider_files as pF ON a.provider_id = pF.provider_id where a.dati_accreditamento_id is not null;