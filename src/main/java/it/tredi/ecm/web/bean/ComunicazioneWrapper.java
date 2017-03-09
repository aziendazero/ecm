package it.tredi.ecm.web.bean;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.ComunicazioneAmbitoEnum;
import it.tredi.ecm.dao.enumlist.ComunicazioneTipologiaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComunicazioneWrapper {

	public ComunicazioneWrapper(){}
	public ComunicazioneWrapper(Comunicazione comunicazione) {
		this.comunicazione = comunicazione;
		this.ambitoList = ComunicazioneAmbitoEnum.values();
		this.tipologiaList = ComunicazioneTipologiaEnum.values();
	}

	private Comunicazione comunicazione;
	private Map<String, Set<Account>> destinatariDisponibili;
	private ComunicazioneAmbitoEnum[] ambitoList;
	private ComunicazioneTipologiaEnum[] tipologiaList;
	private boolean canRespond;
	private boolean canCloseComunicazione;
	private ComunicazioneResponse risposta;
	private File allegatoComunicazione;
	private File allegatoRisposta;

	private HashMap<Long, Boolean> mappaVisibilitaResponse;

	//comparator tra date per il sorting
	private Comparator<ComunicazioneResponse> responseComparator = new Comparator<ComunicazioneResponse>() {
		public int compare(ComunicazioneResponse response1, ComunicazioneResponse response2) {
			if(response1.getDataRisposta().isAfter(response2.getDataRisposta()))
				return -1;
			else if(response1.getDataRisposta().isBefore(response2.getDataRisposta()))
				return 1;
			else
				return 0;
		}
	};
}
