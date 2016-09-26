package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.Map;

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
	private Map<String, Account> destinatariMap = new HashMap<String, Account>();
	private ComunicazioneAmbitoEnum[] ambitoList;
	private ComunicazioneTipologiaEnum[] tipologiaList;
	private boolean canRespond;
	private ComunicazioneResponse risposta;
	private File allegatoComunicazione;
}
