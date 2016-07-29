package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;

public interface FieldValutazioneAccreditamentoRepository extends CrudRepository<FieldValutazioneAccreditamento, Long> {
	Set<FieldValutazioneAccreditamento> findAllByAccreditamentoId(Long accreditamentoId);
}
