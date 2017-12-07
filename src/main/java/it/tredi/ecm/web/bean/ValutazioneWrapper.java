package it.tredi.ecm.web.bean;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValutazioneWrapper {
	
	private Valutazione valutazione;
	private Map<String, Map<IdFieldEnum, FieldValutazioneAccreditamento>> valutazioneSingoli;
	private Map<String, FieldValutazioniRipetibiliWrapper> valutazioneRipetibili;
	
	private Set<Account> allAccountProfileSegreteria;
	private Long accountSelected;
	private Long accreditamentoId;
}
