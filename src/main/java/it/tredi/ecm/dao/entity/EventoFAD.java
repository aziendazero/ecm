package it.tredi.ecm.dao.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(value = "FAD")
public class EventoFAD extends Evento{

	private TipologiaEventoFADEnum tipologiaEvento;

	@OneToMany(mappedBy="eventoDocente")
	private Set<PersonaEvento> docenti = new HashSet<PersonaEvento>();//Sono ammessi per il RuoloPersonaEventoEnum solo DOCENTE e TUTOR

	private String razionale;
	@ElementCollection
	private List<String> risultatiAttesi = new ArrayList<String>();
	@ElementCollection
	@OrderBy("orario ASC")
	private List<DettaglioAttivitaFAD> programma = new ArrayList<DettaglioAttivitaFAD>();

	@ElementCollection
	private Set<VerificaApprendimentoFAD> verificaApprendimento;

	//TODO sia FAD che RES
	private boolean confermatiCrediti;

	private boolean supportoSvoltoDaEsperto;

	private String materialeDurevoleRilasciatoAiPratecipanti;

	@OneToOne
	private File requisitiHardwareSoftware;

	private String userId;
	private String password;
	private String url;

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
