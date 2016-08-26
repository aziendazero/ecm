package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Valutazione;

public interface ValutazioneRepository extends CrudRepository<Valutazione, Long>{
	public Valutazione findOne(Long id);
	public Valutazione findOneByAccreditamentoIdAndAccountId(Long accreditamentoId, Long accountId);
	public Set<Valutazione> findAllByAccreditamentoIdOrderByDataValutazioneAsc(Long accreditamentoId);
	public Set<Valutazione> findAllByAccreditamentoIdAndDataValutazioneNotNullOrderByDataValutazioneAsc(Long accreditamentoId);
}
