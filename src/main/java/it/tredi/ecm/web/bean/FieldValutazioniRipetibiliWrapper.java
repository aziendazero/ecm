package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldValutazioniRipetibiliWrapper {

	private String subset;
	private Map<String, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutazioni = new HashMap<String, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();

}