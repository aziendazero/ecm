SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ecmdb, pg_catalog;

UPDATE ecmdb.evento_piano_formativo epf SET anno_piano_formativo_nativo = (
								SELECT t.anno FROM (
											SELECT epf.id, min(pfa.anno_piano_formativo) as anno
											FROM ecmdb.evento_piano_formativo epf,
												ecmdb.piano_formativo pfa,
												ecmdb.piano_formativo_eventi_piano_formativo pfaE
											WHERE pfaE.eventi_piano_formativo_id = epf.id AND pfaE.piano_formativo_id = pfa.id
											GROUP BY (epf.id)
											) as t
										WHERE t.id = epf.id
								);