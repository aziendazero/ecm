package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import it.tredi.ecm.dao.enumlist.EsecutoreStatoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.StatoWorkflowEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoWorkflowEnum;
import it.tredi.ecm.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Accreditamento extends BaseEntityDefaultId {

	@Column(name = "tipo_domanda")
	@Enumerated(EnumType.STRING)
	private AccreditamentoTipoEnum tipoDomanda;
	//20180403 il setter è stato fatto a mano per gestire la data scadenza correttamente
	@Setter(AccessLevel.NONE)
	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum stato;
	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum statoVariazioneDati;
	@Column(name = "data_invio")//invio alla segreteria (domanda non più in BOZZA) viene settata in inviaDomandaAccreditamento
	private LocalDate dataInvio;
	@Column(name = "data_scadenza")//limite di 180 gg per completare il procedimento
	//20180403 la data scdenza non è impostabile internamente sul cambio stato ed il suo valore calcolato ad hoc cedi getDataScadenza
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private LocalDate dataScadenza;// la data scadenza entro cui il procedimento deve essere completato

	//20180403 data non più utilizzata ora viene registrata al suo posto la data scadenza
//	@Column(name = "data_inizio_conteggio")//data fittizia utilizzata per calcolare la reale durata del procedimento
//	private LocalDate dataInizioConteggio;
	@Column(name = "durata_procedimento")//campo contenente la durata del procedimento espresso in giorni...nel caso in cui il timer viene messo in pausa dal flusso
	private Integer durataProcedimento = null;
	@Column(name = "massima_durata_procedimento")//campo contenente la massima durata del procedimento espresso in giorni (per il valore vedi AccreditamentoService.massimaDurataProcedimento) salvato nel caso venga modificato il valore da 180 giorni
	private Integer massimaDurataProcedimento = null;

	@Column(name = "data_valutazione_crecm")//la data in cui il gruppo CRECM termina la valutazione e il flusso avanza
	private LocalDate dataValutazioneCrecm;
	@Column(name = "data_ins_odg")
	private LocalDate dataInserimentoOdg;//domanda inserita in odg della prossima seduta
	@Column(name = "data_valutazione_commissione")
	private LocalDate dataValutazioneCommissione;//domanda discussa dalla commissione ECM

	@Column(name = "data_integrazione_inizio")
	private LocalDate dataIntegrazioneInizio;//domanda rispedita al provider per integrazioni
	@Column(name = "data_integrazione_fine")
	private LocalDate dataIntegrazioneFine;//domanda rispedita alla segreteria in seguito alle integrazioni del provider

	@Column(name = "data_preavviso_rigetto_inizio")
	private LocalDate dataPreavvisoRigettoInizio;//domanda rispedita al provider per integrazioni preavviso rigetto
	@Column(name = "data_preavviso_rigetto_fine")
	private LocalDate dataPreavvisoRigettoFine;//domanda rispedita alla segreteria dal provider in seguito alle integrazioni su preavviso rigetto

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
	private File letteraAccompagnatoriaAccreditamento;
	@OneToOne
	private File letteraAccompagnatoriaDiniego;
	@OneToOne
	private File richiestaIntegrazione;
	@OneToOne
	private File richiestaPreavvisoRigetto;
	@OneToOne
	private File verbaleValutazioneSulCampoPdf;
	@OneToOne
	private File valutazioneSulCampoAllegato1;
	@OneToOne
	private File valutazioneSulCampoAllegato2;
	@OneToOne
	private File valutazioneSulCampoAllegato3;

	@Column(name = "presa_visione_integrazione")//flag presa visione dell'integrazione
	private Boolean presaVisioneIntegrazione;
	@Column(name = "presa_visione_preavviso_di_rigetto")//flag presa visione del preavviso di rigetto
	private Boolean presaVisionePreavvisoDiRigetto;

	//@OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OneToOne(mappedBy = "accreditamento", cascade=CascadeType.ALL)
	private VerbaleValutazioneSulCampo verbaleValutazioneSulCampo;

	@Embedded
	private WorkflowInfo workflowInfoAccreditamento = null;

	@ElementCollection
	private List<WorkflowInfo> workflowInfo = new ArrayList<WorkflowInfo>();

	@OneToOne
	private File fileDecadenza;

	// ERM014776
	// domanda chiusa DINIEGO/SOSPENSIONE/DECADENZA
	@Column(name = "data_chiusura_accreditamento")
	private LocalDate dataChiusuraAcc;

	public Accreditamento(){}
	public Accreditamento(AccreditamentoTipoEnum tipoDomanda){
		this.tipoDomanda = tipoDomanda;
		this.stato = AccreditamentoStatoEnum.BOZZA;
	}


	public void enableAllIdField(){
		//PROVIDER FIELD
		for(IdFieldEnum id :  IdFieldEnum.getAllForSubset(SubSetFieldEnum.PROVIDER)){
			if((id != IdFieldEnum.PROVIDER__CODICE_FISCALE) && !(id == IdFieldEnum.PROVIDER__PARTITA_IVA && this.getProvider().isHasPartitaIVA())){
				if(this.tipoDomanda == AccreditamentoTipoEnum.PROVVISORIO ||
						(this.tipoDomanda == AccreditamentoTipoEnum.STANDARD && id != IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE && id != IdFieldEnum.PROVIDER__PARTITA_IVA)){
					idEditabili.add(new FieldEditabileAccreditamento(id, this));
				}
			}
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

	public boolean isValutazioneCommissioneVariazioneDati() {
		return statoVariazioneDati == AccreditamentoStatoEnum.VALUTAZIONE_COMMISSIONE;
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

	public boolean isRichiestaIntegrazioneInAttesaDiFirma() {
		return stato == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE_IN_FIRMA;
	}

	public boolean isRichiestaPreavvisoRigetto() {
		return stato == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO;
	}

	public boolean isRichiestaPreavvisoRigettoInAttesaDiFirma() {
		return stato == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO_IN_FIRMA;
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

	public boolean isAccreditatoInAttesaDiFirma(){
		return stato == AccreditamentoStatoEnum.ACCREDITATO_IN_FIRMA;
	}

	public boolean isDiniegoInAttesaDiFirma(){
		return stato == AccreditamentoStatoEnum.DINIEGO_IN_FIRMA;
	}

	public boolean isCancellato(){
		return stato == AccreditamentoStatoEnum.CANCELLATO;
	}

	public boolean isVariazioneDati(){
		return statoVariazioneDati != null && stato == AccreditamentoStatoEnum.ACCREDITATO;
	}

	public boolean isModificaDati() {
		return statoVariazioneDati != null && statoVariazioneDati == AccreditamentoStatoEnum.INTEGRAZIONE;
	}

	public boolean isAbilitaCampiDati(){
		return statoVariazioneDati != null && statoVariazioneDati == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE;
	}

	public boolean isValutazioneSegreteriaVariazioneDati() {
		return statoVariazioneDati != null && statoVariazioneDati == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA;
	}

	public boolean isValutazioneCrecmVariazioneDati() {
		return statoVariazioneDati != null && statoVariazioneDati == AccreditamentoStatoEnum.VALUTAZIONE_CRECM;
	}

	public boolean isAssegnamentoCrecmVariazioneDati() {
		return statoVariazioneDati != null && statoVariazioneDati == AccreditamentoStatoEnum.ASSEGNAMENTO;
	}

	//prima la data scadenza veniva settata in inviaDomandaAccreditamento
	public boolean isProcedimentoAttivo(){
		//if(dataScadenza != null && this.isStatoValido() && (dataScadenza.isAfter(LocalDate.now()) || dataScadenza.isEqual(LocalDate.now())) )
		//20180403 dataScadenza non è più valorizzato quando l'accreditamento + in gestione al provider
		if(this.isStatoValido() && getGiorniRestantiProcedimento() != null && getGiorniRestantiProcedimento() >= 0 )
			return true;
		return false;
	}

	private boolean isStatoValido() {
		switch(stato) {
		case DINIEGO:
			return false;
		case CONCLUSO:
			return false;
		case CANCELLATO:
			return false;
		case ACCREDITATO:
			return false;
		default:
			return true;
		}
	}

	public boolean isDomandaAttiva(){
		if(dataFineAccreditamento != null && (dataFineAccreditamento.isAfter(LocalDate.now()) || dataFineAccreditamento.isEqual(LocalDate.now())) )
			return true;
		return false;
	}

	public boolean isDomandaInRichiestaAccreditamento() {
		if(this.getWorkflowInfoAccreditamento() != null
				&& this.getWorkflowInfoAccreditamento().getStato() == StatoWorkflowEnum.IN_CORSO
				&& this.getWorkflowInCorso().getTipo() == TipoWorkflowEnum.ACCREDITAMENTO)
			return true;
		return false;
	}

	public boolean hasPianoFormativo(){
		return (pianoFormativo != null && !pianoFormativo.isNew());
	}

	public boolean canEdit() {
		Account user = Utils.getAuthenticatedUser().getAccount();
		if(user.isSegreteria() ||
				(user.isProvider() && (this.isBozza()
					|| this.isIntegrazione()
					|| this.isPreavvisoRigetto()
					|| this.isModificaDati())
					&& !this.getProvider().isBloccato()))
			return true;
		return false;
	}

	public LocalDate getDataScadenza() {
		//le domande importate dal vecchio sistema non hanno la dataScadenza
		if(dataScadenza == null) {
			if(durataProcedimento != null) {
				//in capo al provider
				if(massimaDurataProcedimento == null)//uso il valore di massima durata di 180
					return LocalDate.now().plusDays(180 - durataProcedimento);
				else
					return LocalDate.now().plusDays(massimaDurataProcedimento - durataProcedimento);
			}
		} else {
			//in capo alla segreteria
			return dataScadenza;
		}
		return null;
	}

	/**
	 * Restituisce i giorni restanti per completare il procedimento, calcolati considerando quelli in carico alla segreteria
	 * per accreditamenti avviati, per accreditamenti mai avviati restituisce null
	 * @return
	 */
	private Integer getGiorniRestantiProcedimento() {
		if(massimaDurataProcedimento == null || getDurataProcedimento() == null)
			return null;
		//getDurataProcedimento() restituisce il numero di giorni trascorsi dalla domanda in carico alla segreteria
		//sono questi giorni a non poter superare AccreditamentoService.massimaDurataProcedimento (180 giorni)
		//quindi i giorni rimanenti sono dati da getGiorniRestantiProcedimento()
		return massimaDurataProcedimento - getDurataProcedimento();
	}

	public void setStato(AccreditamentoStatoEnum nuovoStato, TipoWorkflowEnum tipoWorkflow) {
		//Gestisco la durata del procedimento
		if(this.stato != nuovoStato) {
			if(tipoWorkflow != null
					&&
					(tipoWorkflow == TipoWorkflowEnum.ACCREDITAMENTO || tipoWorkflow == TipoWorkflowEnum.DECADENZA)
					&&
					nuovoStato != AccreditamentoStatoEnum.BOZZA) {
				if(nuovoStato == AccreditamentoStatoEnum.ACCREDITATO || nuovoStato == AccreditamentoStatoEnum.DINIEGO || nuovoStato == AccreditamentoStatoEnum.CANCELLATO) {
					//gestisco gli stati di conclusione flusso
					stopConteggioDataScadenza();
				} else {
					//l'esecutore dello stato è cambiato
					if(this.stato.getEsecutoreStato() != nuovoStato.getEsecutoreStato()) {
						//ora i possibili EsecutoreStatoEnum sono solo 2 quindi
						if(this.stato.getEsecutoreStato() == EsecutoreStatoEnum.PROVIDER) {
							//è passato da PROVIDER a SEGRETERIA
							startRestartConteggio();
						} else {
							//è passato da SEGRETERIA a PROVIDER
							standbyConteggio();
						}
					}
				}
			}
		}
		this.stato = nuovoStato;
	}

	/**
	 * Restituisce la durata del procedimento per accreditamenti avviati considerando solo i giorni trascorsi in carico alla segreteria
	 * per accreditamenti mai avviati restituisce null
	 * @return
	 */
	private Integer getDurataProcedimento(){
		if(durataProcedimento == null) {
			if(dataScadenza == null || massimaDurataProcedimento == null)
				return null;
			else
				return massimaDurataProcedimento - new Long(ChronoUnit.DAYS.between(LocalDate.now(), dataScadenza)).intValue();
		} else {
			return durataProcedimento;
		}
	}

	private void stopConteggioDataScadenza(){
		if(dataScadenza == null) {
			startRestartConteggio();
		}
	}

	//nel caso in cui si stoppa il conteggio...salviamo momentaneamente la durata già trascorsa
	private void standbyConteggio(){
		durataProcedimento = getDurataProcedimento();
		dataScadenza = null;
	}

	//nel caso in cui riparte il conteggio...azzero la variabile durataProcedimento e setto una dataInizioConteggio coerente
	private void startRestartConteggio(){
		if(durataProcedimento == null)
			dataScadenza = LocalDate.now().plusDays(massimaDurataProcedimento);
		else
			dataScadenza = LocalDate.now().plusDays(massimaDurataProcedimento - durataProcedimento);
		durataProcedimento = null;
	}

	public long getFileIdForProtocollo(){
		if(isProvvisorio()){
			for(File f : datiAccreditamento.getFiles())
				if(f.isDICHIARAZIONELEGALE())
					return f.getId();
		}else {
			for(File f : datiAccreditamento.getFiles())
				if(f.isRICHIESTAACCREDITAMENTOSTANDARD())
					return f.getId();
		}
		return 0L;
	}

	public Set<Long> getFileIdsAllegatiForProtocollo() {
		Set<Long> result = new HashSet<Long>();
		for(File f : this.getDatiAccreditamento().getFiles()){
			if(f != null && !f.isNew()) {
				if(f.getTipo() != FileEnum.FILE_ORGANIGRAMMA &&
						f.getTipo() != FileEnum.FILE_FUNZIONIGRAMMA &&
						f.getTipo() != FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO &&
						f.getTipo() != FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE && (
							(isStandard() || f.getTipo() != FileEnum.FILE_DICHIARAZIONE_LEGALE) &&
							(isProvvisorio() || f.getTipo() != FileEnum.FILE_RICHIESTA_ACCREDITAMENTO_STANDARD))) {
					result.add(f.getId());
				}
			}
		};
		return result;
	}

	public File getFileForProtocollo(){
		if(isProvvisorio()){
			for(File f : datiAccreditamento.getFiles())
				if(f.isDICHIARAZIONELEGALE())
					return f;
		}else {
			for(File f : datiAccreditamento.getFiles())
				if(f.isRICHIESTAACCREDITAMENTOSTANDARD())
					return f;
		}
		return null;
	}

	public WorkflowInfo getWorkflowInCorso() {
		if(getWorkflowInfoAccreditamento() != null && getWorkflowInfoAccreditamento().getStato() == StatoWorkflowEnum.IN_CORSO)
			return getWorkflowInfoAccreditamento();
		for(WorkflowInfo wf : getWorkflowInfo()){
			if(wf.getStato() == StatoWorkflowEnum.IN_CORSO)
				return wf;
		}
		return null;
	}

	public AccreditamentoStatoEnum getStatoForWorkflow() {
		WorkflowInfo wfi = getWorkflowInCorso();
		if(wfi.getTipo() == TipoWorkflowEnum.VARIAZIONE_DATI)
			return getStatoVariazioneDati();
		return getStato();
	}

	public AccreditamentoStatoEnum getCurrentStato() {
		if(this.isVariazioneDati()) {
			return this.getStatoVariazioneDati();
		}
		else return this.getStato();
	}

	public AccreditamentoStatoEnum getStatoUltimaIntegrazione() {
		if(this.isVariazioneDati())
			return AccreditamentoStatoEnum.INTEGRAZIONE;
		if(this.getDataIntegrazioneInizio() == null)
			return null;
		if(this.getDataPreavvisoRigettoInizio() == null)
			return AccreditamentoStatoEnum.INTEGRAZIONE;
		else
			return AccreditamentoStatoEnum.PREAVVISO_RIGETTO;
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
	public Set<Long> getAllWorkflowProcessInstanceIdVariazioneDati() {
		Set<Long> allWorkflowProcessInstanceIdVariazioneDati = new HashSet<Long>();
		for(WorkflowInfo wfi : this.getWorkflowInfo()) {
			if(wfi.getTipo() == TipoWorkflowEnum.VARIAZIONE_DATI)
				allWorkflowProcessInstanceIdVariazioneDati.add(wfi.getProcessInstanceId());
		}
		return allWorkflowProcessInstanceIdVariazioneDati;
	}
	public Set<Long> getAllWorkflowProcessInstanceIdTermineProcedimento() {
		Set<Long> allWorkflowProcessInstanceIdTermineProcedimento = new HashSet<Long>();
		for(WorkflowInfo wfi : this.getWorkflowInfo()) {
			if(wfi.getTipo() == TipoWorkflowEnum.DECADENZA)
				allWorkflowProcessInstanceIdTermineProcedimento.add(wfi.getProcessInstanceId());
		}
		return allWorkflowProcessInstanceIdTermineProcedimento;
	}

	public boolean isStoriaFlussoPresente(){
		if(workflowInfoAccreditamento != null && workflowInfoAccreditamento.getProcessInstanceId() != null)
			return true;

		if(workflowInfo != null && !workflowInfo.isEmpty())
			return true;

		return false;
	}

}
