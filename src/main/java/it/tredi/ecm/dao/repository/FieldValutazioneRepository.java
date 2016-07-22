package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.FieldValutazione;

public interface FieldValutazioneRepository extends CrudRepository<FieldValutazione, Long> {
	Set<FieldValutazione> findAllByAccreditamentoId(Long accreditamentoId);
}
