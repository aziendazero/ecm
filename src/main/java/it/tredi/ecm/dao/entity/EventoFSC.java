package it.tredi.ecm.dao.entity;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(value = "FSC")
public class EventoFSC extends Evento{
	@Embedded
	private SedeEvento sedeEvento;

	private TipologiaEventoFSCEnum tipologiaEvento;
	private Boolean sperimentazioneClinica;
	private Boolean ottenutoComitatoEtico;
	
	private String descrizioneProgetto;

	@ElementCollection
	private Set<VerificaApprendimentoFSCEnum> verificaApprendiemento;

	@ElementCollection
	private Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti;

	private String indicatoreEfficaciaFormativa;
	
	
	//TODO FASI AZIONI RUOLI
	
	//TODO campo 20

	public float calcoloDurata(){
		float durata = 0.0f;
		//TODO
		return durata;
	}
	
	public float calcoloCreditiFormativi(){
		float crediti = 0.0f;
		//TODO
		return crediti;
	}
	
}
