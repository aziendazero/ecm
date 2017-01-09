package it.tredi.ecm.dao.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OrderBy;

import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(value = "FAD")
public class EventoFAD extends Evento{

	@Enumerated(EnumType.STRING)
	@Column(name = "tipologia_evento_fad")
	private TipologiaEventoFADEnum tipologiaEventoFAD;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "docente_id")
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();//Sono ammessi per il RuoloPersonaEventoEnum solo DOCENTE e TUTOR

	@Column(columnDefinition="text")
	private String razionale;
	@ElementCollection
	@Column(columnDefinition="text")
	private List<String> risultatiAttesi = new ArrayList<String>();

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "eventofad_id")
	private List<DettaglioAttivitaFAD> programmaFAD = new ArrayList<DettaglioAttivitaFAD>();

	@ElementCollection
	private List<VerificaApprendimentoFAD> verificaApprendimento = new ArrayList<VerificaApprendimentoFAD>();

	private Boolean supportoSvoltoDaEsperto;

	@Column(columnDefinition="text")
	private String materialeDurevoleRilasciatoAiPratecipanti;

	@OneToOne
	private File requisitiHardwareSoftware;

	private String userId;
	private String password;
	private String url;


	@Embedded
	private RiepilogoFAD riepilogoFAD = new RiepilogoFAD();
}
