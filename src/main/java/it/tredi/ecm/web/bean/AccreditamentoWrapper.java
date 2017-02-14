package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoWrapper {

	//dati domanda di accreditamento
	private Accreditamento accreditamento;

	//dati provider
	private Provider provider;

	private Sede sedeLegale;
	private Set<Sede> sedi = new HashSet<Sede>();

	private Persona legaleRappresentante;
	private Persona delegatoLegaleRappresentante;

	//dati domanda accreditamento
	private DatiAccreditamento datiAccreditamento;

	//dati anagrafiche interne provider
	private Persona responsabileSegreteria;
	private Persona responsabileAmministrativo;
	private Persona coordinatoreComitatoScientifico;
	private List<Persona> componentiComitatoScientifico = new ArrayList<Persona>();
	private Persona responsabileSistemaInformatico;
	private Persona responsabileQualita;

	/*	flag per stato avanzamento domanda	*/
	private boolean providerStato;
	private boolean sediStato;
	private boolean tutteSediValutate = true;
	private boolean legaleRappresentanteStato;
	private boolean delegatoLegaleRappresentanteStato;

	private boolean tipologiaFormativaStato;
	private boolean datiEconomiciStato;
	private boolean datiStrutturaStato;

	private boolean responsabileSegreteriaStato;
	private boolean responsabileAmministrativoStato;
	private boolean responsabileSistemaInformaticoStato;
	private boolean responsabileQualitaStato;

	private boolean comitatoScientificoStato;
	private boolean tuttiComponentiValutati = true;
	private String comitatoScientificoErrorMessage;

	private boolean attoCostitutivoStato;
	private boolean esperienzaFormazioneStato;
	private boolean utilizzoStato;
	private boolean sistemaInformaticoStato;
	private boolean pianoQualitaStato;
	private boolean dichiarazioneLegaleStato;
	private boolean dichiarazioneEsclusioneStato;
	private boolean richiestaAccreditamentoStandardStato;
	private boolean relazioneAttivitaFormativaStato;

	//flag per abilitare disabilitare il pulsante valida
	private boolean canValidateProvider;
	private boolean canValidateLegale;
	private boolean canValidateDelegato;
	private boolean canValidateTipologiaFormativa;
	private boolean canValidateDatiEconomici;
	private boolean canValidateDatiStruttura;
	private boolean canValidateResponsabileSegreteria;
	private boolean canValidateResponsabileAmministrativo;
	private boolean canValidateResponsabileSistemaInformatico;
	private boolean canValidateResponsabileQualita;
	private boolean canValidateAttoCostitutivo;
	private boolean canValidateEsperienzaFormazione;
	private boolean canValidateUtilizzo;
	private boolean canValidateSistemaInformatico;
	private boolean canValidatePianoQualita;
	private boolean canValidateDichiarazioneLegale;
	private boolean canValidateDichiarazioneEsclusione;
	private boolean canValidateRichiestaAccreditamentoStandard;
	private boolean canValidateRelazioneAttivitaFormativa;
	private Map<Long, Boolean> mappaCanValidateSedi = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> mappaCanValidateComitato = new HashMap<Long, Boolean>();

	private boolean valutazioneSulCampoStato = false;
	private boolean sottoscriventeStato;

	private boolean tuttiEventiValutati = true;

	private boolean sezione1Stato;
	private boolean sezione2Stato;
	private boolean sezione3Stato;
	private boolean sezione4Stato;

	private boolean canSendValutazione;
	private boolean canSendIntegrazione;

	//stati per i pulsanti segreteria
	private boolean canPrendiInCarica;
	private boolean canValutaDomanda;
	private boolean canShowValutazione;
	private boolean canShowValutazioneRiepilogo;
	private boolean canAssegnaNuovoGruppo;
	private boolean canConfermaValutazione;
	private boolean canPresaVisione;
	private boolean canEnableField;
	private boolean canVariazioneDati;
	private boolean canSendVariazioneDati;
	private boolean canValutaVariazioneDati;
	private boolean canShowValutazioneStorico;


	//flag per vedere se la segreteria può editare
	private boolean canSegreteriaEdit;

	//int referee da riassegnare
	private int refereeDaRiassegnare = 0;

	//boolean Stati dei multistanza
	private Map<Long, Boolean> componentiComitatoScientificoStati = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> sediStati = new HashMap<Long, Boolean>();
	private boolean coordinatoreComitatoScientificoStato;
	private boolean sedeLegaleStato;
	//TODO togliere se confermato che non serve più
	//private Map<Long, Boolean> eventiStati = new HashMap<Long, Boolean>();

	//Mappe con i FieldValutazione
	private Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa;
	private Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaComponenti;
	private Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaSedi;
	private Map<IdFieldEnum, FieldValutazioneAccreditamento> mappaCoordinatore;
	private Map<IdFieldEnum, FieldValutazioneAccreditamento> mappaSedeLegale;
	//TODO togliere se confermato che non serve più
	//private Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaEventi;

	//Gruppo dei referee
	private Set<Account> refereeGroup = new HashSet<Account>();

	//Referee per la variazione dati
	private Account refereeVariazioneDati;

	//Valutazione Complessiva
	private String valutazioneComplessiva;

	//Valutazioni per l'accreditamento
	private Set<Valutazione> valutazioniList = new HashSet<Valutazione>();

	//Valutazione dell'utente corrente
	private Valutazione valutazioneCurrentUser;

	//Elenco MultiIstanza aggiunti e eliminati durante Integrazione
	private Set<Long> aggiunti = new HashSet<Long>();
	private Set<Long> eliminati = new HashSet<Long>();

	//File allegati integrazione
	private File noteOsservazioniIntegrazione;
	private File noteOsservazioniPreavvisoRigetto;

	//File import pianoFormativo da csv
	private File importEventiDaCsvFile;

	//blocco informazioni per modale di inserimento verbale sul campo
	private VerbaleValutazioneSulCampo verbaleValutazioneSulCampo;
	private Set<Account> componentiCRECM;
	private Set<Account> osservatoriRegionali;
	private Set<Account> componentiSegreteria;
	private Set<Account> referentiInformatici;
	private Set<Sede> sediProvider;
	private File delegaValutazioneSulCampo;
	private File cartaIdentita;

	//file pdf del verbale valutazione sul campo firmato
	private File verbalePdfFirmato;

	//info di destinazione della domanda standard
	private AccreditamentoStatoEnum destinazioneStatoDomandaStandard;

	//info di destinazione della variazione dati
	private AccreditamentoStatoEnum destinazioneVariazioneDati;

	public AccreditamentoWrapper(){};
	public AccreditamentoWrapper(Accreditamento accreditamento){
		setAllAccreditamento(accreditamento);
	}

	public void setAllAccreditamento(Accreditamento accreditamento) {
		this.setAccreditamento(accreditamento);

		// PROVIDER
		this.setProvider(accreditamento.getProvider());

		//SEDI
		for(Sede s : accreditamento.getProvider().getSedi()) {
			if(s.isSedeLegale())
				this.setSedeLegale(s);
			else
				this.getSedi().add(s);
		}

		//DATI ACCREDITAMENTO
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();
		this.setDatiAccreditamento(datiAccreditamento != null ? datiAccreditamento : new DatiAccreditamento());

		// LEGALE RAPPRESENTANTE E RESPONSABILI
		for(Persona p : accreditamento.getProvider().getPersone()){
			if(p.isLegaleRappresentante())
				this.setLegaleRappresentante(p);
			else if(p.isDelegatoLegaleRappresentante())
				this.setDelegatoLegaleRappresentante(p);
			else if(p.isResponsabileSegreteria())
				this.setResponsabileSegreteria(p);
			else if(p.isResponsabileAmministrativo())
				this.setResponsabileAmministrativo(p);
			else if(p.isResponsabileSistemaInformatico())
				this.setResponsabileSistemaInformatico(p);
			else if(p.isResponsabileQualita())
				this.setResponsabileQualita(p);
			else if(p.isCoordinatoreComitatoScientifico())
				this.setCoordinatoreComitatoScientifico(p);
			else if(p.isComponenteComitatoScientifico())
				this.getComponentiComitatoScientifico().add(p);
		}
	}


	public void checkStati(int numeroComponentiComitatoScientifico, int numeroProfessionistiSanitarie, Set<Professione> elencoProfessioniDeiComponenti, int professioniDeiComponentiAnaloghe,Set<String> filesDelProvider, AccreditamentoWrapperModeEnum mode, Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList){
		//TODO migliorare la logica per evitare di fare troppi if
		// ad esempio inizializzare gli stati a true e poi ad ogni controllo se fallisce si mette il false sia allo stato che al valid
		// cosi facendo valid è settato in automatico senza rifare tutti i controlli
		//
		//NON lo faccio adesso perchè voglio capire in fase di validazione della domanda come gestiremo i vari stati

		//check sulla modalità di visualizzazione
		if(mode == AccreditamentoWrapperModeEnum.SHOW || mode == AccreditamentoWrapperModeEnum.EDIT) {
			providerStato = (provider.getRagioneSociale()!= null) ? true : false;

			//check inserimento sede Legale
			sedeLegaleStato = (sedeLegale != null && !sedeLegale.isNew()) ? true : false;

			//check sul cv unico campo non settabile in registrazione o in modifica delle anagrafiche, ma solo durante la domanda di accreditamento
			legaleRappresentanteStato = (legaleRappresentante != null && !legaleRappresentante.isNew() && legaleRappresentante.getAnagrafica().getCellulare() != null && !legaleRappresentante.getAnagrafica().getCellulare().isEmpty()) ? true : false;
			delegatoLegaleRappresentanteStato = (delegatoLegaleRappresentante != null && !delegatoLegaleRappresentante.isNew() && delegatoLegaleRappresentante.getAnagrafica().getCellulare() != null && !delegatoLegaleRappresentante.getAnagrafica().getCellulare().isEmpty()) ? true : false;

			if(accreditamento.getDatiAccreditamento() != null){
				tipologiaFormativaStato = accreditamento.getDatiAccreditamento().isTipologiaFormativaInserita();
				datiEconomiciStato = accreditamento.getDatiAccreditamento().isDatiEconomiciInseriti();
				datiStrutturaStato = accreditamento.getDatiAccreditamento().isDatiStrutturaInseriti();
			}else{
				tipologiaFormativaStato = false;
				datiEconomiciStato = false;
				datiStrutturaStato = false;
			}

			responsabileSegreteriaStato = (responsabileSegreteria != null && !responsabileSegreteria.isNew()) ? true : false;
			responsabileAmministrativoStato = (responsabileAmministrativo != null && !responsabileAmministrativo.isNew()) ? true : false;
			responsabileSistemaInformaticoStato = (responsabileSistemaInformatico != null && !responsabileSistemaInformatico.isNew()) ? true : false;
			responsabileQualitaStato = (responsabileQualita != null && !responsabileQualita.isNew()) ? true : false;

			checkComitatoScientifico_fromDB(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, elencoProfessioniDeiComponenti, professioniDeiComponentiAnaloghe);
			setFilesStato(filesDelProvider);

			sezione1Stato = (providerStato && sedeLegaleStato && legaleRappresentanteStato && tipologiaFormativaStato && datiEconomiciStato && datiStrutturaStato) ? true : false;
			sezione2Stato = (responsabileSegreteriaStato && responsabileAmministrativoStato && responsabileSistemaInformaticoStato && responsabileQualitaStato && comitatoScientificoStato) ? true : false;
			sezione3Stato = (attoCostitutivoStato && (esperienzaFormazioneStato || !accreditamento.getDatiAccreditamento().getDatiEconomici().hasFatturatoFormazione()) && utilizzoStato && sistemaInformaticoStato && pianoQualitaStato && dichiarazioneLegaleStato) ? true : false;
			if(accreditamento.isStandard())
				sezione3Stato = sezione3Stato && richiestaAccreditamentoStandardStato && relazioneAttivitaFormativaStato;
		}

		if(mode == AccreditamentoWrapperModeEnum.VALIDATE) {

			boolean hasIntegrazioneField = (fieldIntegrazioneList == null || fieldIntegrazioneList.isEmpty()) ? false : true;

			providerStato = (
				(mappa.containsKey(IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE) && mappa.get(IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE) && mappa.get(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__PARTITA_IVA) && mappa.get(IdFieldEnum.PROVIDER__PARTITA_IVA).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__CODICE_FISCALE) && mappa.get(IdFieldEnum.PROVIDER__CODICE_FISCALE).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__RAGIONE_SOCIALE) && mappa.get(IdFieldEnum.PROVIDER__RAGIONE_SOCIALE).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__EMAIL_STRUTTURA) && mappa.get(IdFieldEnum.PROVIDER__EMAIL_STRUTTURA).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__NATURA_ORGANIZZAZIONE) && mappa.get(IdFieldEnum.PROVIDER__NATURA_ORGANIZZAZIONE).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.PROVIDER__NO_PROFIT) && mappa.get(IdFieldEnum.PROVIDER__NO_PROFIT).getEsito() != null)
				);
			if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL) != null)
				legaleRappresentanteStato = (mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL).getEsito() != null);
			else
				legaleRappresentanteStato =	(
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__COGNOME) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__COGNOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__NOME) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__NOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__CODICEFISCALE) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__CODICEFISCALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__CELLULARE) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__CELLULARE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__EMAIL) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__EMAIL).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__PEC) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__PEC).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__ATTO_NOMINA) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__ATTO_NOMINA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__CV) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__CV).getEsito() != null)
				);
			if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL) != null)
				delegatoLegaleRappresentanteStato = (mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL).getEsito() != null);
			else
				delegatoLegaleRappresentanteStato = (
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__NOME) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__NOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CV) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CV).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA).getEsito() != null)
				);
			tipologiaFormativaStato = (
				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO).getEsito() != null) &&
 				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE).getEsito() != null) &&
 				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE).getEsito() != null)
				);
			datiEconomiciStato = (
				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO).getEsito() != null) &&
 				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO)  && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO).getEsito() != null) &&
 				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE).getEsito() != null) &&
				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE).getEsito() != null)
				);
			datiStrutturaStato = (
				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI).getEsito() != null) &&
 				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA).getEsito() != null) &&
 				(mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA) && mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA).getEsito() != null)
 				);
			if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL) != null)
				responsabileSegreteriaStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL).getEsito() != null);
			else
				responsabileSegreteriaStato = (
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__COGNOME) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__COGNOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__NOME) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__NOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__CODICEFISCALE) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__CODICEFISCALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__TELEFONO) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__TELEFONO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__EMAIL) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__EMAIL).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__ATTO_NOMINA) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__ATTO_NOMINA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__CV) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__CV).getEsito() != null)
				);
			if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL) != null)
				responsabileAmministrativoStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL).getEsito() != null);
			else
				responsabileAmministrativoStato = (
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__COGNOME) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__COGNOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__NOME) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__NOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__TELEFONO) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__TELEFONO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__EMAIL) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__EMAIL).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CV) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CV).getEsito() != null)
				);
			if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL) != null)
				responsabileSistemaInformaticoStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL).getEsito() != null);
			else
				responsabileSistemaInformaticoStato = (
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__COGNOME) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__COGNOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__NOME) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__NOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__EMAIL) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__EMAIL).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CV) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CV).getEsito() != null)
				);
			if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, IdFieldEnum.RESPONSABILE_QUALITA__FULL) != null)
				responsabileQualitaStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__FULL).getEsito() != null);
			else
				responsabileQualitaStato = (
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__COGNOME) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__COGNOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__NOME) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__NOME).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__CODICEFISCALE) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__CODICEFISCALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__TELEFONO) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__TELEFONO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__EMAIL) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__EMAIL).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__ATTO_NOMINA) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__ATTO_NOMINA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__CV) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__CV).getEsito() != null)
				);
			attoCostitutivoStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO).getEsito() != null;
			esperienzaFormazioneStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE).getEsito() != null;
			utilizzoStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO).getEsito() != null;
			sistemaInformaticoStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO).getEsito() != null;
			pianoQualitaStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA).getEsito() != null;
			dichiarazioneLegaleStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE).getEsito() != null;
			dichiarazioneEsclusioneStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE).getEsito() != null;
			richiestaAccreditamentoStandardStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD).getEsito() != null;
			relazioneAttivitaFormativaStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA) && mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA).getEsito() != null;

			if(accreditamento.isValutazioneSulCampo()) {
				valutazioneSulCampoStato = (
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__PIANO_FORMATIVO) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__PIANO_FORMATIVO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__IDONEITA_SEDE) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__IDONEITA_SEDE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__RELAZIONE_ANNUALE) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__RELAZIONE_ANNUALE).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__PERCEZIONE_INTERESSE_COMMERICALE_SANITA) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__PERCEZIONE_INTERESSE_COMMERICALE_SANITA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__SCHEDA_QUALITA_PERCEPITA) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__SCHEDA_QUALITA_PERCEPITA).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__PRESENZA_PARTECIPANTI) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__PRESENZA_PARTECIPANTI).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__RECLUTAMENTO_DIRETTO) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__RECLUTAMENTO_DIRETTO).getEsito() != null) &&
					(mappa.containsKey(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__VERIFICA_APPRENDIMENTO) && mappa.get(IdFieldEnum.VALUTAZIONE_SUL_CAMPO__VERIFICA_APPRENDIMENTO).getEsito() != null)
					);
				if(verbaleValutazioneSulCampo != null && verbaleValutazioneSulCampo.getIsPresenteLegaleRappresentante() != null) {
					if(verbaleValutazioneSulCampo.getIsPresenteLegaleRappresentante()) {
							sottoscriventeStato = true;
					}
					else {
						if(verbaleValutazioneSulCampo.getDelegato().getNome() != null && !verbaleValutazioneSulCampo.getDelegato().getNome().isEmpty()
								&& verbaleValutazioneSulCampo.getDelegato().getCognome() != null && !verbaleValutazioneSulCampo.getDelegato().getCognome().isEmpty()
								&& verbaleValutazioneSulCampo.getDelegato().getCodiceFiscale() != null && !verbaleValutazioneSulCampo.getDelegato().getCodiceFiscale().isEmpty()) {
							sottoscriventeStato = true;
						}
						else
							sottoscriventeStato = false;
					}
				}
				else
					sottoscriventeStato = false;
			}

			//check valutazione dei multistanza
			for (Persona p : componentiComitatoScientifico) {
				boolean fullValutato = false;
				if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, p.getId(), IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL) != null)
					fullValutato = (mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL).getEsito() != null);
				else
					fullValutato = (
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA).getEsito() != null) &&
						(mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV).getEsito() != null)
					);
				if(mappaComponenti.get(p.getId()) != null && fullValutato)
					componentiComitatoScientificoStati.replace(p.getId(), true);
			};

			for (Sede s : sedi) {
				boolean fullValutato = false;
				if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, s.getId(), IdFieldEnum.SEDE__FULL) != null)
					fullValutato = (mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__FULL) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__FULL).getEsito() != null);
				else
					fullValutato = (
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__IS_LEGALE) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__IS_LEGALE).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__IS_OPERATIVA) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__IS_OPERATIVA).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__PROVINCIA) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__PROVINCIA).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__COMUNE) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__COMUNE).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__INDIRIZZO) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__INDIRIZZO).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__CAP) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__CAP).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__TELEFONO) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__TELEFONO).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__FAX) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__FAX).getEsito() != null) &&
						(mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__EMAIL) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__EMAIL).getEsito() != null)
					);
				if(mappaSedi.get(s.getId()) != null && fullValutato)
					sediStati.replace(s.getId(), true);
			};

			boolean coordinatoreFullValutato = false;
			//il coordinatore potrebbe essere stato eliminato in fase di integrazione (???)
			if(coordinatoreComitatoScientifico != null) {
				if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, coordinatoreComitatoScientifico.getId(), IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL) != null)
					coordinatoreFullValutato = (mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL).getEsito() != null);
				else
					coordinatoreFullValutato = (
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA).getEsito() != null) &&
						(mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV).getEsito() != null)
					);
			}

			boolean sedeLegaleFullValutato = false;
			if(sedeLegale != null) {
				if(hasIntegrazioneField && Utils.getField(fieldIntegrazioneList, sedeLegale.getId(), IdFieldEnum.SEDE__FULL) != null)
					sedeLegaleFullValutato = (mappaSedeLegale.containsKey(IdFieldEnum.SEDE__FULL) && mappaSedeLegale.get(IdFieldEnum.SEDE__FULL).getEsito() != null);
				else
					sedeLegaleFullValutato = (
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__IS_LEGALE) && mappaSedeLegale.get(IdFieldEnum.SEDE__IS_LEGALE).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__IS_OPERATIVA) && mappaSedeLegale.get(IdFieldEnum.SEDE__IS_OPERATIVA).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__PROVINCIA) && mappaSedeLegale.get(IdFieldEnum.SEDE__PROVINCIA).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__COMUNE) && mappaSedeLegale.get(IdFieldEnum.SEDE__COMUNE).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__INDIRIZZO) && mappaSedeLegale.get(IdFieldEnum.SEDE__INDIRIZZO).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__CAP) && mappaSedeLegale.get(IdFieldEnum.SEDE__CAP).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__TELEFONO) && mappaSedeLegale.get(IdFieldEnum.SEDE__TELEFONO).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__FAX) && mappaSedeLegale.get(IdFieldEnum.SEDE__FAX).getEsito() != null) &&
						(mappaSedeLegale.containsKey(IdFieldEnum.SEDE__EMAIL) && mappaSedeLegale.get(IdFieldEnum.SEDE__EMAIL).getEsito() != null)
					);
			}

			coordinatoreComitatoScientificoStato = (mappaCoordinatore != null && coordinatoreFullValutato) ? true : false;

			sedeLegaleStato = (mappaSedeLegale != null && sedeLegaleFullValutato) ? true : false;

			//ciclo la mappa degli stati dei componenti (tuttiComponentiValutati default == true)
			componentiComitatoScientificoStati.forEach((k,v) -> {
				if(v == false)
					tuttiComponentiValutati = false;
			});

			//ciclo la mappa degli stati delle sedi (tutteSediValutate default == true)
			sediStati.forEach((k,v) -> {
				if(v == false)
					tutteSediValutate = false;
			});

			comitatoScientificoStato = (coordinatoreComitatoScientificoStato && tuttiComponentiValutati) ? true : false;
			sediStato = (sedeLegaleStato && tutteSediValutate) ? true : false;

			/* check per abilitare disabilitare pulsanti valutazione dopo integrazione
			* controllo lo stato di inserimento per velocizzare la procedura:
			* se NON ha tutti i valori inseriti sicuramento canValidate è true
			* se ha tutti i valori inseriti ho già fatto questo controllo e controllo che almeno 1 sia abilitato
			*/
			if(!providerStato)
				canValidateProvider = true;
			else {
				canValidateProvider = (mappa.get(IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__PARTITA_IVA).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__CODICE_FISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__RAGIONE_SOCIALE).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__EMAIL_STRUTTURA).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__NATURA_ORGANIZZAZIONE).isEnabled() ||
					mappa.get(IdFieldEnum.PROVIDER__NO_PROFIT).isEnabled());
			}
			if(!legaleRappresentanteStato)
				canValidateLegale = true;
			else {
				canValidateLegale = ((mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL) && mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__FULL).isEnabled()) ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__COGNOME).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__NOME).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__CODICEFISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__CELLULARE).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__EMAIL).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__PEC).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__ATTO_NOMINA).isEnabled() ||
					mappa.get(IdFieldEnum.LEGALE_RAPPRESENTANTE__CV).isEnabled());
			}
			if(!delegatoLegaleRappresentanteStato)
				canValidateDelegato = true;
			else {
				canValidateDelegato = ((mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL) && mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__FULL).isEnabled()) ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__NOME).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CV).isEnabled() ||
					mappa.get(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA).isEnabled());
			}
			if(!tipologiaFormativaStato)
				canValidateTipologiaFormativa = true;
			else {
				canValidateTipologiaFormativa = (mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO).isEnabled() ||
	 				mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE).isEnabled() ||
	 				mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO).isEnabled() ||
					mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE).isEnabled());
			}
			if(!datiEconomiciStato)
				canValidateDatiEconomici = true;
			else {
				canValidateDatiEconomici = (mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO).isEnabled() ||
	 				mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO).isEnabled() ||
	 				mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE).isEnabled() ||
					mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE).isEnabled());
			}
			if(!datiStrutturaStato)
				canValidateDatiStruttura = true;
			else {
				canValidateDatiStruttura = (mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI).isEnabled() ||
	 				mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA).isEnabled() ||
	 				mappa.get(IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA).isEnabled());
			}
			if(!responsabileSegreteriaStato)
				canValidateResponsabileSegreteria = true;
			else {
				canValidateResponsabileSegreteria = ((mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__FULL).isEnabled()) ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__COGNOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__NOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__CODICEFISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__TELEFONO).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__EMAIL).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__ATTO_NOMINA).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SEGRETERIA__CV).isEnabled());
			}
			if(!responsabileAmministrativoStato)
				canValidateResponsabileAmministrativo = true;
			else {
				canValidateResponsabileAmministrativo = ((mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__FULL).isEnabled()) ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__COGNOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__NOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__TELEFONO).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__EMAIL).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CV).isEnabled());
			}
			if(!responsabileSistemaInformaticoStato)
				canValidateResponsabileSistemaInformatico = true;
			else {
				canValidateResponsabileSistemaInformatico = ((mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__FULL).isEnabled()) ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__COGNOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__NOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__EMAIL).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CV).isEnabled());
			}
			if(!responsabileQualitaStato)
				canValidateResponsabileQualita = true;
			else {
				canValidateResponsabileQualita = ((mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__FULL) && mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__FULL).isEnabled()) ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__COGNOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__NOME).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__CODICEFISCALE).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__TELEFONO).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__EMAIL).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__ATTO_NOMINA).isEnabled() ||
					mappa.get(IdFieldEnum.RESPONSABILE_QUALITA__CV).isEnabled());
			}
			canValidateAttoCostitutivo = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO).isEnabled();
			canValidateEsperienzaFormazione = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE).isEnabled();
			canValidateUtilizzo = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO).isEnabled();
			canValidateSistemaInformatico = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO).isEnabled();
			canValidatePianoQualita = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA).isEnabled();
			canValidateDichiarazioneLegale = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE).isEnabled();
			canValidateDichiarazioneEsclusione = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE).isEnabled();
			canValidateRichiestaAccreditamentoStandard = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD).isEnabled();
			canValidateRelazioneAttivitaFormativa = !mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA) || mappa.get(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA).isEnabled();
			//ripetibili canValidate
			for (Persona p : componentiComitatoScientifico) {
				boolean canValidatePersona;
				if(!componentiComitatoScientificoStati.get(p.getId()))
					canValidatePersona = true;
				else {
					canValidatePersona = ((mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL) && mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL).isEnabled()) ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA).isEnabled() ||
						!mappaComponenti.get(p.getId()).containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV) || mappaComponenti.get(p.getId()).get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV).isEnabled());
				};
				mappaCanValidateComitato.put(p.getId(), canValidatePersona);
			}
			if(coordinatoreComitatoScientifico != null) {
				boolean canValidatePersona;
				if(!coordinatoreComitatoScientificoStato)
					canValidatePersona = true;
				else {
					canValidatePersona = ((mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL) && mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL).isEnabled()) ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA).isEnabled() ||
						!mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV) || mappaCoordinatore.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV).isEnabled());
				};
				mappaCanValidateComitato.put(coordinatoreComitatoScientifico.getId(), canValidatePersona);
			}
			for (Sede s : sedi) {
				boolean canValidateSede;
				if(!sediStati.get(s.getId()))
					canValidateSede = true;
				else {
					canValidateSede = ((mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__FULL) && mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__FULL).isEnabled()) ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__PROVINCIA) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__PROVINCIA).isEnabled() ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__COMUNE) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__COMUNE).isEnabled() ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__INDIRIZZO) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__INDIRIZZO).isEnabled() ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__CAP) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__CAP).isEnabled() ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__TELEFONO) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__TELEFONO).isEnabled() ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__FAX) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__FAX).isEnabled() ||
						!mappaSedi.get(s.getId()).containsKey(IdFieldEnum.SEDE__EMAIL) || mappaSedi.get(s.getId()).get(IdFieldEnum.SEDE__EMAIL).isEnabled());
				};
				mappaCanValidateSedi.put(s.getId(), canValidateSede);
			}
			if(sedeLegale != null) {
				boolean canValidateSede;
				if(!sedeLegaleStato)
					canValidateSede = true;
				else {
					canValidateSede = ((mappaSedeLegale.containsKey(IdFieldEnum.SEDE__PROVINCIA) && mappaSedeLegale.get(IdFieldEnum.SEDE__PROVINCIA).isEnabled()) ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__PROVINCIA) || mappaSedeLegale.get(IdFieldEnum.SEDE__PROVINCIA).isEnabled() ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__COMUNE) || mappaSedeLegale.get(IdFieldEnum.SEDE__COMUNE).isEnabled() ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__INDIRIZZO) || mappaSedeLegale.get(IdFieldEnum.SEDE__INDIRIZZO).isEnabled() ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__CAP) || mappaSedeLegale.get(IdFieldEnum.SEDE__CAP).isEnabled() ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__TELEFONO) || mappaSedeLegale.get(IdFieldEnum.SEDE__TELEFONO).isEnabled() ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__FAX) || mappaSedeLegale.get(IdFieldEnum.SEDE__FAX).isEnabled() ||
						!mappaSedeLegale.containsKey(IdFieldEnum.SEDE__EMAIL) || mappaSedeLegale.get(IdFieldEnum.SEDE__EMAIL).isEnabled());
				};
				mappaCanValidateSedi.put(sedeLegale.getId(), canValidateSede);

			}

			//sezioni validate o meno
			sezione1Stato = (providerStato && sediStato && legaleRappresentanteStato && tipologiaFormativaStato && datiEconomiciStato && datiStrutturaStato) ? true : false;
			sezione2Stato = (responsabileSegreteriaStato && responsabileAmministrativoStato && responsabileSistemaInformaticoStato && responsabileQualitaStato && comitatoScientificoStato) ? true : false;
			sezione3Stato = (attoCostitutivoStato && esperienzaFormazioneStato && utilizzoStato && sistemaInformaticoStato && pianoQualitaStato && dichiarazioneLegaleStato) ? true : false;
			//TODO rimuovere se deciso che non serve più
