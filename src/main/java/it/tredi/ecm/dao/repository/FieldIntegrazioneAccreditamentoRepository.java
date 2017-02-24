package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;

public interface FieldIntegrazioneAccreditamentoRepository extends CrudRepository<FieldIntegrazioneAccreditamento, Long> {
	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoId(Long accreditamentoId);
	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoIdAndObjectReference(Long accreditamentoId, Long objectReference);
//	@Query("select f.objectReference from FieldIntegrazioneAccreditamento f where f.accreditamento.id = :accreditamentoId and f.tipoIntegrazioneEnum = :tipo")
//	public Set<Long> findAllByAccreditamentoIdAndTipoIntegrazioneEnum(@Param("accreditamentoId") Long accreditamentoId, @Param("tipo")TipoIntegrazioneEnum tipo);
//	public Set<FieldIntegrazioneAccreditamento> findAllByAccreditamentoIdAndModificato(Long accreditamentoId, boolean modificato);
	public void deleteAllByAccreditamentoId(Long accreditamentoId);
	@Query(value = "SELECT fi.* FROM ecmdb.field_integrazione_accreditamento fi \r\n" +
			"	INNER JOIN ecmdb.field_integrazione_history_container fihc ON fihc.id = fi.field_integrazione_history_container_id \r\n" +
			"	INNER JOIN ecmdb.field_valutazione_accreditamento fv ON fi.id_field = fv.id_field AND fi.object_reference = fv.object_reference AND fi.accreditamento_id = fv.accreditamento_id \r\n" +
			"	INNER JOIN ecmdb.valutazione_valutazioni vv ON vv.valutazioni_id = fv.id\r\n" +
			"	INNER JOIN ecmdb.valutazione v ON vv.valutazione_id = v.id \r\n" +
			"	WHERE fv.enabled = true AND fv.esito = true AND v.storicizzato = false AND v.tipo_valutazione = 'SEGRETERIA_ECM' AND fi.accreditamento_id = :accreditamentoId \r\n" +
			"   	AND fihc.accreditamento_id = :accreditamentoId AND fihc.stato = :stato AND fihc.work_flow_process_instance_id = :workFlowProcessInstanceId AND fihc.applicato = false"	, nativeQuery = true)
	public Set<FieldIntegrazioneAccreditamento> findAllApprovedBySegreteria(@Param("accreditamentoId") Long accreditamentoId, @Param("stato") String stato, @Param("workFlowProcessInstanceId") Long workFlowProcessInstanceId);
}
