package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.DestinatariEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
*  Una volta validato l'evento può essere modificato con le seguenti restrizioni:
*  PROVIDER A (<= fino) (>= dal)
*  	+ (DataInizioEvento - 4) >= Now() => tutto tranne ANTICIPO dataEvento
*  	+ (DataInizioEvento - 3) <= Now() => tutto bloccato tranne campi Docenti
*  	+ (DataInizioEvento) <= Now() => tutto bloccato
*
*  PROVIDER B
*  	+ (DataInizioEvento - 30) >= Now() => tutto modificabile anche dataInizioEvento (ma limite minimo è Now() + 10) per non corrompere i controlli successivi
* 		+ (DataInizioEvento - 10) >= Now() => tutto tranne ANTICIPO dataEvento
*  	+ (DataInizioEvento - 9) <= Now() => tutto bloccato tranne campi Docenti
*  	+ (DataInizioEvento) <= Now() => tutto bloccato
*
*  RENDICONTAZIONE
*  	+ (Now() > dataFineEvento && Now() <= dataFineEvento + 90)
*  	+ Inserimento sponsor effettuato
*
*  INSERIMENTO SPONSOR
*  	+ (Now() <= dataFineEvento + 90)
*
* */
@Entity
@Table(name = "evento")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "eventoType")
@NamedEntityGraphs({

	@NamedEntityGraph(name="graph.evento.forRiedizione",
			attributeNodes = {@NamedAttributeNode("id"),
					@NamedAttributeNode(value="brochureEvento", subgraph="fileFull")},
//					@NamedAttributeNode(value="autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", subgraph="fileFull"),
//					@NamedAttributeNode(value="autocertificazioneAutorizzazioneMinisteroSalute", subgraph="fileFull"),
//					@NamedAttributeNode(value="autocertificazioneAssenzaFinanziamenti", subgraph="fileFull"),
//					@NamedAttributeNode(value="contrattiAccordiConvenzioni", subgraph="fileFull"),
//					@NamedAttributeNode(value="dichiarazioneAssenzaConflittoInteresse", subgraph="fileFull")},
			subgraphs = {@NamedSubgraph(name="fileFull", attributeNodes={
					@NamedAttributeNode("id"),
					@NamedAttributeNode("nomeFile"),
					@NamedAttributeNode("tipo"),
					@NamedAttributeNode(value="fileData", subgraph="fileData")
			}), @NamedSubgraph(name="fileData", attributeNodes={
					@NamedAttributeNode("id"),
					@NamedAttributeNode("data")
			})
			}
	)

})

public class Evento extends BaseEntity{
	private static Logger LOGGER = LoggerFactory.getLogger(Evento.class);
	/*
	 * PREFIX[-edizione]
	 * 	PREFIX=
	 * 		*EVENTO inserito in PIANO FORMATIVO -> COD_PROVIDER-ID_EVENTO (quando lo creo nel piano formativo)
	 *
	 * 		*EVENTO inserito in EVENTI a partire da evento in piano formativo -> codiceIdentificativo dell'evento di partenza
	 *
	 * 		*EVENTO inserito da nuovo in EVENTI -> COD_PROVIDER-ID_EVENTO
	 *
	 * 		*EVENTO inserito come RIEDIZIONE -> PREFIX dell'evento padre + #edizione
	 * */
	private String prefix;
	private int edizione = 1;
	public String getCodiceIdentificativo(){
		if(edizione > 1)
			return prefix + "-" + edizione;
		else return prefix;
	}

	@Enumerated(EnumType.STRING)
	private ProceduraFormativa proceduraFormativa;
	private String titolo;

	@OneToOne
	private Obiettivo obiettivoNazionale;
	@OneToOne
	private Obiettivo obiettivoRegionale;

	@Column(name="anno_piano_formativo")
	private Integer pianoFormativo;

	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;
	@ManyToOne @JoinColumn(name = "accreditamento_id")
	private Accreditamento accreditamento;

	private String professioniEvento;
	@ManyToMany
	@JoinTable(name = "evento_discipline",
				joinColumns = @JoinColumn(name = "evento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		if(discipline != null){
			for(Disciplina d : discipline)
				professioniSelezionate.add(d.getProfessione());
		}
		return professioniSelezionate;
	}
	/**	Fine sezione in comune con EventoPianoFormativo	**/

	/**	Inizio sezione Eventi	**/
	@OneToOne
	private Evento eventoPadre;//valorizzato solo se è una riedizione di un evento
	public boolean isRiedizione(){
		return eventoPadre != null;
	}

	//false -> dopo 90gg
	//true -> dopo fineEvento
	private boolean canAttachSponsor = true;
	//true -> dopo fineEvento
	//false -> dopo aver pagato
	//false -> se passano i 90 gg e non ha fatto nulla
	private boolean canDoPagamento = false;
	//true -> dopo pagamento (Provider B) and attachSponsor fatto
	//true -> dopo fineEvento (Provider A) and attachSponsor fatto
	//false -> dopo 90gg
	private boolean canDoRendicontazione = false;

