package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Obiettivo;

public interface ObiettivoRepository extends CrudRepository<Obiettivo, Long> {
	public Set<Obiettivo> findAll();
	public Set<Obiettivo> findAllByNazionale(boolean nazionale);
	Set<Obiettivo> findAllByCodiceCogeapsAndNazionale(String codiceCogeaps, boolean nazionale);
	Set<Obiettivo> findAllByNazionaleAndVersioneNotOrderByCodiceCogeapsAsc(boolean nazionale, int Versione);
	Set<Obiettivo> findAllByNazionaleAndVersioneOrderByCodiceCogeapsAsc(boolean nazionale, int Versione);
	Obiettivo findOneByCodiceCogeapsAndNazionaleAndVersione(String codiceCogeaps, boolean nazionale,int Versione);
}
