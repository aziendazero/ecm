package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wrapper {
	private AccreditamentoStatoEnum statoAccreditamento;
	private Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();
	private Set<FieldIntegrazioneAccreditamento> fieldIntegrazione = new HashSet<FieldIntegrazioneAccreditamento>();
	private Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
}
