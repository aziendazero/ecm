package it.tredi.ecm.dao.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(value = "FAD")
public class EventoFAD extends Evento{

	@Enumerated(EnumType.STRING)
	private TipologiaEventoFADEnum tipologiaEvento;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "docente_id")
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();//Sono ammessi per il RuoloPersonaEventoEnum solo DOCENTE e TUTOR

	private String razionale;
	@ElementCollection
	private List<String> risultatiAttesi = new ArrayList<String>();

	@ElementCollection(fetch = FetchType.EAGER) //TODO fastfix per demo.. sarebbe da utilizzare lazy con la annotazione in web.xml(?)
	@JoinColumn(name = "programma_fad_id")
	private List<DettaglioAttivitaFAD> programmaFAD = new ArrayList<DettaglioAttivitaFAD>();

	@ElementCollection
	private List<VerificaApprendimentoFAD> verificaApprendimento = new ArrayList<VerificaApprendimentoFAD>();

	//TODO sia FAD che RES
	private Boolean confermatiCrediti;

	private Boolean supportoSvoltoDaEsperto;

	private String materialeDurevoleRilasciatoAiPratecipanti;

	@OneToOne
	private File requisitiHardwareSoftware;

	private String userId;
	private String password;
	private String url;


	@Embedded
	private RiepilogoFAD riepilogoFAD = new RiepilogoFAD();
}
