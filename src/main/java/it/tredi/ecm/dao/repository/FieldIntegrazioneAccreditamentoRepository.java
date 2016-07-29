package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;

public interface FieldIntegrazioneAccreditamentoRepository extends CrudRepository<FieldIntegrazioneAccreditamento, Long> {
	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoId(Long accreditamentoId);
}
