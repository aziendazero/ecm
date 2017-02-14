package it.tredi.ecm.dao.repository;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;

public interface ValutazioneCommissioneRepository extends CrudRepository<ValutazioneCommissione, Long>{

	void deleteOneByAccreditamentoAndSedutaLockedFalse(Accreditamento accreditamento);

}
