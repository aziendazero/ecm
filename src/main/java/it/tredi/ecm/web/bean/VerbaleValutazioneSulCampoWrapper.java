package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.Map;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerbaleValutazioneSulCampoWrapper extends Wrapper {

	private Accreditamento accreditamento;
	private VerbaleValutazioneSulCampo verbale;
	private Map<String, String> mappaErroriValutazione = new HashMap<String, String>();

}
