package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Valutazione;

public interface ValutazioneRepository extends CrudRepository<Valutazione, Long> {
	Set<Valutazione> findAllByAccreditamentoId(Long accreditamentoId);
	Set<Valutazione> findAllByAccreditamentoIdAndCampoBetween(Long accreditamentoId, int start, int end);
}