//			sezione4Stato = tuttiEventiValutati ? true : false;

			if(accreditamento.isStandard())
				sezione3Stato = sezione3Stato && relazioneAttivitaFormativaStato && richiestaAccreditamentoStandardStato;

			sezione4Stato = valutazioneSulCampoStato && sottoscriventeStato;

			//stato di valutazione completa
			if(accreditamento.isValutazioneSulCampo())
				canSendValutazione = (sezione1Stato && sezione2Stato && sezione3Stato && sezione4Stato);
			else
				canSendValutazione = (sezione1Stato && sezione2Stato && sezione3Stato);

			canConfermaValutazione = canValutaDomanda && canSendValutazione;
		}
	}

	/*
	 * [A] Almeno 5 componenti (coordinatore incluso)
	 * [B] Almeno 5 professionisti Sanitari
	 * [C] Se settato "Generale" -> Almeno 2 professioni diverse tra i componenti TODO rimosse come richiesto, ma commentate solamente (non si sa mai)
	 * [D] Se settato "Settoriale" -> Almeno 2 professioni analoghe a quelle selezionate (almeno che non sia stata selezionate solo 1 professione)
	 * [D-bis] Se Selezionata solo 1 professione -> deve conicidere con quella e UNICA dei componenti del comitato scientifico
	 * */
	public void checkComitatoScientifico_fromDB(int numeroComponentiComitatoScientifico, int numeroProfessionistiSanitari, Set<Professione> elencoProfessioniDeiComponenti, int professioniDeiComponentiAnaloghe){
		comitatoScientificoStato = true;

//		int professioniDeiComponenti = elencoProfessioniDeiComponenti.size();

		//[A]
		if(numeroComponentiComitatoScientifico < 4 || (coordinatoreComitatoScientifico == null || coordinatoreComitatoScientifico.isNew())){
			comitatoScientificoStato = false;
			comitatoScientificoErrorMessage = "error.numero_minimo_comitato";
		}//[B]
		else if(numeroProfessionistiSanitari < 5){
			comitatoScientificoStato = false;
			comitatoScientificoErrorMessage = "error.numero_minimo_professionisti_sanitari";
		}
//		else{
//			// mi assicuro che sono stati gia' inseriti i dati relativi all'accreditamento
//			if(datiAccreditamentoStato){
//				//[C]
//				if(datiAccreditamento.getProfessioniAccreditamento().equalsIgnoreCase("generale")){
//					if(professioniDeiComponenti < 2){
//						comitatoScientificoStato = false;
//						comitatoScientificoErrorMessage = "error.numero_minimo_professioni";
//					}
//				}//[D]
//				else{
//					if(datiAccreditamento.getProfessioniSelezionate().size() > 1){
//						if(professioniDeiComponentiAnaloghe < 2){
//							comitatoScientificoStato = false;
//							comitatoScientificoErrorMessage = "error.numero_minimo_professioni_settoriale";
//						}
//					}//[D - bis]
//					else{
//						if(elencoProfessioniDeiComponenti.size() > 1 || elencoProfessioniDeiComponenti.size() == 0){
//							comitatoScientificoStato = false;
//							comitatoScientificoErrorMessage = "error.professioni_non_conformi_con_accreditamento";
//						}else{
//							if(elencoProfessioniDeiComponenti.iterator().next() != datiAccreditamento.getProfessioniSelezionate().iterator().next()){
//								comitatoScientificoStato = false;
//								comitatoScientificoErrorMessage = "error.professioni_non_conformi_con_accreditamento";
//							}
//						}
//					}
//				}
//			}else{
//				comitatoScientificoStato = false;
//				comitatoScientificoErrorMessage = "error.datiaccreditamento_mancanti";
//			}
//		}
	}

	private void setFilesStato(Set<String> filesDelProvider){
		if(filesDelProvider.contains(FileEnum.FILE_ATTO_COSTITUTIVO))
			attoCostitutivoStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_ESPERIENZA_FORMAZIONE))
			esperienzaFormazioneStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_UTILIZZO))
			utilizzoStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_SISTEMA_INFORMATICO))
			sistemaInformaticoStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_PIANO_QUALITA))
			pianoQualitaStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_DICHIARAZIONE_LEGALE))
			dichiarazioneLegaleStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_DICHIARAZIONE_ESCLUSIONE))
			dichiarazioneEsclusioneStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_RICHIESTA_ACCREDITAMENTO_STANDARD))
			richiestaAccreditamentoStandardStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_RELAZIONE_ATTIVITA_FORMATIVA))
			relazioneAttivitaFormativaStato = true;
	}

	public boolean isComitatoScientificoEditabile(){
		Set<IdFieldEnum> ids = Utils.getSubsetOfIdFieldEnum(new HashSet<FieldEditabileAccreditamento>(getAccreditamento().getIdEditabili()), SubSetFieldEnum.COMITATO_SCIENTIFICO);
		if(ids.isEmpty())
			return false;
		else
			return true;
	}

	//la domanda è stata compilata in tutte le sue parti (tutti i flag sono TRUE)
	public boolean isCompleta(){
		if(sezione1Stato && sezione2Stato && sezione3Stato)
			return true;
		else
			return false;
	}

	//Inserisci piano formativo e blocca idEditabili
	public boolean isCanInsertPianoFormativo(){
		if(accreditamento.isBozza() && isCompleta() && !accreditamento.hasPianoFormativo())
			return true;
		else
			return false;
	}

	//Invia domanda alla segreteria cambiando stato all'accreditamento e rendendo la domanda non più modificabile
	public boolean isCanSend(){
		if(accreditamento.isProvvisorio())
			return (accreditamento.isBozza() && isCompleta() && isPianoFormativoCompleto());
		else if(accreditamento.isStandard())
			return (accreditamento.isBozza() && isCompleta());
		return false;
	}

	//ci sono eventi inseriti nel piano formativo
	private boolean isPianoFormativoCompleto(){
		return ((accreditamento.getPianoFormativo() != null) && !accreditamento.getPianoFormativo().getEventiPianoFormativo().isEmpty());
	}

	public boolean isCanInsertEventoInPianoFormativo(){
		return (accreditamento.hasPianoFormativo());
		//return (accreditamento.hasPianoFormativo() && accreditamento.isEditabile());
		//TODO controllo FieldEditabileAccreditamento
	}
}
