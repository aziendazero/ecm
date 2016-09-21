package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;

public interface FieldIntegrazioneAccreditamentoRepository extends CrudRepository<FieldIntegrazioneAccreditamento, Long> {
	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoId(Long accreditamentoId);
	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoIdAndObjectReference(Long accreditamentoId, Long objectReference);
	@Query("select f.objectReference from FieldIntegrazioneAccreditamento f where f.accreditamento.id = :accreditamentoId and f.tipoIntegrazioneEnum = :tipo")
	public Set<Long> findAllByAccreditamentoIdAndTipoIntegrazioneEnum(@Param("accreditamentoId") Long accreditamentoId, @Param("tipo")TipoIntegrazioneEnum tipo);
	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoIdAndModificato(Long accreditamentoId, boolean modificato);
}
