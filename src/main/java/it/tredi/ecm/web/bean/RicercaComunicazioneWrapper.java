package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.ComunicazioneAmbitoEnum;
import it.tredi.ecm.dao.enumlist.ComunicazioneTipologiaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RicercaComunicazioneWrapper {
	private String oggetto;
	
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataCreazioneStart;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataCreazioneEnd;
	
	private Set<ComunicazioneAmbitoEnum> ambitiSelezionati;
	private Set<ComunicazioneTipologiaEnum> tipologieSelezionate;
}
