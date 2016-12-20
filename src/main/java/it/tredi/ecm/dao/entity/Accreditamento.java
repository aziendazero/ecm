package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Accreditamento extends BaseEntity{

	@Column(name = "tipo_domanda")
	@Enumerated(EnumType.STRING)
	private AccreditamentoTipoEnum tipoDomanda;
	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum stato;
	@Column(name = "data_invio")//invio alla segreteria (domanda non più in BOZZA)
	private LocalDate dataInvio;
	@Column(name = "data_scadenza")//limite di 180 gg per completare il procedimento
	private LocalDate dataScadenza;
	@Column(name = "data_inizio_conteggio")//data fittizia utilizzata per calcolare la reale durata del procedimento
	private LocalDate dataInizioConteggio;
	@Column(name = "durata_procedimento")//campo contenente la durata del procedimento espresso in giorni...nel caso in cui il timer viene messo in pausa dal flusso
	private Integer durataProcedimento;

	@Column(name = "data_valutazione_crecm")//la data in cui il gruppo CRECM termina la valutazione e il flusso avanza
	private LocalDate dataValutazioneCrecm;
	@Column(name = "data_ins_odg")
	private LocalDate dataInserimentoOdg;//domanda inserita in odg della prossima seduta
	@Column(name = "data_valutazione_commissione")
	private LocalDate dataValutazioneCommissione;//domanda discussa dalla commissione ECM
	@Column(name = "data_richiesta_integrazione")
	private LocalDate dataRichiestaIntegrazione;//domanda rispedita al provider per integrazioni
	@Column(name = "data_integrazione")
	private LocalDate dataIntegrazione;//domanda rispedita alla segreteria in seguito alle integrazioni del provider
	@Column(name = "data_inizio_accreditamento")
	private LocalDate dataInizioAccreditamento;//procedimento terminato..con l'accettazione dell'accreditamento...in attesa del pagamento
	@Column(name = "data_fine_accreditamento")//data di scadenza calcolata dall'accreditamento +4anni
	private LocalDate dataFineAccreditamento;

	private Long giorniIntegrazione;
	private Long giorniPreavvisoRigetto;
	private Boolean integrazioneEseguitaDaProvider;
	private Boolean preavvisoRigettoEseguitoDaProvider;

	//Contiene la data in cui il documento (Integrazione/Rigetto/Diniego/Accreditamento) viene inviato al protocollo
	//viene settato a null quando si ottiene conferma della protocollazione
	//viene usato per ottenere gli accreditamenti in protocollazzione da troppo tempo
	@Column(name = "dataora_invio_protocollazione")
	private LocalDateTime dataoraInvioProtocollazione;

	@JoinColumn(name = "provider_id")
	@OneToOne(fetch = FetchType.LAZY)
	private Provider provider;

	@JoinColumn(name = "dati_accreditamento_id")
	@OneToOne(fetch = FetchType.LAZY, cascade= CascadeType.REMOVE)
	private DatiAccreditamento datiAccreditamento;

	@OneToMany(mappedBy = "accreditamento", cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private List<FieldEditabileAccreditamento> idEditabili = new ArrayList<FieldEditabileAccreditamento>();

	@OneToOne
	private PianoFormativo pianoFormativo;

	@OneToOne
	private GruppoCrecm gruppoCrecm;

	//@OneToOne
	//private TeamValutazione teamValutazione;

	@OneToMany(mappedBy = "accreditamento")
	Set<ValutazioneCommissione> valutazioniCommissione = new HashSet<ValutazioneCommissione>();

	@OneToOne
	private File NoteOsservazioniIntegrazione;
	@OneToOne
	private File NoteOsservazioniPreavvisoRigetto;
	@OneToOne
	private File decretoAccreditamento;
	@OneToOne
	private File decretoDiniego;
	@OneToOne
	private File richiestaIntegrazione;
	@OneToOne
	private File richiestaPreavvisoRigetto;
	@OneToOne
	private File verbaleValutazioneSulCampoPdf;

	//@OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OneToOne(mappedBy = "accreditamento", cascade=CascadeType.ALL)
	private VerbaleValutazioneSulCampo verbaleValutazioneSulCampo;

	@Embedded
	private WorkflowInfo workflowInfoAccreditamento = null;

	public Accreditamento(){}
	public Accreditamento(AccreditamentoTipoEnum tipoDomanda){
		this.tipoDomanda = tipoDomanda;
		this.stato = AccreditamentoStatoEnum.BOZZA;
	}

	public void enableAllIdField(){
		//PROVIDER FIELD
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.PROVIDER)){
			if((id != IdFieldEnum.PROVIDER__CODICE_FISCALE) && !(id == IdFieldEnum.PROVIDER__PARTITA_IVA && this.getProvider().isHasPartitaIVA()))
				idEditabili.add(new FieldEditabileAccreditamento(id, this));
		}

		//SEDI
		for(Sede s : this.getProvider().getSedi()){
			for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.SEDE))
				idEditabili.add(new FieldEditabileAccreditamento(id, this, s.getId()));
		}

		//LEGALE RAPPRESENTANTE
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.LEGALE_RAPPRESENTANTE))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//DELEGATO LEGALE RAPPRESENTANTE
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//DATI ACCREDITAMENTO
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.DATI_ACCREDITAMENTO))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//RESPONSABILE SEGRETERIA
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_SEGRETERIA))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//RESPONSABILE AMMINISTRATIVO
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//RESPONSABILE SISTEMA INFORMATICO
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//RESPONSABILE SISTEMA QUALITA
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.RESPONSABILE_QUALITA))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//COMPONENTI COMITATO SCIENTIFICO
		for(Persona p : this.getProvider().getPersone()){
			if(p.isComponenteComitatoScientifico()){
				for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO))
					idEditabili.add(new FieldEditabileAccreditamento(id, this, p.getId()));
			}
		}

		//FULL
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.FULL))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

		//ALLEGATI ACCREDITAMENTO
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO))
			idEditabili.add(new FieldEditabileAccreditamento(id, this));

	}

	public boolean isProvvisorio(){
		return tipoDomanda == AccreditamentoTipoEnum.PROVVISORIO;
	}

	public boolean isStandard(){
		return tipoDomanda == AccreditamentoTipoEnum.STANDARD;
	}

	public boolean isBozza(){
		return stato == AccreditamentoStatoEnum.BOZZA;
	}

	public boolean isValutazioneSegreteriaAssegnamento(){
		return stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO;
	}

	public boolean isAssegnamento(){
		return stato == AccreditamentoStatoEnum.ASSEGNAMENTO;
	}

	public boolean isValutazioneSegreteria() {
		return stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA;
	}

	public boolean isValutazioneCrecm() {
		return stato == AccreditamentoStatoEnum.VALUTAZIONE_CRECM;
	}

	public boolean isInsOdg() {
		return stato == AccreditamentoStatoEnum.INS_ODG;
	}

	public boolean isValutazioneCommissione() {
		return stato == AccreditamentoStatoEnum.VALUTAZIONE_COMMISSIONE;
	}

	public boolean isIntegrazione() {
		return stato == AccreditamentoStatoEnum.INTEGRAZIONE;
	}

	public boolean isPreavvisoRigetto() {
		return stato == AccreditamentoStatoEnum.PREAVVISO_RIGETTO;
	}

	public boolean isRichiestaIntegrazione() {
		return stato == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE;
	}

	public boolean isRichiestaPreavvisoRigetto() {
		return stato == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO;
	}

	public boolean isValutazioneSulCampo() {
		return stato == AccreditamentoStatoEnum.VALUTAZIONE_SUL_CAMPO;
	}

	public boolean isValutazioneTeamLeader() {
		return stato == AccreditamentoStatoEnum.VALUTAZIONE_TEAM_LEADER;
	}

	public boolean isAccreditato(){
		return stato == AccreditamentoStatoEnum.ACCREDITATO;
	}

	public boolean isDiniego(){
		return stato == AccreditamentoStatoEnum.DINIEGO;
	}

	public boolean isCancellato(){
		return stato == AccreditamentoStatoEnum.CANCELLATO;
	}

	public boolean isProcedimentoAttivo(){
		if(dataScadenza != null && (dataScadenza.isAfter(LocalDate.now()) || dataScadenza.isEqual(LocalDate.now())) )
			return true;
		return false;
	}

	public boolean isDomandaAttiva(){
		if(dataFineAccreditamento != null && (dataFineAccreditamento.isAfter(LocalDate.now()) || dataFineAccreditamento.isEqual(LocalDate.now())) )
			return true;
		return false;
	}

	public boolean hasPianoFormativo(){
		return (pianoFormativo != null && !pianoFormativo.isNew());
	}

	public int getDurataProcedimento(){
		if(durataProcedimento == null)
			return new Long(ChronoUnit.DAYS.between(dataInizioConteggio, LocalDate.now())).intValue();
		else
		{
			return durataProcedimento;
		}
	}

	//nel caso in cui si stoppa il conteggio...salviamo momentaneamente la durata già trascorsa
	public void standbyConteggio(){
		durataProcedimento = getDurataProcedimento();
		dataInizioConteggio = null;
	}

	//nel caso in cui riparte il conteggio...azzero la variabile durataProcedimento e setto una dataInizioConteggio coerente
	public void restartConteggio(){
		dataInizioConteggio = LocalDate.now().minusDays(durataProcedimento);
		durataProcedimento = null;
	}

	public long getFileIdForProtocollo(){
		for(File f : datiAccreditamento.getFiles())
			if(f.isDICHIARAZIONELEGALE())
				return f.getId();
		return 0L;
	}

	public File getFileForProtocollo(){
		for(File f : datiAccreditamento.getFiles())
			if(f.isDICHIARAZIONELEGALE())
				return f;
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Accreditamento entitapiatta = (Accreditamento) o;
		return Objects.equals(id, entitapiatta.id);
	}

}
