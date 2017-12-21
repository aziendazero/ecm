package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Generated;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.DestinatariEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.service.bean.CurrentUser;
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
public class Evento extends BaseEntity {
	private static Logger LOGGER = LoggerFactory.getLogger(Evento.class);

	@JsonView({JsonViewModel.EventoLookup.class})
	@SequenceGenerator(name="evento_sequence", sequenceName="evento_sequence", allocationSize=1)
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="evento_sequence")
    protected Long id;
	public Long getId() {
	        return id;
	    }

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
	@DiffIgnore
	@JsonView(JsonViewModel.EventoLookup.class)
	private String prefix;
	@DiffIgnore
	@JsonView({JsonViewModel.EventoLookup.class, EventoListDataTableModel.View.class})
	private int edizione = 1;
	@JsonView({JsonViewModel.EventoLookup.class, EventoListDataTableModel.View.class})
	public String getCodiceIdentificativo(){
		if(edizione > 1)
			return prefix + "-" + edizione;
		else return prefix;
	}

	@JsonView({JsonViewModel.EventoLookup.class, EventoListDataTableModel.View.class})
	@Enumerated(EnumType.STRING)
	private ProceduraFormativa proceduraFormativa;
	
	@JsonView(EventoListDataTableModel.View.class)
	@Transient
	private String link = "";

	@JsonView({JsonViewModel.EventoLookup.class, EventoListDataTableModel.View.class})
	@Column(columnDefinition = "text")
	private String titolo;

	@OneToOne
	private Obiettivo obiettivoNazionale;
	@OneToOne
	private Obiettivo obiettivoRegionale;

	@Column(name="anno_piano_formativo")
	private Integer pianoFormativo;

	@DiffIgnore
	@ManyToOne @JoinColumn(name = "provider_id")
	@JsonView(EventoListDataTableModel.View.class)
	private Provider provider;
	@DiffIgnore
	@ManyToOne @JoinColumn(name = "accreditamento_id")
	private Accreditamento accreditamento;

	private String professioniEvento;
	//@DiffIgnore
	@ManyToMany
	@JoinTable(name = "evento_discipline",
				joinColumns = @JoinColumn(name = "evento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	//Per Audit
//	@Transient
//	private Set<String> disciplineAudit = new HashSet<String>();

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
	@DiffIgnore
	@OneToOne
	private Evento eventoPadre;//valorizzato solo se è una riedizione di un evento
	public boolean isRiedizione(){
		return eventoPadre != null;
	}

	//false -> dopo 90gg
	//true -> dopo fineEvento
	//private boolean canAttachSponsor = true;
	//true -> dopo fineEvento
	//false -> dopo aver pagato
	//false -> se passano i 90 gg e non ha fatto nulla
	@DiffIgnore
	private Boolean sponsorUploaded = false;
	//true -> dopo pagamento (Provider B) and attachSponsor fatto
	//true -> dopo fineEvento (Provider A) and attachSponsor fatto
	//false -> dopo 90gg
//	private Boolean canDoRendicontazione = false;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name = "data_scadenza_invio_rendicontazione")//scadenza invio rendicontazione
	LocalDate dataScadenzaInvioRendicontazione;

	@Enumerated(EnumType.STRING)
	private EventoStatoEnum stato;//vedi descrizione in EventoStatoEnum
	@DiffIgnore
	private boolean validatorCheck = false; //(durante il salvataggio check di un flag per sapere se sono stati rispettati tutti i vincoli del validator)

	@DiffIgnore
	@OneToMany(mappedBy="evento", cascade=CascadeType.ALL)
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

	private Boolean pagatoQuietanza = false;

	@DiffIgnore
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
	@Enumerated(EnumType.STRING)
	private Set<DestinatariEventoEnum> destinatariEvento = new HashSet<DestinatariEventoEnum>();

	@Column(name = "contenuti_evento")
	@Enumerated(EnumType.STRING)
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

	@DiffIgnore
	@Column(name = "data_ultima_modifica")//data ultima_modifica
	private LocalDateTime dataUltimaModifica;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="responsabile_id")
	@OrderBy("id")
	private List<PersonaEvento> responsabili = new ArrayList<PersonaEvento>();

	protected Integer numeroPartecipanti;

	@OneToOne
	private File brochureEvento;

	protected Float durata;//calcolo automatico
	protected Float crediti;//calcolo con algoritmo che puo essere modificato dal provider

	private Boolean confermatiCrediti;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="responsabile_segreteria_id")
	private PersonaFullEvento responsabileSegreteria;

	@NumberFormat(pattern = "0.00")
	private BigDecimal quotaPartecipazione;

	private Boolean eventoSponsorizzato;

	private Boolean letteInfoAllegatoSponsor;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="evento_id")
	private List<Sponsor> sponsors = new ArrayList<Sponsor>();

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
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="evento_id")
	private List<Partner> partners = new ArrayList<Partner>();

	@OneToOne
	private File dichiarazioneAssenzaConflittoInteresse;

	private Boolean proceduraVerificaQualitaPercepita;

	private Boolean autorizzazionePrivacy;

	@DiffIgnore
	@OneToMany(cascade=CascadeType.ALL, mappedBy="evento", orphanRemoval=true)
	private Set<AnagrafeRegionaleCrediti> anagrafeRegionaleCrediti = new HashSet<AnagrafeRegionaleCrediti>();

	public void setAnagrafeRegionaleCrediti(Set<AnagrafeRegionaleCrediti> items){
		if(anagrafeRegionaleCrediti == null)
			anagrafeRegionaleCrediti = new HashSet<AnagrafeRegionaleCrediti>();

		if(items != null){
			for(AnagrafeRegionaleCrediti a : items){
				a.setEvento(this);
			}

			anagrafeRegionaleCrediti.clear();
			anagrafeRegionaleCrediti.addAll(items);
		}else{
			anagrafeRegionaleCrediti = new HashSet<AnagrafeRegionaleCrediti>();
		}
	}

	public void calcolaCosto() throws Exception{
		if(provider.isGruppoA()){
			costo = 0.00;
			pagato = true;
		}else if(provider.getTipoOrganizzatore().getGruppo().equalsIgnoreCase("B")){
			if(getProceduraFormativa() == ProceduraFormativa.RES || getProceduraFormativa() == ProceduraFormativa.FSC){
				costo = 258.22;
			}else{
				costo = 1500.00;
			}

			//ridotto di 1/3
			if((eventoSponsorizzato != null && !eventoSponsorizzato.booleanValue()) && (altreFormeFinanziamento != null && !altreFormeFinanziamento.booleanValue())){
				 costo = Utils.getRoundedDoubleValue((costo*2)/3, 2);
			}

		}else{
			throw new Exception("provider non classificato correttamente");
		}
	}

	public boolean canEdit(){
		if(provider.isBloccato() && !Utils.getAuthenticatedUser().isSegreteria())
			return false;
		if(stato == EventoStatoEnum.BOZZA)
			return true;
		if(stato == EventoStatoEnum.RAPPORTATO)
			return false;
		if(stato == EventoStatoEnum.CANCELLATO)
			return false;
		if(stato == EventoStatoEnum.VALIDATO){
			if(this.getProvider().isGruppoA()) {
				//ho già controllato che non sei rendicontato (Provider di gruppo A hanno pagato a true di default)
				return true;
			}
			else {
				//per i Provider di gruppo B controllo che l'evento non sia già stato pagato
				if((this.pagato != true) || Utils.getAuthenticatedUser().isSegreteria())
					return true;
				else
					return false;
			}
		}

		return false;
	}

	/*
	*	1) evento terminato
	*	2) sponsor non ancora caricati
	*	3) siamo ancora entro i 90 gg dalla fine dell'evento
	*	4) passati i 90 gg -> non è più possibile caricare gli sponsor
	*   5) la segreteria può sempre
	*/
	public boolean canDoUploadSponsor(){
		if(stato == EventoStatoEnum.BOZZA || stato == EventoStatoEnum.CANCELLATO || (eventoSponsorizzato != null && !eventoSponsorizzato.booleanValue()))
			return false;

		if(dataFine != null && LocalDate.now().isAfter(dataFine)){
			if( (dataScadenzaInvioRendicontazione != null && (sponsorUploaded == null || !sponsorUploaded.booleanValue()) && !LocalDate.now().isAfter(dataScadenzaInvioRendicontazione)) || Utils.getAuthenticatedUser().isSegreteria())
				return true;
		}

		return false;
	}


	/*
	*	1) evento terminato
	*	2) evento non è stato già pagato
	*	3) siamo ancora entro i 90 gg dalla fine dell'evento
	*	4) passati i 90 gg -> non è più possibile pagare
	*/
	public boolean canDoPagamento(){
		if(stato == EventoStatoEnum.BOZZA || stato == EventoStatoEnum.CANCELLATO)
			return false;

		if(dataFine != null && LocalDate.now().isAfter(dataFine)){
			if(pagato != null && !pagato.booleanValue() && dataScadenzaPagamento != null && !LocalDate.now().isAfter(dataScadenzaPagamento))
				return true;
		}

		return false;
	}

	/*
	 * Il tasto appare solo a evento terminato
	 * */
	public boolean canDoRendicontazione(){
		if(stato == EventoStatoEnum.BOZZA || stato == EventoStatoEnum.CANCELLATO)
			return false;

		if(dataFine != null && LocalDate.now().isAfter(dataFine))
			return true;

		return false;
	}

	// evento cancellato

	public boolean isCancellato() {
		if(stato == EventoStatoEnum.CANCELLATO)
			return true;
		return false;
	}

	/*	Per inviare al cogeaps:
	*	1) evento terminato
	*	2) tutti gli allegati dello sponsor sono stati caricati
	*	3) siamo ancora entro i 90 gg dalla fine dell'evento
	*	4) passati i 90 gg -> non è più possibile inviare al cogeaps
	*
	*/
	public boolean canDoInviaACogeaps(){
		if(stato == EventoStatoEnum.BOZZA || stato == EventoStatoEnum.CANCELLATO)
			return false;

		if(dataFine != null && LocalDate.now().isAfter(dataFine)){
			if(dataScadenzaInvioRendicontazione != null
					//o ha allegato tutti gli sponsor
					&& ((sponsorUploaded != null && sponsorUploaded.booleanValue())
							//o non è sponsorizzato per niente
							|| eventoSponsorizzato != null && !eventoSponsorizzato.booleanValue())
					&& !LocalDate.now().isAfter(dataScadenzaInvioRendicontazione) && pagato != null && pagato.booleanValue())
				return true;
		}

		return false;
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

	public boolean canSegreteriaShiftData() {
		if(stato == EventoStatoEnum.BOZZA || stato == EventoStatoEnum.CANCELLATO)
			return false;
		return true;
	}

	public String getAuditEntityType() throws Exception {
		switch(this.proceduraFormativa) {
		case FAD:
			return "EventoFAD";
		case FSC:
			return "EventoFSC";
		case RES:
			return "EventoRES";
		}
		throw new Exception("Nuovo tipo di evento: "+ this.proceduraFormativa +" NON gestito");
	}

	public void handleDateScadenza() {
		if(dataFine != null) {
			setDataScadenzaPagamento(dataFine.plusDays(90));
			setDataScadenzaInvioRendicontazione(dataFine.plusDays(90));
		}
	}
}