	@Enumerated(EnumType.STRING)
	private EventoStatoEnum stato;//vedi descrizione in EventoStatoEnum
	private boolean validatorCheck = false; //(durante il salvataggio check di un flag per sapere se sono stati rispettati tutti i vincoli del validator)

	@OneToMany(mappedBy="evento")
	@OrderBy("data_invio DESC")
	private Set<RendicontazioneInviata> inviiRendicontazione = new HashSet<RendicontazioneInviata>();

	/**	Utilizzati da Engineering **/
	private Double costo = 0.00;
	private Boolean pagato = false;
	private Boolean pagInCorso = false;

	/**	Utilizzati per invio Cogeaps	**/
	@OneToOne
	private File reportPartecipantiXML;
	@OneToOne
	private File reportPartecipantiCSV;

	@OneToOne //valorizzato solo se è la realizzazione di un evento descritto nel piano formativo
	private EventoPianoFormativo eventoPianoFormativo;
	public boolean isEventoDaPianoFormativo(){
		return eventoPianoFormativo != null;
	}

	public void setFromEventoPianoFormativo(EventoPianoFormativo epf){
		this.setEventoPianoFormativo(epf);
		this.titolo = epf.getTitolo();
		this.proceduraFormativa = epf.getProceduraFormativa();
		this.obiettivoNazionale = epf.getObiettivoNazionale();
		this.obiettivoRegionale = epf.getObiettivoRegionale();
		this.provider = epf.getProvider();
		this.accreditamento = epf.getAccreditamento();
		this.professioniEvento = epf.getProfessioniEvento();
		this.discipline = epf.getDiscipline();
		this.prefix = epf.getCodiceIdentificativo();
		this.edizione = 1;
		this.pianoFormativo = epf.getPianoFormativo();
	}

	public void buildPrefix(){
		if(isEventoDaPianoFormativo() && eventoPianoFormativo != null){
			//evento inserito a partire dall'evento corrispondente in piano formativo
			this.prefix = eventoPianoFormativo.getPrefix();
		}else if(isRiedizione()){
			//evento inserito come riedizione di un evento
			this.prefix = eventoPadre.getPrefix();
		}else {
			this.prefix = provider.getCodiceIdentificativoUnivoco() + "-" + this.id;
		}
	}

	@ElementCollection
	private Set<DestinatariEventoEnum> destinatariEvento = new HashSet<DestinatariEventoEnum>();
	private ContenutiEventoEnum contenutiEvento;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name = "data_inizio")//inizio evento
	private LocalDate dataInizio;
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name = "data_fine")//fine evento
	private LocalDate dataFine;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name = "data_scadenza_pagamento")//data scadenza pagamento
	private LocalDate dataScadenzaPagamento;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="responsabile_id")
	private List<PersonaEvento> responsabili = new ArrayList<PersonaEvento>();

	protected Integer numeroPartecipanti;

	@OneToOne
	private File brochureEvento;

	protected Float durata;//calcolo automatico
	protected Float crediti;//calcolo con algoritmo che puo essere modificato dal provider

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="responsabile_segreteria_id")
	private PersonaFullEvento responsabileSegreteria = new PersonaFullEvento();

	private BigDecimal quotaPartecipazione;

	private Boolean eventoSponsorizzato;

	private Boolean letteInfoAllegatoSponsor;

	@OneToMany(cascade=CascadeType.MERGE, orphanRemoval=true)
	@JoinColumn(name="evento_id")
	private Set<Sponsor> sponsors = new HashSet<Sponsor>();

	private Boolean eventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia;
	@OneToOne
	private File autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia;
	@OneToOne
	private File autocertificazioneAutorizzazioneMinisteroSalute;

	private Boolean altreFormeFinanziamento;
	@OneToOne
	private File autocertificazioneAssenzaFinanziamenti;
	@OneToOne
	private File contrattiAccordiConvenzioni;

	private Boolean eventoAvvalePartner;
	@OneToMany(cascade=CascadeType.MERGE, orphanRemoval=true)
	@JoinColumn(name="evento_id")
	private Set<Partner> partners = new HashSet<Partner>();

	@OneToOne
	private File dichiarazioneAssenzaConflittoInteresse;

	private Boolean proceduraVerificaQualitaPercepita;

	private Boolean autorizzazionePrivacy;

	public void calcolaCosto() throws Exception{
		if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("A")){
			costo = 0.00;
			pagato = true;
		}else if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("B")){
			if(getProceduraFormativa() == ProceduraFormativa.RES || getProceduraFormativa() == ProceduraFormativa.FSC){
				costo = 258.22;
			}else{
				costo = 1500.00;
			}

			//ridotto di 1/3
			if(altreFormeFinanziamento != null && !altreFormeFinanziamento.booleanValue()){
				 costo = Utils.getRoundedDoubleValue((costo*2)/3);
			}
		}else{
			throw new Exception("provider non classificato correttamente");
		}

		if(dataFine != null)
			setDataScadenzaPagamento(dataFine.plusDays(90));
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Evento entitapiatta = (Evento) o;
        return Objects.equals(id, entitapiatta.id);
    }

	public RendicontazioneInviata getUltimaRendicontazioneInviata() {
		return inviiRendicontazione.size() == 0? null : inviiRendicontazione.iterator().next();
	}
}
