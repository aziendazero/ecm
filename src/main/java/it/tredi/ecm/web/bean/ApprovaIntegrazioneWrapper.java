package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovaIntegrazioneWrapper{
	private long accreditamentoId;
	private Set<FieldIntegrazioneAccreditamento> oldValueList = new HashSet<FieldIntegrazioneAccreditamento>();
	private Set<FieldIntegrazioneAccreditamento> selected = new HashSet<FieldIntegrazioneAccreditamento>();
	private Set<FieldIntegrazioneAccreditamento> fullLista = new HashSet<FieldIntegrazioneAccreditamento>();
}
