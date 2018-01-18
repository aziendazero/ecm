package it.tredi.ecm.dao.entity;

import java.util.ArrayList;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;

import it.tredi.ecm.dao.enumlist.ProgettiDiMiglioramentoFasiDaInserireFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaGruppoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaSperimentazioneFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import lombok.Getter;
import lombok.Setter;

@TypeName("EventoFSC")
@Entity
@Getter
@Setter
@DiscriminatorValue(value = "FSC")
public class EventoFSC extends Evento{
	@Embedded
	private SedeEvento sedeEvento;

	@Enumerated(EnumType.STRING)
	@Column(name="tipologia_evento_fsc")
	private TipologiaEventoFSCEnum tipologiaEventoFSC;
	@Enumerated(EnumType.STRING)
	private TipologiaGruppoFSCEnum tipologiaGruppo;
	@Enumerated(EnumType.STRING)
	private TipologiaSperimentazioneFSCEnum tipologiaSperimentazione;

	@Enumerated(EnumType.STRING)
	private ProgettiDiMiglioramentoFasiDaInserireFSCEnum fasiDaInserire;

	private Boolean sperimentazioneClinica;
	private Boolean ottenutoComitatoEtico;

	//Per calcolo crediti versione 2
	//È prevista la redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative?
	private Boolean previstaRedazioneDocumentoConclusivo;
	//È presente un Tutor esperto esterno che validi le attività del gruppo?
	private Boolean presenteTutorEspertoEsternoValidatoreAttivita;
	
	@Column(columnDefinition = "text")
	private String descrizioneProgetto;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<VerificaApprendimentoFSCEnum> verificaApprendimento;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti;

	@Column(columnDefinition = "text")
	private String indicatoreEfficaciaFormativa;

	private String fasiAzioniRuoliJson;

	@OneToMany(mappedBy="evento", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<FaseAzioniRuoliEventoFSCTypeA> fasiAzioniRuoli = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();

	@ElementCollection
	private List<RiepilogoRuoliFSC> riepilogoRuoli = new ArrayList<RiepilogoRuoliFSC>();

	@DiffIgnore
	private Integer numeroTutor;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "esperto_evento_id")
	private List<PersonaEvento> esperti = new ArrayList<PersonaEvento>();

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "coordinatore_evento_id")
	private List<PersonaEvento> coordinatori = new ArrayList<PersonaEvento>();

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "investigatore_evento_id")
	private List<PersonaEvento> investigatori = new ArrayList<PersonaEvento>();
}
