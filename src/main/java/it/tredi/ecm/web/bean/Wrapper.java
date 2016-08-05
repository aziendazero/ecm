package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wrapper {
	private Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();
	private Set<FieldIntegrazioneAccreditamento> fieldIntegrazione = new HashSet<FieldIntegrazioneAccreditamento>();
}
