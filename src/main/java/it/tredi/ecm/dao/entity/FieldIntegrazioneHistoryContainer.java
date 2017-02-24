package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FieldIntegrazioneHistoryContainer extends BaseEntity {

	public FieldIntegrazioneHistoryContainer() {}
	public FieldIntegrazioneHistoryContainer(Long accreditamentoId, AccreditamentoStatoEnum stato, Long processInstanceId) {
		this.accreditamentoId = accreditamentoId;
		this.stato = stato;
		this.workFlowProcessInstanceId = processInstanceId;
	}

	private static final long serialVersionUID = 4457924659387916452L;

	private Long accreditamentoId;

	private Long workFlowProcessInstanceId;

	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum stato;

	private boolean applicato;

	@OneToMany
	@JoinColumn(name = "field_integrazione_history_container_id")
	private Set<FieldIntegrazioneAccreditamento> integrazioni = new HashSet<FieldIntegrazioneAccreditamento>();

}
