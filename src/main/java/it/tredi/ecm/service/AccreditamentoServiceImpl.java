package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import it.tredi.bonita.api.model.TaskInstanceDataModel;
import it.tredi.ecm.audit.entity.AccreditamentoAudit;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoDiff;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneHistoryContainer;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
import it.tredi.ecm.dao.entity.WorkflowInfo;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.StatoWorkflowEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.dao.enumlist.TipoWorkflowEnum;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.dao.repository.ValutazioneCommissioneRepository;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioAccreditatoInfo;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioDecretoDecadenzaInfo;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo;
import it.tredi.ecm.pdf.PdfAccreditamentoProvvisorioRigettoInfo;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ImpostazioniProviderWrapper;

@Service
public class AccreditamentoServiceImpl implements AccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoServiceImpl.class);
	private static long millisecondiInGiorno = 86400000;
	private static long millisecondiInMinuto = 60000;
	private static int massimaDurataProcedimento = 180;

	@Autowired private AccreditamentoRepository accreditamentoRepository;

	@Autowired private ProviderService providerService;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private AccountRepository accountRepository;
	@Autowired private EmailService emailService;
//	@Autowired private PagamentoService pagamentoService;
	@Autowired private QuotaAnnualeService quotaAnnualeService;
	@Autowired private ProtocolloService protocolloService;

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;

	@Autowired private IntegrazioneService integrazioneService;
	@Autowired private WorkflowService workflowService;

	@Autowired private FileService fileService;
	@Autowired private PdfService pdfService;

	@Autowired private MessageSource messageSource;

	@Autowired private EcmProperties ecmProperties;

	@Autowired private AlertEmailService alertEmailService;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private AccreditamentoStatoHistoryService accreditamentoStatoHistoryService;

	@Autowired private ValutazioneCommissioneRepository valutazioneCommissioneRepository;

	@Autowired private PersonaService personaService;
	@Autowired private SedeService sedeService;
	@Autowired private AuditService auditService;

	@Autowired private DiffService diffService;

	@Autowired private TokenService tokenService;

	@Autowired private AuditReportProviderService auditReportProviderService;
	@Autowired private AccountService accountService;
	
	@Autowired private EventoService eventoService;


	@Override
	public Accreditamento getNewAccreditamentoForCurrentProvider(AccreditamentoTipoEnum tipoDomanda) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Creazione domanda di accreditamento per il provider corrente"));
		Provider currentProvider = providerService.getProvider();
		return getNewAccreditamento(currentProvider,tipoDomanda);
	}

	@Override
	public Accreditamento getNewAccreditamentoForProvider(Long providerId, AccreditamentoTipoEnum tipoDomanda) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Creazione domanda di accreditamento " + tipoDomanda + " per il provider: " + providerId));
		Provider provider = providerService.getProvider(providerId);
		return getNewAccreditamento(provider,tipoDomanda);
	}

	@Transactional
	private Accreditamento getNewAccreditamento(Provider provider, AccreditamentoTipoEnum tipoDomanda) throws Exception{
		if(provider == null){
			throw new Exception("Provider non può essere NULL");
		}

		if(provider.isNew()){
			throw new Exception("Provider non registrato");
		}else{

			Set<Accreditamento> accreditamentiAttivi = getAccreditamentiAvviatiForProvider(provider.getId(), tipoDomanda);

			if(canProviderCreateAccreditamento(provider.getId(), tipoDomanda)){
				Accreditamento accreditamento = new Accreditamento(tipoDomanda);
				accreditamento.setProvider(provider);
				accreditamento.enableAllIdField();
				save(accreditamento);

				//se è la seconda volta che inserisco un provvisorio risetto i dati a default
				if(tipoDomanda == AccreditamentoTipoEnum.PROVVISORIO) {
					provider.setCanInsertAccreditamentoProvvisorio(false);
					provider.setDataRinnovoInsertAccreditamentoProvvisorio(null);
				}

				if(tipoDomanda == AccreditamentoTipoEnum.STANDARD){
						Accreditamento ultimoAccreditamento = getAccreditamentoAttivoOppureUltimoForProvider(provider.getId());
						diffService.creaAllDiffAccreditamento(ultimoAccreditamento);

						save(accreditamento);

						if(ultimoAccreditamento != null && ultimoAccreditamento.getDatiAccreditamento() != null){
							DatiAccreditamento ultimoDatiAccreditamento = ultimoAccreditamento.getDatiAccreditamento();
							if(ultimoDatiAccreditamento.isDatiStrutturaInseriti() || ultimoDatiAccreditamento.isTipologiaFormativaInserita()){
								DatiAccreditamento datiAccreditamento = new DatiAccreditamento();

								datiAccreditamento.setTipologiaAccreditamento(String.copyValueOf(ultimoDatiAccreditamento.getTipologiaAccreditamento().toCharArray()));
								datiAccreditamento.getProcedureFormative().addAll(ultimoDatiAccreditamento.getProcedureFormative());
								datiAccreditamento.setProfessioniAccreditamento(String.copyValueOf(ultimoDatiAccreditamento.getProfessioniAccreditamento().toCharArray()));
								datiAccreditamento.getDiscipline().addAll(ultimoDatiAccreditamento.getDiscipline());

								datiAccreditamento.setNumeroDipendentiFormazioneTempoIndeterminato(ultimoDatiAccreditamento.getNumeroDipendentiFormazioneTempoIndeterminato());
								datiAccreditamento.setNumeroDipendentiFormazioneAltro(ultimoDatiAccreditamento.getNumeroDipendentiFormazioneAltro());

								File organigramma = null;
								File funzionigramma = null;
								for(File f : ultimoDatiAccreditamento.getFiles()){
									if(f.isORGANIGRAMMA())
										organigramma = (File) f.clone();
									else if(f.isFUNZIONIGRAMMA()){
										funzionigramma = (File) f.clone();
									}
								}

								if(organigramma != null){
									fileService.save(organigramma);
									datiAccreditamento.addFile(organigramma);
								}

								if(funzionigramma != null){
									fileService.save(funzionigramma);
									datiAccreditamento.addFile(funzionigramma);
								}

								datiAccreditamentoService.save(datiAccreditamento, accreditamento.getId());
							}
						}
				}else{
					save(accreditamento);
				}

				return accreditamento;
			}else{
				throw new Exception("Il provider " + provider.getId() + " non può presentare una domanda di accreditamento " + tipoDomanda);
			}
		}
	}

	@Transactional
	@Override
	public void createAccreditamentoDiff(Long providerId) throws Exception{
		Accreditamento ultimoAccreditamento = getAccreditamentoAttivoForProvider(providerId);
		diffService.creaAllDiffAccreditamento(ultimoAccreditamento);
	}

	@Override
	public Accreditamento getAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Caricamento domanda di accreditamento: " + accreditamentoId));
		return accreditamentoRepository.findOne(accreditamentoId);
	};

	@Override
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le domande di accreditamento per il provider " + providerId));
		Set<Accreditamento> accreditamenti = accreditamentoRepository.findByProviderId(providerId);
		if(accreditamenti != null)
			LOGGER.debug("Trovati " + accreditamenti.size() + " accreditamenti");
		return accreditamenti;
	}

	@Override
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId,AccreditamentoTipoEnum tipoDomnda) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le domande di accreditamento " + tipoDomnda + " per il provider " + providerId));
		Set<Accreditamento> accreditamenti = accreditamentoRepository.findAllByProviderIdAndTipoDomanda(providerId,tipoDomnda);
		if(accreditamenti != null)
			LOGGER.debug("Trovati " + accreditamenti.size() + " accreditamenti");
		return accreditamenti;
	}

	/**
	 * Restituisce tutte le domande di accreditamento che hanno una data di scadenza "attiva"
	 * */
	@Override
	public Set<Accreditamento> getAccreditamentiAvviatiForProvider(Long providerId, AccreditamentoTipoEnum tipoDomanda) {
		LOGGER.debug(Utils.getLogMessage("Recupero domande di accreditamento avviate per il provider " + providerId));
		LOGGER.debug(Utils.getLogMessage("Ricerca domande di accreditamento di tipo: " + tipoDomanda.name() + "con data di scadenza posteriore a: " + LocalDate.now()));
		//20180403 la dataScadenza non è più valorizzata quando l'accreditamento è in capo al provider
		//return accreditamentoRepository.findAllByProviderIdAndTipoDomandaAndDataScadenzaAfter(providerId, tipoDomanda, LocalDate.now());
		return accreditamentoRepository.getAccreditamentiAvviatiForProvider(providerId, tipoDomanda);
	}

	/**
	 * Restituisce l'unica domanda di accreditamento che ha una data di fine accreditamento "attiva" e che è in stato "ACCREDITATO"
	 * se ne trova più di una prendo quella che scadrà per primo
	 * */
	@Override
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) throws AccreditamentoNotFoundException{
		LOGGER.debug(Utils.getLogMessage("Recupero eventuale accreditamento attivo per il provider: " + providerId));

		Accreditamento accreditamento = null;
		Set<Accreditamento> listaAccreditamentiAttivi = accreditamentoRepository.findAllByProviderIdAndStatoAndDataFineAccreditamentoAfterOrderByDataFineAccreditamentoAsc(providerId, AccreditamentoStatoEnum.ACCREDITATO, LocalDate.now());

		if(listaAccreditamentiAttivi != null && !listaAccreditamentiAttivi.isEmpty()){
			LOGGER.info("Trovati " + listaAccreditamentiAttivi.size() + " accreditamenti attivi per il provider: " + providerId);
			//prendo il primo (quello che sarà il primo a scadere)
			accreditamento = listaAccreditamentiAttivi.iterator().next();
			LOGGER.info("Selezionato come valido l'accreditamento: " + accreditamento.getId() + " valido fino al " + accreditamento.getDataFineAccreditamento());
		}else{
			throw new AccreditamentoNotFoundException("Nessun Accreditamento attivo trovato per il provider " + providerId);
		}

		return accreditamento;
	}


	/**
	 * Restituisce la domanda di accreditamento attiva se presente altrimenti l'ultimo Accreditamento inserito
	 * */
	@Override
	public Accreditamento getAccreditamentoAttivoOppureUltimoForProvider(Long providerId) throws AccreditamentoNotFoundException{
		LOGGER.debug(Utils.getLogMessage("Recupero eventuale accreditamento attivo oppure l'ultimo per il provider: " + providerId));

		Accreditamento accreditamento = null;
		try {
			accreditamento = getAccreditamentoAttivoForProvider(providerId);
			LOGGER.info("Selezionato come corrente l'accreditamento attivo per il provider: " + providerId);
		} catch (Exception e) {
			//Attivo non trovato recupero l'ultimo
			Set<Accreditamento> listaAccreditamentiAttivi = accreditamentoRepository.findAllByProviderIdOrderByDataInvioDesc(providerId);
			if(listaAccreditamentiAttivi != null && !listaAccreditamentiAttivi.isEmpty()){
				LOGGER.info("Trovati " + listaAccreditamentiAttivi.size() + " accreditamenti per il provider: " + providerId);
				//prendo il primo (quello che sarà il primo a scadere)
				accreditamento = listaAccreditamentiAttivi.iterator().next();
				LOGGER.info("Selezionato come corrente l'ultimo accreditamento: " + accreditamento.getId() + " data invio il " + accreditamento.getDataInvio());
			}else{
				throw new AccreditamentoNotFoundException("Nessun Accreditamento trovato per il provider " + providerId);
			}
		}

		return accreditamento;
	}

	@Override
	public AccreditamentoStatoEnum getStatoAccreditamento(Long accreditamentoId) {
		return accreditamentoRepository.getStatoByAccreditamentoId(accreditamentoId);
	}

	@Override
	@Transactional
	public void save(Accreditamento accreditamento) {
		LOGGER.debug("Salvataggio domanda di accreditamento " + accreditamento.getTipoDomanda() + " per il provider " + accreditamento.getProvider().getId());
		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);
	}

	public void saveAndAudit(Accreditamento accreditamento) {
		accreditamentoRepository.saveAndFlush(accreditamento);
		auditService.commitForCurrentUser(new AccreditamentoAudit(accreditamento));
		auditReportProviderService.auditAccreditamentoProvider(accreditamento.getProvider());
	}

	public void audit(Accreditamento accreditamento) {
		auditService.commitForCurrentUser(new AccreditamentoAudit(accreditamento));
		auditReportProviderService.auditAccreditamentoProvider(accreditamento.getProvider());
	}

	@Override
	public boolean canProviderCreateAccreditamento(Long providerId, AccreditamentoTipoEnum tipoDomanda) {

		//per le domande standard è innanzitutto necessario che la segreteria abiliti il provider
		if(tipoDomanda == AccreditamentoTipoEnum.STANDARD){
			if(!providerService.canInsertAccreditamentoStandard(providerId)){
				LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> " + "Non Abilitato"));
				return false;
			}
		}

		//si può reinserire una domanda provvisoria se la standard è stata diniegata e sono passati almeno 6 mesi
		if(tipoDomanda == AccreditamentoTipoEnum.PROVVISORIO) {
			Provider provider = providerService.getProvider(providerId);
			if(!providerService.canInsertAccreditamentoProvvisorio(providerId)) {
				LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> " + "Non può ancora creare una nuova domanda provvisoria"));
				return false;
			}
		}

		Set<Accreditamento> accreditamentoList = getAllAccreditamentiForProvider(providerId, tipoDomanda);
		for(Accreditamento accreditamento : accreditamentoList){
			if(accreditamento.isBozza()){
				LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> Presente domanda " + accreditamento.getId() + " in stato di " + accreditamento.getStato().name()));
				return false;
			}

			if(accreditamento.isProcedimentoAttivo()){
				LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> Presente domanda " + accreditamento.getId() + " in stato di Procedimento Attivo"));
				return false;
			}

			//SOLO PER IL PROVVISORIO - se e' già attiva una domanda non facciamo crearne altre
			//PER LA STD invece si...caso del rinnovo
			if(tipoDomanda == AccreditamentoTipoEnum.PROVVISORIO){
				if (accreditamento.isDomandaAttiva()){
					LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> Presente domanda " + accreditamento.getId() + " in stato di Domanda Attiva con scadenza " + accreditamento.getDataFineAccreditamento()));
					return false;
				}

			}
		}

		return true;
	}

	@Override
	//TODO capire perchè con @Transactional non ha effetto il salvataggio del wokflowService
	public void inviaDomandaAccreditamento(Long accreditamentoId) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Invio domanda di Accreditamento " + accreditamentoId + " alla segreteria"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		if(accreditamento.getDataInvio() == null)
			accreditamento.setDataInvio(LocalDate.now());
		//20180403 la dataScadenza viene gestita internamente all'entity Accreditamento
//		accreditamento.setDataScadenza(accreditamento.getDataInvio().plusDays(massimaDurataProcedimento));
		accreditamento.setMassimaDurataProcedimento(massimaDurataProcedimento);

		accreditamento.getProvider().setStatus(ProviderStatoEnum.VALIDATO);

		if(accreditamento.isStandard()) {
			accreditamento.getProvider().setCanInsertAccreditamentoStandard(false);
			accreditamento.getProvider().setDataScadenzaInsertAccreditamentoStandard(null);
			accreditamento.getProvider().setInviatoAccreditamentoStandard(true);
		}

		fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);

		protocolloService.protocollaDomandaInArrivo(accreditamentoId, accreditamento.getFileIdForProtocollo(), accreditamento.getFileIdsAllegatiForProtocollo());

		try{
			if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
				workflowService.createWorkflowAccreditamentoProvvisorio(Utils.getAuthenticatedUser(), accreditamento);
			if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD)
				workflowService.createWorkflowAccreditamentoStandard(Utils.getAuthenticatedUser(), accreditamento);
		}catch (Exception ex){
			LOGGER.debug(Utils.getLogMessage("Errore avvio Workflow Accreditamento per la domanda " + accreditamentoId));
			throw new Exception("Errore avvio Workflow Accreditamento per la domanda " + accreditamentoId);
		}

		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);

	}

	@Override
	@Transactional
	public void inserisciPianoFormativo(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Inserimento piano formativo per la domanda di Accreditamento " + accreditamentoId));

		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		PianoFormativo pianoFormativo = new PianoFormativo();
		pianoFormativo.setAnnoPianoFormativo(LocalDate.now().getYear());
		pianoFormativo.setProvider(accreditamento.getProvider());
		pianoFormativoService.save(pianoFormativo);
		accreditamento.setPianoFormativo(pianoFormativo);
		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);


		fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);
		fieldEditabileService.insertFieldEditabileForAccreditamento(accreditamentoId, null, SubSetFieldEnum.FULL, new HashSet<IdFieldEnum>(Arrays.asList(IdFieldEnum.EVENTO_PIANO_FORMATIVO__FULL)));
	}

	/* La segreteria invia la prima valutazione della domanda di accreditamento provvisoria
	 * blocca e storicizza la valutazione, crea le valutazioni dei referee, manda avanti il flusso a VALUTAZIONE_CRECM */
	@Override
	@Transactional
	public void inviaValutazioneSegreteriaAssegnamentoProvvisorio(Long accreditamentoId, String valutazioneComplessiva, Set<Account> referee) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Assegnamento domanda di Accreditamento Provvisoria" + accreditamentoId + " ad un gruppo CRECM"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account user = Utils.getAuthenticatedUser().getAccount();
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, user.getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());

		//crea le valutazioni per i referee
		List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
		for (Account a : referee) {
			Valutazione valutazioneReferee = new Valutazione(a, accreditamento, ValutazioneTipoEnum.REFEREE);
			//setta i campi valutati positivamente di default
			//valutazioneReferee.setValutazioni(fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento));
			valutazioneService.initializeFieldValutazioni(valutazioneReferee, accreditamento);
			valutazioneService.save(valutazioneReferee);
			emailService.inviaNotificaAReferee(a.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
			usernameWorkflowValutatoriCrecm.add(a.getUsernameWorkflow());
		}
		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);

		//il numero minimo di valutazioni necessarie (se 3 Referee -> minimo 2)
		Integer numeroValutazioniCrecmRichieste = new Integer(usernameWorkflowValutatoriCrecm.size() - 1);

		workflowService.eseguiTaskValutazioneAssegnazioneCrecmForCurrentUser(accreditamento, usernameWorkflowValutatoriCrecm, numeroValutazioniCrecmRichieste);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	/* salvataggio della prima valutazione della segreteria della domanda standard, blocca e storicizza valutazione per poi azzerarla e riabilitarla subito
	 * per la valutazione sul campo */
	@Override
	@Transactional
	public void inviaValutazioneSegreteriaAssegnamentoStandard(Long accreditamentoId, String valutazioneComplessiva, VerbaleValutazioneSulCampo verbale) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Salvataggio valutazione segreteria assegnamento STANDARD della domanda di Accreditamento " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account user = Utils.getAuthenticatedUser().getAccount();
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, user.getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		//mantengo l'id della valutazione prima della storicizzazione
		Long valutazioneId = valutazione.getId();

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());


		//svuoto e riabilito la valutazione della segreteria (comunque salvata in storico)
		Valutazione valutazioneReload = valutazioneService.getValutazione(valutazioneId);
		valutazioneReload.setDataValutazione(null);
		valutazioneReload.setDataOraScadenzaPossibilitaValutazione(null);
		valutazioneReload.setValutazioneComplessiva(null);
		valutazioneReload.getValutazioni().clear();
		valutazioneReload.setValutazioni(fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento));
		valutazioneService.save(valutazioneReload);

		//segretario valutatore
		if(verbale != null) {
			verbale.setValutatore(Utils.getAuthenticatedUser().getAccount());
			accreditamento.setVerbaleValutazioneSulCampo(verbale);
		}
		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);

		//invio mail
		Set<String> dst = new HashSet<String>();
		if(verbale != null){
			if(verbale.getTeamLeader() != null)
				dst.add(verbale.getTeamLeader().getEmail());
			if(verbale.getReferenteInformatico() != null)
				dst.add(verbale.getReferenteInformatico().getEmail());

			if(verbale.getComponentiSegreteria() != null){
				for(Account a : verbale.getComponentiSegreteria())
					dst.add(a.getEmail());
			}
			emailService.inviaConvocazioneValutazioneSulCampo(dst, verbale.getDataoraVisita(), accreditamento.getProvider().getDenominazioneLegale());
		}

		//il nome del metodo è un pò ingannevole
		workflowService.eseguiTaskValutazioneAssegnazioneTeamLeaderForCurrentUser(accreditamento, verbale.getTeamLeader().getUsernameWorkflow());

		//rilascio semaforo Bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	/* Valutazione del provider. Salva la valutazione complessiva, blocca e storicizza la valutazione, aggiorna le valutazioni (dell'utente) e avvisa Bonita che questo utente ha eseguito
	 * il proprio task */
	@Override
	@Transactional
	public void inviaValutazioneCrecmProvvisorio(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Salvataggio valutazione CRECM della domanda di Accreditamento Provvisorio" + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account user = Utils.getAuthenticatedUser().getAccount();
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, user.getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());

		user.setValutazioniNonDate(0);
		user.setDomandeNonValutate(new HashSet<Accreditamento>());
		accountRepository.save(user);
		workflowService.eseguiTaskValutazioneCrecmForCurrentUser(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	private void bloccaAndStoricizzaValutazione(Valutazione valutazione, String valutazioneComplessiva, AccreditamentoStatoEnum stato) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Bloccaggio e storicizzazione della valutazione: " + valutazione.getId()));
		//setta la data
		valutazione.setDataValutazione(LocalDateTime.now());
		//disabilito tutti i filedValutazioneAccreditamento
		for (FieldValutazioneAccreditamento fva : valutazione.getValutazioni()) {
			fva.setEnabled(false);
		}
		valutazione.setValutazioneComplessiva(valutazioneComplessiva);
		valutazione.setAccreditamentoStatoValutazione(stato);
		valutazioneService.saveAndFlush(valutazione);
		//detacha e copia: da questo momento valutazione si riferisce alla copia storicizzata
		valutazioneService.copiaInStorico(valutazione);
	}

	@Deprecated
	@Override
	@Transactional
	public void inviaValutazioneDomanda(Long accreditamentoId, String valutazioneComplessiva, Set<Account> refereeGroup, VerbaleValutazioneSulCampo verbale) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Assegnamento domanda di Accreditamento " + accreditamentoId + " ad un gruppo CRECM"));
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Account user = Utils.getAuthenticatedUser().getAccount();
		Long valutazioneId = valutazione.getId();

		//setta la data
		valutazione.setDataValutazione(LocalDateTime.now());
		//disabilito tutti i filedValutazioneAccreditamento
		for (FieldValutazioneAccreditamento fva : valutazione.getValutazioni()) {
			fva.setEnabled(false);
		}

		//inserisce il commento complessivo
		valutazione.setValutazioneComplessiva(valutazioneComplessiva);

		//setta lo stato dell'accreditamento al momento del salvataggio
		valutazione.setAccreditamentoStatoValutazione(accreditamento.getStato());

		valutazioneService.saveAndFlush(valutazione);

		//detacha e copia: da questo momento valutazione si riferisce alla copia storicizzata
		valutazioneService.copiaInStorico(valutazione);

		//il referee azzera il suo contatore di valutazioni non date consecutivamente e svuota la lista
		//(REFEREE CHIAMA QUESTO METODO SOLO DAL PROVVISORIO)
		if (user.isReferee()) {
			user.setValutazioniNonDate(0);
			user.setDomandeNonValutate(new HashSet<Accreditamento>());
			accountRepository.save(user);
			workflowService.eseguiTaskValutazioneCrecmForCurrentUser(accreditamento);
		}
		//la segreteria
		else {
			if(accreditamento.isProvvisorio()) {
				//crea le valutazioni per i referee
				List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
				for (Account a : refereeGroup) {
					Valutazione valutazioneReferee = new Valutazione(a, accreditamento, ValutazioneTipoEnum.REFEREE);
					//setta i campi valutati positivamente di default
					valutazioneReferee.setValutazioni(fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento));
					valutazioneService.save(valutazioneReferee);
					emailService.inviaNotificaAReferee(a.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
					usernameWorkflowValutatoriCrecm.add(a.getUsernameWorkflow());
				}
				accreditamento.setDataValutazioneCrecm(LocalDate.now());
				accreditamentoRepository.save(accreditamento);
				//saveAndAudit(accreditamento);

				//il numero minimo di valutazioni necessarie (se 3 Referee -> minimo 2)
				Integer numeroValutazioniCrecmRichieste = new Integer(usernameWorkflowValutatoriCrecm.size() - 1);
				workflowService.eseguiTaskValutazioneAssegnazioneCrecmForCurrentUser(accreditamento, usernameWorkflowValutatoriCrecm, numeroValutazioniCrecmRichieste);

			} else if(accreditamento.isStandard()) {
				Valutazione valutazioneReload = valutazioneService.getValutazione(valutazioneId);
				//svuoto e riabilito la valutazione della segreteria (comunque salvata in storico)
				valutazioneReload.setDataValutazione(null);
				valutazioneReload.setDataOraScadenzaPossibilitaValutazione(null);
				valutazioneReload.setValutazioneComplessiva(null);
				valutazioneReload.getValutazioni().clear();
				valutazioneReload.setValutazioni(fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento));
				valutazioneService.save(valutazioneReload);

				//segretario valutatore
				if(verbale != null) {
					verbale.setValutatore(Utils.getAuthenticatedUser().getAccount());
					accreditamento.setVerbaleValutazioneSulCampo(verbale);
				}
				//accreditamentoRepository.save(accreditamento);
				saveAndAudit(accreditamento);


				//è qui che è stata settala la data di valutazione del verbale e tutti i suoi componenti???
				// risposta: sì, ma rimane modificabile per tutta la durata di valutazione sul campo
				Set<String> dst = new HashSet<String>();
				if(verbale != null){
					if(verbale.getTeamLeader() != null)
						dst.add(verbale.getTeamLeader().getEmail());
					if(verbale.getReferenteInformatico() != null)
						dst.add(verbale.getReferenteInformatico().getEmail());

					if(verbale.getComponentiSegreteria() != null){
						for(Account a : verbale.getComponentiSegreteria())
							dst.add(a.getEmail());
					}
					emailService.inviaConvocazioneValutazioneSulCampo(dst, verbale.getDataoraVisita(), accreditamento.getProvider().getDenominazioneLegale());
				}

				//non deve creare un task di valutazione per il team leader.. deve solo mandare il flusso in valutazione sul campo con lo stesso
				//attore (segretario) che deve inserire la valutazione sul campo.. solo se l'accreditamento va in integrazione il teamleader deve valutare
				workflowService.eseguiTaskValutazioneAssegnazioneTeamLeaderForCurrentUser(accreditamento, verbale.getTeamLeader().getUsernameWorkflow());
			}
		}
	}

	@Override
	@Transactional
	public void inviaValutazioneTeamLeaderStandard(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invia Valutazione TeamLeader " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account user = Utils.getAuthenticatedUser().getAccount();
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, user.getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());

		workflowService.eseguiTaskValutazioneTeamLeaderForCurrentUser(accreditamento);

		//azzera il suo contatore di valutazioni non date consecutivamente e svuota la lista
		user.setValutazioniNonDate(0);
		user.setDomandeNonValutate(new HashSet<Accreditamento>());
		accountRepository.save(user);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public void approvaIntegrazione(Long accreditamentoId) throws Exception{

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();
		//NB sono in valutazione
		AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();

		Set<FieldValutazioneAccreditamento> fieldValutazioniSegreteria = fieldValutazioneAccreditamentoService.getAllFieldValutazioneForAccreditamentoBySegreteriaNotStoricizzato(accreditamentoId);
		Set<FieldIntegrazioneAccreditamento> fieldIntegrazione = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamentoByContainer(accreditamentoId, stato, workFlowProcessInstanceId);

		Set<FieldIntegrazioneAccreditamento> approved = new HashSet<FieldIntegrazioneAccreditamento>();
		Set<FieldIntegrazioneAccreditamento> notApproved = new HashSet<FieldIntegrazioneAccreditamento>();

		fieldValutazioniSegreteria.forEach(v -> {
			FieldIntegrazioneAccreditamento field = null;
			if(v.getIdField().getGruppo().isEmpty()) {
				if(v.getEsito().booleanValue()){
					if(v.getObjectReference() == -1)
						field = Utils.getField(fieldIntegrazione, v.getIdField());
					else
						field = Utils.getField(fieldIntegrazione,v.getObjectReference(), v.getIdField());
					if(field != null)
						approved.add(field);
				}
				else {
					if(v.getObjectReference() == -1)
						field = Utils.getField(fieldIntegrazione, v.getIdField());
					else
						field = Utils.getField(fieldIntegrazione,v.getObjectReference(), v.getIdField());
					if(field != null)
						notApproved.add(field);
				}
			}
			//gestione del raggruppamento (non prevista per i multiinstanza)
			else {
				if(v.getEsito().booleanValue()){
					for(IdFieldEnum id : v.getIdField().getGruppo()) {
						field = Utils.getField(fieldIntegrazione, id);
						if(field != null) {
							approved.add(field);
						}
					}
				}
				else {
					for(IdFieldEnum id : v.getIdField().getGruppo()) {
						field = Utils.getField(fieldIntegrazione, id);
						if(field != null) {
							notApproved.add(field);
						}
					}
				}
			}
		});

		if(!accreditamento.isVariazioneDati()) {
			//calcolo se mi trovo in una situazione di integrazione o di preavviso di rigetto
			//guardo la data di inizio del preavviso di rigetto -> se è null devo per forza trovarmi ancora in integrazione
			if(accreditamento.getDataPreavvisoRigettoInizio() == null)
				accreditamento.setPresaVisioneIntegrazione(false);
			else accreditamento.setPresaVisionePreavvisoDiRigetto(false);
		}

		integrazioneService.applyIntegrazioneAccreditamentoAndSave(accreditamentoId, approved);
		integrazioneService.cancelObjectNotApproved(accreditamentoId, notApproved);

		fieldIntegrazioneAccreditamentoService.applyIntegrazioneInContainer(accreditamentoId, stato, workFlowProcessInstanceId);

	}

	/* Riassegna la domanda alla valutazione di un altro gruppo CRECM */
	@Override
	public void riassegnaGruppoCrecm(Long accreditamentoId, Set<Account> refereeGroup) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Riassegnamento domanda di Accreditamento " + accreditamentoId + " ad un ALTRO gruppo CRECM"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		// crea le valutazioni per i nuovi referee
		List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
		for (Account a : refereeGroup) {
			Valutazione valutazioneReferee = new Valutazione(a, accreditamento, ValutazioneTipoEnum.REFEREE);
			//medoto per la gestione delle valutazioni
			valutazioneService.initializeFieldValutazioni(valutazioneReferee, accreditamento);
			valutazioneService.save(valutazioneReferee);
			emailService.inviaNotificaAReferee(a.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
			usernameWorkflowValutatoriCrecm.add(a.getUsernameWorkflow());
		}

		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);

		//il numero minimo di valutazioni necessarie (se 3 Referee -> minimo 2)
		Integer numeroValutazioniCrecmRichieste = new Integer(usernameWorkflowValutatoriCrecm.size() - 1);
		if(numeroValutazioniCrecmRichieste == 0)
			numeroValutazioniCrecmRichieste = 1;
		workflowService.eseguiTaskAssegnazioneCrecmForCurrentUser(accreditamento, usernameWorkflowValutatoriCrecm, numeroValutazioniCrecmRichieste);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	/* approva/nega le modifiche al provider, blocca e storicizza la valutazione e procede nel riassegnare la valutazione della domanda al gruppo Crecm
	 * (solo nei campi relativi all'integrazione) */
	@Override
	public void inviaValutazioneSegreteriaProvvisorio(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Salvataggio valutazione segreteria e riassegnamento domanda di Accreditamento " + accreditamentoId + " allo STESSO gruppo CRECM"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Valutazione valutazioneSegreteria = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();
		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();

		approvaIntegrazione(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazioneSegreteria, valutazioneComplessiva, accreditamento.getStato());

		//elimino le date delle vecchie valutazioni
		Set<Account> valutatori = valutazioneService.getAllValutatoriForAccreditamentoId(accreditamentoId);
		List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
		for(Account a : valutatori) {
			if(a.isReferee()) {
				usernameWorkflowValutatoriCrecm.add(a.getUsernameWorkflow());
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, a.getId());
				valutazioneService.sbloccaValutazioneByFieldIntegrazioneList(valutazione, fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazionePerSbloccoValutazioneForAccreditamentoByContainer(accreditamentoId, stato, workFlowProcessInstanceId));
				valutazione.setDataValutazione(null);
				valutazioneService.save(valutazione);
				emailService.inviaNotificaAReferee(a.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
			}
		}

		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);

		workflowService.eseguiTaskValutazioneSegreteriaForCurrentUser(accreditamento, false, usernameWorkflowValutatoriCrecm);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	/* approva/nega le modifiche al provider, blocca e storicizza la valutazione e procede nel riassegnare la valutazione della domanda al Team Leader
	 * (solo nei campi relativi all'integrazione) */
	@Override
	public void inviaValutazioneSegreteriaStandard(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Salvataggio valutazione segreteria e riassegnamento domanda di Accreditamento " + accreditamentoId + " allo STESSO gruppo CRECM"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Valutazione valutazioneSegreteria = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		AccreditamentoStatoEnum statoIntegrazione = accreditamento.getStatoUltimaIntegrazione();
		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();

		approvaIntegrazione(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazioneSegreteria, valutazioneComplessiva, accreditamento.getStato());

		Account accountTeamLeader = accreditamento.getVerbaleValutazioneSulCampo().getTeamLeader();
		String usernameWorkflowTeamLeader = accountTeamLeader.getUsernameWorkflow();

		//controllo se ho gia la valutazione per l'utente corrente
		Valutazione valutazioneTL = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, accountTeamLeader.getId());

		//prima volta che il Team Leader valuta
		if(valutazioneTL == null) {
			valutazioneTL = new Valutazione(accountTeamLeader, accreditamento, ValutazioneTipoEnum.TEAM_LEADER);
			//medoto per la gestione delle valutazioni
			valutazioneService.initializeFieldValutazioni(valutazioneTL, accreditamento);
		}
		//ha già una valutazione
		else {
			Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazionePerSbloccoValutazioneForAccreditamentoByContainer(accreditamentoId, statoIntegrazione, workFlowProcessInstanceId);
			valutazioneService.sbloccaValutazioneByFieldIntegrazioneList(valutazioneTL, fieldIntegrazioneList);
			valutazioneTL.setDataValutazione(null);
		}

		valutazioneService.save(valutazioneTL);
		emailService.inviaNotificaATeamLeader(accountTeamLeader.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
		workflowService.eseguiTaskValutazioneSegreteriaTeamLeaderForCurrentUser(accreditamento, false, usernameWorkflowTeamLeader);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	//TODO al secondo giro e al terzo questo sarebbe il valuta domanda della segreteria.. sarebbe da fare un corrispettivo per lo standard
	//dove al primo giro crea la valutazione del team leader e al terzo la riassegna..
	@Deprecated
	@Override
	public void assegnaStessoGruppoCrecm(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Salvataggio valutazione segreteria e riassegnamento domanda di Accreditamento " + accreditamentoId + " allo STESSO gruppo CRECM"));
		Valutazione valutazioneSegreteria = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		approvaIntegrazione(accreditamentoId);

		//disabilito tutti i filedValutazioneAccreditamento
		for (FieldValutazioneAccreditamento fva : valutazioneSegreteria.getValutazioni()) {
			fva.setEnabled(false);
		}

		//setta la data (per la presa visione)
		valutazioneSegreteria.setDataValutazione(LocalDateTime.now());
		//Non dovrebbe servire perche' passando in AssegnazioneCRECM la valutazione della segreteria è già bloccata
		/*
		//disabilito tutti i filedValutazioneAccreditamento
		for (FieldValutazioneAccreditamento fva : valutazioneSegreteria.getValutazioni()) {
			fva.setEnabled(false);
		}
		*/

		//inserisce il commento complessivo
		valutazioneSegreteria.setValutazioneComplessiva(valutazioneComplessiva);

		//setta lo stato dell'accreditamento al momento del salvataggio
		valutazioneSegreteria.setAccreditamentoStatoValutazione(accreditamento.getStato());

		valutazioneService.save(valutazioneSegreteria);

		valutazioneService.copiaInStorico(valutazioneSegreteria);

		//elimino le date delle vecchie valutazioni
		Set<Account> valutatori = valutazioneService.getAllValutatoriForAccreditamentoId(accreditamentoId);
		List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
		for(Account a : valutatori) {
			if(a.isReferee()) {
				usernameWorkflowValutatoriCrecm.add(a.getUsernameWorkflow());
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, a.getId());

				//TODO potrebbe spaccarsi perchè esistono gia - setta i campi valutati positivamente di default
				Set<FieldValutazioneAccreditamento> defaults = fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento);
				for(FieldValutazioneAccreditamento f : valutazione.getValutazioni()){
					defaults.removeIf(field -> (
													field.getIdField() == f.getIdField() &&
													field.getObjectReference() == f.getObjectReference() &&
													field.getAccreditamento().getId() == f.getAccreditamento().getId()
													)
										);
				}

				if(!defaults.isEmpty())
					valutazione.getValutazioni().addAll(defaults);

				valutazione.setDataValutazione(null);
				valutazioneService.save(valutazione);
				emailService.inviaNotificaAReferee(a.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
			}
		}

		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);

		workflowService.eseguiTaskValutazioneSegreteriaForCurrentUser(accreditamento, false, usernameWorkflowValutatoriCrecm);
	}

	@Deprecated
	@Override
	public void assegnaTeamLeader(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Riassegnamento domanda di Accreditamento " + accreditamentoId + " al Team Leader"));
		Valutazione valutazioneSegreteria = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		approvaIntegrazione(accreditamentoId);

		//setta la data (per la presa visione)
		valutazioneSegreteria.setDataValutazione(LocalDateTime.now());

		//disabilito tutti i filedValutazioneAccreditamento
		for (FieldValutazioneAccreditamento fva : valutazioneSegreteria.getValutazioni()) {
			fva.setEnabled(false);
		}

		//inserisce il commento complessivo
		valutazioneSegreteria.setValutazioneComplessiva(valutazioneComplessiva);

		//setta lo stato dell'accreditamento al momento del salvataggio
		valutazioneSegreteria.setAccreditamentoStatoValutazione(accreditamento.getStato());

		valutazioneService.save(valutazioneSegreteria);

		valutazioneService.copiaInStorico(valutazioneSegreteria);

		Account accountTeamLeader = accreditamento.getVerbaleValutazioneSulCampo().getTeamLeader();
		String usernameWorkflowTeamLeader = accountTeamLeader.getUsernameWorkflow();

		//accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);

		workflowService.eseguiTaskValutazioneSegreteriaTeamLeaderForCurrentUser(accreditamento, false, usernameWorkflowTeamLeader);
	}

	@Override
	public void presaVisione(Long accreditamentoId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Presa visione della conferma dei dati da parte del provider e cambiamento stato della domanda " + accreditamentoId + " in INS_ODG"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		accreditamento.setDataInserimentoOdg(LocalDate.now());

		//calcolo se mi trovo in una situazione di presa visione dell'integrazione o del preavviso di rigetto
		//guardo la data di inizio del preavviso di rigetto -> se è null devo per forza trovarmi ancora in integrazione
		if(accreditamento.getDataPreavvisoRigettoInizio() == null)
			accreditamento.setPresaVisioneIntegrazione(true);
		else accreditamento.setPresaVisionePreavvisoDiRigetto(true);

		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);


		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();
		AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();

		//setto il container dei field integrazione ad applicati
		fieldIntegrazioneAccreditamentoService.applyIntegrazioneInContainer(accreditamentoId, stato, workFlowProcessInstanceId);

		if(accreditamento.isProvvisorio())
			workflowService.eseguiTaskValutazioneSegreteriaForCurrentUser(accreditamento, true, null);
		else if (accreditamento.isStandard())
			workflowService.eseguiTaskValutazioneSegreteriaTeamLeaderForCurrentUser(accreditamento, true, null);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);

	}

	@Override
	@Transactional
	public void inviaRichiestaIntegrazione(Long accreditamentoId, Long giorniTimer) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio Richiesta Integrazione della domanda " + accreditamentoId + " alla Firma"));

		boolean conteggioGiorniAvanzatoAbilitato = ecmProperties.isConteggioGiorniAvanzatoAbilitato();
		boolean conteggioGiorniAvanzatoBeforeDayMode = ecmProperties.isConteggioGiorniAvanzatoBeforeDayMode();

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Long timerIntegrazioneRigetto = giorniTimer * millisecondiInGiorno;

		if (conteggioGiorniAvanzatoAbilitato && !conteggioGiorniAvanzatoBeforeDayMode) {
			timerIntegrazioneRigetto = millisecondsToAdd(giorniTimer);
		}
		else if (conteggioGiorniAvanzatoAbilitato && conteggioGiorniAvanzatoBeforeDayMode) {
			giorniTimer--;
			timerIntegrazioneRigetto = millisecondsToAdd(giorniTimer);
		}

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(accreditamento.getWorkflowInCorso().getTipo() == TipoWorkflowEnum.ACCREDITAMENTO) {
			accreditamento.setGiorniIntegrazione(giorniTimer);
		} else {
			accreditamento.getWorkflowInCorso().setGiorniIntegrazione(giorniTimer);
		}
		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);

		if(ecmProperties.isDebugTestMode() && giorniTimer < 0) {
			//Per efffettuare i test si da la possibilità di inserire il tempo in minuti
			timerIntegrazioneRigetto = (-giorniTimer) * millisecondiInMinuto;
		}
		workflowService.eseguiTaskRichiestaIntegrazioneForCurrentUser(accreditamento, timerIntegrazioneRigetto);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);

	}

	public Long millisecondsToAdd(Long giorniTimer) {
		long millisecondiInGiorno = 86400000;
		long millisecondiInMinuto = 60000;
		//we get the current time in milliseconds
		LocalDateTime currentTime = LocalDateTime.now();
		Long currentHourInMilliseconds = (currentTime.getHour()*60)*millisecondiInMinuto;
		Long currentMinuteInMilliseconds = currentTime.getMinute()*millisecondiInMinuto;
		Long currentTimeInMillisecods = currentHourInMilliseconds + currentMinuteInMilliseconds;

		//we calculate the added time so that the timer in bonita stops at 23:59
		Long milliseconds2359 = millisecondiInGiorno - millisecondiInMinuto;
		Long addedTimeInMilliseconds = milliseconds2359 - currentTimeInMillisecods;

		//returns giorniTimer + added time from the moment the method is called till 23:59 in milliseconds
		return (giorniTimer * millisecondiInGiorno) + addedTimeInMilliseconds;
	}

	@Override
	@Transactional
	public void inviaRichiestaIntegrazioneInAttesaDiFirma(Long accreditamentoId, Long fileId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio Richiesta Integrazione della domanda " + accreditamentoId + " al Protocollo"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		File fileFirmato = fileService.getFile(fileId);

		//salvo nel file che viene protocollato l'operatore che effettua la richiesta
		fileFirmato.setOperatoreProtocollo(Utils.getAuthenticatedUser().getAccount());
		fileService.save(fileFirmato);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		accreditamento.setRichiestaIntegrazione(fileFirmato);
		saveAndAudit(accreditamento);
		workflowService.eseguiTaskFirmaIntegrazioneForCurrentUser(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	@Transactional
	public void inviaRichiestaPreavvisoRigetto(Long accreditamentoId, Long giorniTimer) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio Richiesta Preavviso Rigetto della domanda " + accreditamentoId + " alla Firma"));

		boolean conteggioGiorniAvanzatoAbilitato = ecmProperties.isConteggioGiorniAvanzatoAbilitato();
		boolean conteggioGiorniAvanzatoBeforeDayMode = ecmProperties.isConteggioGiorniAvanzatoBeforeDayMode();

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Long timerIntegrazioneRigetto = giorniTimer * millisecondiInGiorno;

		if (conteggioGiorniAvanzatoAbilitato && !conteggioGiorniAvanzatoBeforeDayMode) {
			timerIntegrazioneRigetto = millisecondsToAdd(giorniTimer);
		}
		else if (conteggioGiorniAvanzatoAbilitato && conteggioGiorniAvanzatoBeforeDayMode) {
			giorniTimer--;
			timerIntegrazioneRigetto = millisecondsToAdd(giorniTimer);
		}

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		accreditamento.setGiorniPreavvisoRigetto(giorniTimer);
		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);

		if(ecmProperties.isDebugTestMode() && giorniTimer < 0) {
			//Per efffettuare i test si da la possibilità di inserire il tempo in minuti
			timerIntegrazioneRigetto = (-giorniTimer) * millisecondiInMinuto;
		}
		workflowService.eseguiTaskRichiestaPreavvisoRigettoForCurrentUser(accreditamento, timerIntegrazioneRigetto);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	@Transactional
	public void inviaRichiestaPreavvisoRigettoInAttesaDiFirma(Long accreditamentoId, Long fileId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio Richiesta Preavviso Rigetto della domanda " + accreditamentoId + " al Protocollo"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		File fileFirmato = fileService.getFile(fileId);

		//salvo nel file che viene protocollato l'operatore che effettua la richiesta
		fileFirmato.setOperatoreProtocollo(Utils.getAuthenticatedUser().getAccount());
		fileService.save(fileFirmato);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		accreditamento.setRichiestaPreavvisoRigetto(fileFirmato);
		saveAndAudit(accreditamento);
		workflowService.eseguiTaskFirmaPreavvisoRigettoForCurrentUser(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	/* invia l'integrazione del provider, sblocca i campi relativi e li flagga nella valutazione della segreteria
	 * N.B. i fieldIntegrazione fittizi sono quei campi che il provider non ha modificato e non ha nemmeno risalvato, per i quali i
	 * fieldIntegrazione non sono quindi stati salvati (vengono calcolati dai fieldEditabili prima della loro eliminazione) */
	@Override
	public void inviaIntegrazione(Long accreditamentoId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Integrazione della domanda " + accreditamentoId + " inviata alla segreteria per essere valutata"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();
		AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();
		FieldIntegrazioneHistoryContainer container = fieldIntegrazioneAccreditamentoService.getContainer(accreditamentoId, stato, workFlowProcessInstanceId);

		//controllo quali campi sono stati modificati e quali confermati
		Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = container.getIntegrazioni();
		integrazioneService.checkIfFieldIntegrazioniConfirmedForAccreditamento(accreditamentoId, fieldIntegrazioneList);
		fieldIntegrazioneAccreditamentoService.saveSet(fieldIntegrazioneList);

		//per i campi modificati...elimino i field integrazione su tutte le valutazioni presenti
		Set<FieldIntegrazioneAccreditamento> fieldModificati = fieldIntegrazioneAccreditamentoService.getModifiedFieldIntegrazioneForAccreditamento(accreditamentoId, stato, workFlowProcessInstanceId);

		//se ci sono state delle modifiche ri-abilito la valutazione cancellando la data
		//altrimenti non faccio nulla perche' si passa per forza dal PRESA VISIONE
		//dpranteda 18/06/2018: se il flusso è di tipo VARIAZIONI DATI ri-abilito comunque per evidenziare i campi non modificati
		if((fieldModificati != null && !fieldModificati.isEmpty()) || accreditamento.isVariazioneDati()){
			//elimina data valutazione se flusso di accreditamento
			if(!accreditamento.isVariazioneDati()) {
				Valutazione valutazione = valutazioneService.getValutazioneSegreteriaForAccreditamentoIdNotStoricizzato(accreditamentoId);
				valutazione.setDataValutazione(null);
				valutazioneService.save(valutazione);
			}

			//salvo la lista di fieldIntegrazione fittizia, per applicare sui fieldValutazione l'info che un campo abilitato non è stato modificato dal provider
			//il fieldIntegrazione verrà utilizzato solo per richiamare la 'sbloccaValutazioniByFieldIntegrazioneList' è realizzato valorizzando solo
			//i campi: objectReference, idField, isModificato, isFittizio
			//Long id = -1L;
			List<FieldIntegrazioneAccreditamento> fieldIntegrazioneListFITTIZIA = new ArrayList<FieldIntegrazioneAccreditamento>();
			Set<FieldEditabileAccreditamento> fieldEditabileList = fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamentoId);
			if(fieldEditabileList != null){
				for(FieldEditabileAccreditamento fieldEditabile : fieldEditabileList){
					//per ogni fieldEditabile (abilitato attraverso l'enableField sulla domanda), controllo se NON esiste il fieldIntegrazione creato dal provider
					FieldIntegrazioneAccreditamento fieldIntegrazione = null;
					Long objRef = fieldEditabile.getObjectReference();
					if(fieldEditabile.getObjectReference() != -1){
						fieldIntegrazione = Utils.getField(fieldIntegrazioneList, fieldEditabile.getObjectReference(), fieldEditabile.getIdField());
					}else{
						fieldIntegrazione = Utils.getField(fieldIntegrazioneList, fieldEditabile.getIdField());
					}

					if(fieldIntegrazione == null){
						//NON esiste il fieldIntegrazione creato dal provider...creo quello fittizio
						if(fieldEditabile.getObjectReference() != -1){
							fieldIntegrazione = new FieldIntegrazioneAccreditamento(fieldEditabile.getIdField(), fieldEditabile.getAccreditamento(), fieldEditabile.getObjectReference(),null,null);
						}else{
							fieldIntegrazione = new FieldIntegrazioneAccreditamento(fieldEditabile.getIdField(), fieldEditabile.getAccreditamento(), null,null);
						}
						fieldIntegrazione.setModificato(false);
						//ciclo per i fieldIntegrazione "complessivi"
						if(!fieldEditabile.getIdField().getGruppo().isEmpty()) {
							for(IdFieldEnum id : fieldEditabile.getIdField().getGruppo()) {
								FieldIntegrazioneAccreditamento fieldIntegrazioneChild = null;
								if(objRef != -1){
									fieldIntegrazioneChild = Utils.getField(fieldIntegrazioneList, objRef, id);
								}else{
									fieldIntegrazioneChild = Utils.getField(fieldIntegrazioneList, id);
								}
								if(fieldIntegrazioneChild != null && fieldIntegrazioneChild.isModificato()) {
									fieldIntegrazione.setModificato(true);
									break;
								}
							}
						}
						//fieldIntegrazione.setId(id--);
						fieldIntegrazione.setFittizio(true);
						//dpranteda 18/06/2018: per i full devo creare il field non fittizio altrimenti non riesco ad abilitare le valutazioni
						//abilitando delle valutazioni con field non fittizi che non hanno valori...come si comporta l'applyintegrazione????
//						if(IdFieldEnum.isFull(fieldIntegrazione.getIdField()))
//							fieldIntegrazione.setFittizio(false);
						fieldIntegrazioneListFITTIZIA.add(fieldIntegrazione);
					}
				}

				if(fieldIntegrazioneListFITTIZIA != null && !fieldIntegrazioneListFITTIZIA.isEmpty()){
					fieldIntegrazioneAccreditamentoService.save(fieldIntegrazioneListFITTIZIA);
					container.getIntegrazioni().addAll(fieldIntegrazioneListFITTIZIA);
					fieldIntegrazioneAccreditamentoService.saveContainer(container);
				}
			}

			//setto il flag per vedere se ci sono state modifiche di integrazione nei field valutazioni, elimino il vecchio esito e li riabilito
			Valutazione valutazione = valutazioneService.getValutazioneSegreteriaForAccreditamentoIdNotStoricizzato(accreditamentoId);
			valutazioneService.sbloccaValutazioneByFieldIntegrazioneList(valutazione, container.getIntegrazioni());
		}

		//TODO non spacca niente???
		fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);

		if(accreditamento.isIntegrazione()){
			emailService.inviaConfermaReInvioIntegrazioniAccreditamento(accreditamento.isStandard(), false, accreditamento.getProvider());
		}
		else if(accreditamento.isPreavvisoRigetto()){
			emailService.inviaConfermaReInvioIntegrazioniAccreditamento(accreditamento.isStandard(), true, accreditamento.getProvider());
		}

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public void eseguiTaskInviaIntegrazione(Long accreditamentoId) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Esecuzione Task - Integrazione della domanda " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		if(accreditamento.isIntegrazione()){
			workflowService.eseguiTaskIntegrazioneForCurrentUser(accreditamento);
		}
		else if(accreditamento.isPreavvisoRigetto()){
			workflowService.eseguiTaskPreavvisoRigettoForCurrentUser(accreditamento);
		}
		else if(accreditamento.isModificaDati()){
			workflowService.eseguiTaskIntegrazioneForCurrentUser(accreditamento);
		}

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}



	@Override
	public DatiAccreditamento getDatiAccreditamentoForAccreditamentoId(Long accreditamentoId) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Recupero datiAccreditamento per la domanda " + accreditamentoId));
		DatiAccreditamento datiAccreditamento = accreditamentoRepository.getDatiAccreditamentoForAccreditamento(accreditamentoId);
		if(datiAccreditamento == null)
			throw new Exception("Dati non presenti");
		return datiAccreditamento;
	}

	@Override
	public Long getProviderIdForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero providerId per domanda " + accreditamentoId));
		return accreditamentoRepository.getProviderIdById(accreditamentoId);
	}

	@Override
	public Set<Accreditamento> getAllAccreditamentiInviati(){
		LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento inviate alla segreteria"));
		return accreditamentoRepository.findAllByStato(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO);
	}

	@Override
	public int countAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Numero delle domande di accreditamento " + stato + " per provider " + providerId));
		return accreditamentoRepository.countAllByStatoAndProviderId(stato, providerId);
	}

	@Override
	public Set<Accreditamento> getAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento " + stato + " per provider " + providerId));
		return accreditamentoRepository.findAllByStatoAndProviderId(stato, providerId);
	}

	//recupera tutti gli accreditamenti a seconda dello stato e del tipo, il flag filterTaken settato a true aggiunge la richiesta
	//di filtrare tutti gli accreditamenti già presi in carica (la funzione restituisce così solo gli accreditamenti che possono essere presi in carica)
	@Override
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato,	AccreditamentoTipoEnum tipo, Boolean filterTaken) {
		if (tipo != null) {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento " + stato + " di tipo " + tipo + " NON prese in carica"));
				return accreditamentoRepository.findAllByStatoAndTipoDomandaNotTaken(stato, tipo);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento " + stato + " di tipo " + tipo));
				return accreditamentoRepository.findAllByStatoAndTipoDomanda(stato, tipo);
			}
		}
		else {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento " + stato + " NON prese in carica"));
				return accreditamentoRepository.findAllByStatoNotTaken(stato);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento " + stato));
				return accreditamentoRepository.findAllByStato(stato);
			}
		}
	}

	//conta tutti gli accreditamenti a seconda dello stato e del tipo, il flag filterTaken settato a true aggiunge la richiesta
	//di filtrare tutti gli accreditamenti già presi in carica (la funzione restituisce così solo il numero degli accreditamenti che possono essere presi in carica)
	@Override
	public int countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Boolean filterTaken) {
		if (tipo != null) {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " NON prese in carica"));
				return accreditamentoRepository.countAllByStatoAndTipoDomandaNotTaken(stato, tipo);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo));
				return accreditamentoRepository.countAllByStatoAndTipoDomanda(stato, tipo);
			}
		}
		else {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " NON prese in carica"));
				return accreditamentoRepository.countAllByStatoNotTaken(stato);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato));
				return accreditamentoRepository.countAllByStato(stato);
			}
		}
	}

	@Override
	public Set<Accreditamento> getAllAccreditamentiByGruppoAndTipoDomanda(String gruppo, AccreditamentoTipoEnum tipo, Boolean filterTaken) {
		Set<AccreditamentoStatoEnum> stati = AccreditamentoStatoEnum.getAllStatoByGruppo(gruppo);
		if (tipo != null) {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento del gruppo " + gruppo + " di tipo " + tipo + " NON prese in carica"));
				return accreditamentoRepository.findAllByStatoInAndTipoDomandaNotTaken(stati, tipo);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento del gruppo " + gruppo + " di tipo " + tipo));
				return accreditamentoRepository.findAllByStatoInAndTipoDomanda(stati, tipo);
			}
		}
		else {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento del gruppo " + gruppo + " NON prese in carica"));
				return accreditamentoRepository.findAllByStatoInNotTaken(stati);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento del gruppo " + gruppo));
				return accreditamentoRepository.findAllByStatoIn(stati);
			}
		}
	}

	@Override
	public int countAllAccreditamentiByGruppoAndTipoDomanda(String gruppo, AccreditamentoTipoEnum tipo, Boolean filterTaken) {
		Set<AccreditamentoStatoEnum> stati = AccreditamentoStatoEnum.getAllStatoByGruppo(gruppo);
		if (tipo != null) {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento del gruppo " + gruppo + " di tipo " + tipo + " NON prese in carica"));
				return accreditamentoRepository.countAllByStatoInAndTipoDomandaNotTaken(stati, tipo);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento del gruppo " + gruppo + " di tipo " + tipo));
				return accreditamentoRepository.countAllByStatoInAndTipoDomanda(stati, tipo);
			}
		}
		else {
			if (filterTaken != null && filterTaken == true) {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento del gruppo " + gruppo + " NON prese in carica"));
				return accreditamentoRepository.countAllByStatoInNotTaken(stati);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento del gruppo " + gruppo));
				return accreditamentoRepository.countAllByStatoIn(stati);
			}
		}
	}

	//recupera tutti gli accreditamenti in stato INS_ODG NON ancora inseriti in NESSUNA seduta NON bloccata
	@Override
	public Set<Accreditamento> getAllAccreditamentiInseribiliInODG() {
		LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento in stato INS_ODG NON inseriti in NESSUNA Seduta non bloccata/valutata"));
		return accreditamentoRepository.findAllAccreditamentiInseribiliInODG();
	}

	//conta tutti gli accreditamenti in stato INS_ODG NON ancora inseriti in NESSUNA seduta NON bloccata
	@Override
	public int countAllAccreditamentiInseribiliInODG() {
		LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento in stato INS_ODG NON inseriti in NESSUNA Seduta non bloccata/valutata"));
		return accreditamentoRepository.countAllAccreditamentiInseribiliInODG();
	}

	//recupera tutti gli accreditamenti a seconda dello stato e del tipo che sono state assegnate in valutazione ad un certo id utente
	@Override
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomandaForValutatoreId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long id, Boolean filterDone) {
		if (tipo != null) {
			if(filterDone != null && filterDone == true) {
				LOGGER.debug(Utils.getLogMessage("Recupero le domande di accreditamento " + stato + " di tipo " + tipo + " assegnate all'id: " + id + ", che NON ha ancora valutato"));
				return accreditamentoRepository.findAllByStatoAndTipoDomandaInValutazioneAssignedToAccountIdNotDone(stato, tipo, id);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Recupero le domande di accreditamento " + stato + " di tipo " + tipo + " assegnate all'id: " + id));
				return accreditamentoRepository.findAllByStatoAndTipoDomandaInValutazioneAssignedToAccountId(stato, tipo, id);
			}
		}
		else {
			if(filterDone != null && filterDone == true) {
				LOGGER.debug(Utils.getLogMessage("Recupero le domande di accreditamento " + stato + " assegnate all'id: " + id + ", che NON ha ancora valutato"));
				return accreditamentoRepository.findAllByStatoInValutazioneAssignedToAccountIdNotDone(stato, id);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Recupero le domande di accreditamento " + stato + " assegnate all'id: " + id));
				return accreditamentoRepository.findAllByStatoInValutazioneAssignedToAccountId(stato, id);
			}
		}
	}

	//conta tutti gli accreditamenti a seconda dello stato e del tipo che sono state assegnate in valutazione ad un certo id utente
	@Override
	public int countAllAccreditamentiByStatoAndTipoDomandaForValutatoreId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long id, Boolean filterDone) {
		if (tipo != null) {
			if(filterDone != null && filterDone == true) {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " assegnate all'id: " + id + ", che NON ha ancora valutato"));
				return accreditamentoRepository.countAllByStatoAndTipoDomandaInValutazioneAssignedToAccountIdNotDone(stato, tipo, id);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " assegnate all'id: " + id));
				return accreditamentoRepository.countAllByStatoAndTipoDomandaInValutazioneAssignedToAccountId(stato, tipo, id);
			}
		}
		else {
			if(filterDone != null && filterDone == true) {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " assegnate all'id: " + id + ", che NON ha ancora valutato"));
				return accreditamentoRepository.countAllByStatoInValutazioneAssignedToAccountIdNotDone(stato, id);
			}
			else {
				LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " assegnate all'id: " + id));
				return accreditamentoRepository.countAllByStatoInValutazioneAssignedToAccountId(stato, id);
			}
		}
	}

	//recupera tutti gli accreditamenti a seconda dello stato e del tipo che sono state assegnate ad un certo id provider
	@Override
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomandaForProviderId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long providerId) {
		if (tipo != null) {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " assegnate al provider: " + providerId));
			return accreditamentoRepository.findAllByStatoAndTipoDomandaAndProviderId(stato, tipo, providerId);
		}
		else {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " assegnate al provider: " + providerId));
			return accreditamentoRepository.findAllByStatoAndProviderId(stato, providerId);
		}
	}

	//conta tutti gli accreditamenti a seconda dello stato e del tipo che sono state assegnate ad un certo id provider
	@Override
	public int countAllAccreditamentiByStatoAndTipoDomandaForProviderId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long id) {
		if (tipo != null) {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " assegnate al provider: " + id));
			return accreditamentoRepository.countAllByStatoAndTipoDomandaAndProviderId(stato, tipo, id);
		}
		else {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " assegnate al provider: " + id));
			return accreditamentoRepository.countAllByStatoAndProviderId(stato, id);
		}
	}

	//recupera tutte le domande di accreditamento in scadenza
	//controlla se la data è compresa tra la data di scadenza e 30 giorni alla data di scadenza
	@Override
	public Set<Accreditamento> getAllAccreditamentiInScadenza() {
		//20180403 la dataScadenza non è più valorizzata quando l'accreditamento è in capo al provider
//		LocalDate oggi = LocalDate.now();
//		LocalDate dateScadenza = LocalDate.now().plusDays(30);
//		return accreditamentoRepository.findAllByDataScadenzaProssima(oggi, dateScadenza);
		return accreditamentoRepository.findAllAccreditamentiInScadenzaNeiProssimiGiorni(30);
	}

	//conta tutte le domande di accreditamento in scadenza
	//controlla se oggi + 30 giorni supera la data di scadenza
	@Override
	public long countAllAccreditamentiInScadenza() {
		//20180403 la dataScadenza non è più valorizzata quando l'accreditamento è in capo al provider
//		LocalDate oggi = LocalDate.now();
//		LocalDate dateScadenza = LocalDate.now().plusDays(30);
//		return accreditamentoRepository.countAllByDataScadenzaProssima(oggi, dateScadenza);
		return accreditamentoRepository.countAllAccreditamentiInScadenzaNeiProssimiGiorni(30);
	}

	@Override
	/*
	 * L'utente segreteria può prendere in carica una domanda se:
	 * 	+ La domanda è in stato VALUTAZIONE_SEGRTERIA_ASSEGNAMENTO
	 *  + E NON esiste già una valutazione di tipo SEGRETERIA_ECM (significa che nessun altro l'ha già presa in carico)
	 *	+ Deve esistere il corrispondente Task da prendere in carica nel flusso
	 */
	public boolean canUserPrendiInCarica(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		if(currentUser.isSegreteria() && getAccreditamento(accreditamentoId).isValutazioneSegreteriaAssegnamento()) {
			Set<Valutazione> valutazioniAccreditamento = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
			for (Valutazione v : valutazioniAccreditamento) {
				if(v.getTipoValutazione() == ValutazioneTipoEnum.SEGRETERIA_ECM)
					return false;
			}

			//TODO rimuovere il seguente "if" quando si avrà il flusso STANDARD
			if(getAccreditamento(accreditamentoId).isProvvisorio()) {
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(getAccreditamento(accreditamentoId));
			if(task == null){
				return false;
			}

			if(task.isAssigned())
				return false;
			}
			return true;
		}
		else
			return false;
	}

	@Override
	/*
	 * L'utente (segreteria | referee) può andare in validate e valutare se:
	 * 	+ ESISTE una valutazione agganciata al suo account e non è stata ancora inviata (dataValutazione == NULL)
	 *  + ESISTE il TASK relativo all'utente sul flusso
	 */
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser) throws Exception{
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		if( ((accreditamento.isValutazioneSegreteriaAssegnamento() || accreditamento.isValutazioneSulCampo() || accreditamento.isValutazioneSegreteria()) && currentUser.isSegreteria()) ||
			(accreditamento.isValutazioneCrecm() && currentUser.isReferee()) ||
			(accreditamento.isValutazioneTeamLeader() && currentUser.isReferee()) ||
			(accreditamento.isValutazioneSegreteriaVariazioneDati() && currentUser.isSegreteria()) ||
			(accreditamento.isValutazioneCrecmVariazioneDati() && currentUser.isReferee())){
			Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, currentUser.getAccount().getId());
			if(valutazione != null && valutazione.getDataValutazione() == null){
				TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
				//TODO rimuovere il seguente "if" quando si avrà il flusso STANDARD
				if(accreditamento.isStandard())
					return true;
				//TODO rimuovere il seguente "if" quando si avrà il flusso VARIAZIONE DATI E DOCUMENTI
				if(accreditamento.isVariazioneDati())
					return true;
				if(task == null){
					return false;
				}
				if(accreditamento.isValutazioneSegreteria()){
					if(!task.isAssigned() && !canUserPresaVisione(accreditamentoId, currentUser))
						return true;
					else
						return false;
				}else{
					if(!task.isAssigned())
						return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	/*
	 * L'utente (segreteria | referee) può visualizzare la valutazione:
	 * 	+ ESISTE una valutazione agganciata al suo account ed è stata inviata (dataValutazione != NULL)
	 */
	public boolean canUserValutaDomandaShow(Long accreditamentoId, CurrentUser currentUser) {
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, currentUser.getAccount().getId());
		if(valutazione != null && (currentUser.isSegreteria() || currentUser.isReferee()))
			return true;
		else return false;
	}

	@Override
	/*
	 * L'utente (segreteria | commissioneECM) può visualizzare tutte le valutazioni inviate
	 */
	public boolean canUserValutaDomandaShowRiepilogo(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.isSegreteria() || currentUser.isCommissioneEcm()){
			Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniCompleteForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
			return !valutazioni.isEmpty();
		}
		return false;
	}

	@Override
	/*
	 * L'utente (segreteria | commissioneECM) può visualizzare tutte le valutazioni inviate
	 */
	public boolean canUserValutaDomandaShowStorico(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.isSegreteria() || currentUser.isCommissioneEcm() || currentUser.isReferee()){
			Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniStoricizzateForAccreditamentoId(accreditamentoId);
			return !valutazioni.isEmpty();
		}
		return false;
	}

	@Override
	/*
	 * L'utente (segreteria) può riassegnare l'accreditamento ad un altro gruppo di referee crecm
	 */
	public boolean canRiassegnaGruppo(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.isSegreteria() && getAccreditamento(accreditamentoId).isAssegnamento())
			return true;
		return false;
	}

	@Override
	/*
	 * L'utente (segreteria) può riassegnare l'accreditamento allo stesso gruppo referee crecm
	 */
	public boolean canUserPresaVisione(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isValutazioneSegreteria()){
			//disabilitato per consentire ad ogni utente segreteria di fare "presa visione"
//			Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, currentUser.getAccount().getId());
//			if(valutazione != null && valutazione.getTipoValutazione() == ValutazioneTipoEnum.SEGRETERIA_ECM && valutazione.getDataValutazione() != null){
			Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();
			AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();
			Set<FieldIntegrazioneAccreditamento> fields = fieldIntegrazioneAccreditamentoService.getModifiedFieldIntegrazioneForAccreditamento(accreditamentoId, stato, workFlowProcessInstanceId);
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(fields == null || fields.isEmpty())
				return true;
		}
//		}
		return false;
	}

	@Override
	/*
	 * L'utente (segreteria) può abilitare i campi per eventuale modifica
	 */
	public boolean canUserEnableField(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && (accreditamento.isRichiestaIntegrazione() || accreditamento.isRichiestaPreavvisoRigetto())){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
			return true;
		}
		return false;
	}

	@Override
	public boolean canUserInviaRichiestaIntegrazione(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		return canUserEnableField(accreditamentoId, currentUser) || canUserInviaCampiVariazioneDati(accreditamentoId, currentUser);
	}

	@Override
	public boolean canUserinviaRichiestaIntegrazioneInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser)	throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isRichiestaIntegrazioneInAttesaDiFirma()){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
			return true;
		}
		return false;
	}

	@Override
	public boolean canUserinviaRichiestaPreavvisoRigettoInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser)	throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isRichiestaPreavvisoRigettoInAttesaDiFirma()){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
			return true;
		}
		return false;
	}

	@Override
	/*
	 * La domanda deve essere in INTEGRAZIONE
	 * 	+	L'utente provider titolare della domanda
	 * 	+ 	La segreteria
	 * */
	public boolean canUserInviaIntegrazione(Long accreditamentoId, CurrentUser currentUser) throws Exception{
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(accreditamento.isIntegrazione() || accreditamento.isPreavvisoRigetto()){
			if(currentUser.isProvider()){
				Long providerId = getProviderIdForAccreditamento(accreditamentoId);
				if(currentUser.getAccount().getProvider() != null &&  currentUser.getAccount().getProvider().getId().equals(providerId)){
					TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
					if(task == null){
						return false;
					}
					return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean canUserInviaVariazioneDati(Long accreditamentoId, CurrentUser currentUser) {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(accreditamento.isModificaDati() && currentUser.isProvider()){
			Long providerId = getProviderIdForAccreditamento(accreditamentoId);
			if(currentUser.getAccount().getProvider() != null &&  currentUser.getAccount().getProvider().getId().equals(providerId)){
				return true;
			}
		}
		return false;
	}

	@Transactional
	@Override
	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception  {
		changeState(accreditamentoId, stato, null);
	}

	@Transactional
	@Override
	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato, Boolean eseguitoDaUtente) throws Exception  {
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);

		WorkflowInfo workflowInCorso = accreditamento.getWorkflowInCorso();
		if(workflowInCorso == null)
			throw new Exception("AccreditamentoService - changeState: Impossibile ricavare il workflow in corso per l'accreaditamento id: " + accreditamento.getId());
		Boolean presaVisione = false;
		Boolean missedTLValutazione = false;
		//In alcuni stati devono essere effettuate altre operazioni
		//Creazione pdf
		if(workflowInCorso.getTipo() == TipoWorkflowEnum.ACCREDITAMENTO) {
			if(stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO) {
				//20180403 ora viene gestito internamente al entity accreditamento sul cambio stato
				//accreditamento.startRestartConteggio();
			} else if(stato == AccreditamentoStatoEnum.INTEGRAZIONE) {
				//20180403 ora viene gestito internamente al entity accreditamento sul cambio stato
				//accreditamento.standbyConteggio();
				accreditamento.setDataIntegrazioneInizio(LocalDate.now());
			} else if(stato == AccreditamentoStatoEnum.PREAVVISO_RIGETTO) {
				//20180403 ora viene gestito internamente al entity accreditamento sul cambio stato
				//accreditamento.standbyConteggio();
				accreditamento.setDataPreavvisoRigettoInizio(LocalDate.now());
			} else if(stato == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE_IN_FIRMA) {
				//Ricavo la seduta
				Seduta seduta = null;
				for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
					//TODO nel caso vengano agganciati piu' flussi alla domanda occorre prendere l'ultima ValutazioneCommissione
					if(valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
						seduta = valCom.getSeduta();
					}
				}
				Set<FieldEditabileAccreditamento> fieldEditabiliAccreditamento = fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamento.getId());
				List<String> listaCriticita = new ArrayList<String>();
				fieldEditabiliAccreditamento.forEach(v -> {
					if(!v.getIdField().hasGruppo()) {
			            //Richiesta
			            //Riepilogo_Consegne_ECM_20.10.2016.docx - Modulo 7 - 40 - a [inserire singole note sui campi] (pag 4)
						if(v.getNota() == null || v.getNota().isEmpty())
							listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()));
						else
							listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()) + "\n" + v.getNota());
					}
				});
				PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo integrazioneInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(accreditamento, seduta, listaCriticita);
				integrazioneInfo.setGiorniIntegrazionePreavvisoRigetto(accreditamento.getGiorniIntegrazione());
				File file = null;
				if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
					file = pdfService.creaPdfAccreditamentoProvvisiorioIntegrazione(integrazioneInfo);
				else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD)
					file = pdfService.creaPdfAccreditamentoStandardIntegrazione(integrazioneInfo);
				accreditamento.setRichiestaIntegrazione(file);
				//invia l'email di notifica al responsabile segreteria ECM
				sendEmailToResponsabili(accreditamentoId);
			} else if(stato == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE_IN_PROTOCOLLAZIONE) {
				File file = accreditamento.getRichiestaIntegrazione();
				protocolloService.protocollaAllegatoFlussoDomandaInUscita(accreditamentoId, file.getId());
				accreditamento.setDataoraInvioProtocollazione(LocalDateTime.now());
				//protocollo il file

			} else if(stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA) {
				//mi sono spostato da INTEGRAZIONE o PREAVVISO_RIGETTO a VALUTAZIONE_SEGRETERIA quindi rimuovo i fieldEditabili
				//20180403 ora viene gestito internamente al entity accreditamento sul cambio stato
				//accreditamento.startRestartConteggio();
				if(accreditamento.getStato() == AccreditamentoStatoEnum.INTEGRAZIONE) {
					accreditamento.setDataIntegrazioneFine(LocalDate.now());
					accreditamento.setIntegrazioneEseguitaDaProvider(eseguitoDaUtente);
				} else if(accreditamento.getStato() == AccreditamentoStatoEnum.PREAVVISO_RIGETTO) {
					accreditamento.setDataPreavvisoRigettoFine(LocalDate.now());
					accreditamento.setPreavvisoRigettoEseguitoDaProvider(eseguitoDaUtente);
				}
				inviaIntegrazione(accreditamentoId);
				fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);
			} else if(stato == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO_IN_FIRMA) {
				//Ricavo la seduta
				Seduta seduta = null;
				for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
					if(valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
						seduta= valCom.getSeduta();
					}
				}
				Set<FieldEditabileAccreditamento> fieldEditabiliAccreditamento = fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamento.getId());
				List<String> listaCriticita = new ArrayList<String>();
				fieldEditabiliAccreditamento.forEach(v -> {
					if(!v.getIdField().hasGruppo())
						listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()) + " - " + v.getNota());
				});
				PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo preavvisoRigettoInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(accreditamento, seduta, listaCriticita);
				preavvisoRigettoInfo.setGiorniIntegrazionePreavvisoRigetto(accreditamento.getGiorniPreavvisoRigetto());
				File file = null;
				if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
					file = pdfService.creaPdfAccreditamentoProvvisiorioPreavvisoRigetto(preavvisoRigettoInfo);
				else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD)
					file = pdfService.creaPdfAccreditamentoStandardPreavvisoRigetto(preavvisoRigettoInfo);
				accreditamento.setRichiestaPreavvisoRigetto(file);
				//invia l'email di notifica al responsabile segreteria ECM
				sendEmailToResponsabili(accreditamentoId);
			} else if(stato == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO_IN_PROTOCOLLAZIONE) {
				File file = accreditamento.getRichiestaPreavvisoRigetto();
				protocolloService.protocollaAllegatoFlussoDomandaInUscita(accreditamentoId, file.getId());
				accreditamento.setDataoraInvioProtocollazione(LocalDateTime.now());
			} else if(stato == AccreditamentoStatoEnum.DINIEGO_IN_FIRMA) {
				//Ricavo la seduta
				Seduta sedutaRigetto = null;
				Seduta sedutaIntegrazione = null;
				Seduta sedutaPreavvisoRigetto = null;
				for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
					if(valCom.getStato() == AccreditamentoStatoEnum.DINIEGO) {
						sedutaRigetto = valCom.getSeduta();
					} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
						sedutaIntegrazione = valCom.getSeduta();
					} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
						sedutaPreavvisoRigetto = valCom.getSeduta();
					}
				}
				/*
				Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneAccreditamento = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamento.getId());
				List<String> listaCriticita = new ArrayList<String>();
				fieldIntegrazioneAccreditamento.forEach(v -> {
					listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()));
				});*/
				PdfAccreditamentoProvvisorioRigettoInfo rigettoInfo = new PdfAccreditamentoProvvisorioRigettoInfo(accreditamento, sedutaRigetto, sedutaIntegrazione, sedutaPreavvisoRigetto);
				File file = null;
				if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
					file = pdfService.creaPdfAccreditamentoProvvisiorioDiniego(rigettoInfo);
				else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD)
					file = pdfService.creaPdfAccreditamentoStandardDiniego(rigettoInfo);
				accreditamento.setDecretoDiniego(file);
				//invia l'email di notifica al responsabile segreteria ECM
				sendEmailToResponsabili(accreditamentoId);
			} else if(stato == AccreditamentoStatoEnum.DINIEGO_IN_PROTOCOLLAZIONE) {
				File lettera = accreditamento.getLetteraAccompagnatoriaDiniego();
				File decreto = accreditamento.getDecretoDiniego();
				Set<Long> fileAllegatiIds = new HashSet<Long>();
				fileAllegatiIds.add(decreto.getId());
				protocolloService.protocollaAllegatoFlussoDomandaInUscita(accreditamentoId, lettera.getId(), fileAllegatiIds);
				accreditamento.setDataoraInvioProtocollazione(LocalDateTime.now());
				
				// ERM014776
				chiudiAccreditamentoEPulisciEventi(accreditamento);
				
			} else if(stato == AccreditamentoStatoEnum.DINIEGO) {
				//Setto il flusso come concluso
				workflowInCorso.setStato(StatoWorkflowEnum.CONCLUSO);
			} else if(stato == AccreditamentoStatoEnum.ACCREDITATO_IN_FIRMA) {
				//Ricavo la seduta
				Seduta sedutaAccreditamento = null;
				Seduta sedutaIntegrazione = null;
				Seduta sedutaPreavvisoRigetto = null;
				for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
					if(valCom.getStato() == AccreditamentoStatoEnum.ACCREDITATO) {
						sedutaAccreditamento = valCom.getSeduta();
					} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
						sedutaIntegrazione = valCom.getSeduta();
					} else if (valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO) {
						sedutaPreavvisoRigetto = valCom.getSeduta();
					}
				}
				//Set<FieldIntegrazioneAccreditamento> fieldIntegrazioneAccreditamento = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamento.getId());
				PdfAccreditamentoProvvisorioAccreditatoInfo accreditatoInfo = new PdfAccreditamentoProvvisorioAccreditatoInfo(accreditamento, sedutaAccreditamento, sedutaIntegrazione, sedutaPreavvisoRigetto);
				File file = null;
				if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
					file = pdfService.creaPdfAccreditamentoProvvisiorioAccreditato(accreditatoInfo);
				else if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD)
					file = pdfService.creaPdfAccreditamentoStandardAccreditato(accreditatoInfo);
				accreditamento.setDecretoAccreditamento(file);
				//invia l'email di notifica al responsabile segreteria ECM
				sendEmailToResponsabili(accreditamentoId);
			} else if(stato == AccreditamentoStatoEnum.ACCREDITATO_IN_PROTOCOLLAZIONE) {
				File lettera = accreditamento.getLetteraAccompagnatoriaAccreditamento();
				File decreto = accreditamento.getDecretoAccreditamento();
				Set<Long> fileAllegatiIds = new HashSet<Long>();
				fileAllegatiIds.add(decreto.getId());
				protocolloService.protocollaAllegatoFlussoDomandaInUscita(accreditamentoId, lettera.getId(), fileAllegatiIds);
				accreditamento.setDataoraInvioProtocollazione(LocalDateTime.now());
			} else if(stato == AccreditamentoStatoEnum.ACCREDITATO) {
				//Setto il flusso come concluso
				workflowInCorso.setStato(StatoWorkflowEnum.CONCLUSO);
				if(accreditamento.getDataInizioAccreditamento() != null) {
					try {
						//se è presente un precedente accreditamento ne setto la data fine al giorno prima della data inizio dell'accreditamento corrente
						Accreditamento prec = getAccreditamentoAttivoForProvider(accreditamento.getProvider().getId());
						prec.setDataFineAccreditamento(accreditamento.getDataInizioAccreditamento().minus(Period.ofDays(1)));
						saveAndAudit(prec);
					} catch (AccreditamentoNotFoundException e) {
						LOGGER.warn("ChangeState ad ACCREDITATO per Accreditamento.id: " + accreditamentoId + " - Il relativo provider: " + accreditamento.getProvider().getId() + " non ha accreditamenti attivi, non verrà modificata la data fine dell'accreditamento precedente", e);
					}
				}
			} else if(stato == AccreditamentoStatoEnum.INS_ODG) {
				//Cancelliamo le Valutazioni non completate dei referee e del team leader
				Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
				for(Valutazione v : valutazioni){
					if((v.getTipoValutazione() == ValutazioneTipoEnum.REFEREE || v.getTipoValutazione() == ValutazioneTipoEnum.TEAM_LEADER) && v.getDataValutazione() == null){
						//se la valutazione è del team leader riportiamo la mancata valutazione nella history del flusso
						if(v.getTipoValutazione() == ValutazioneTipoEnum.TEAM_LEADER)
							missedTLValutazione = true;
						valutazioneService.delete(v);
					}
				}
			}

			//calcolo se la segreteria ha fatto presa visione
			//sa da valutazione segreteria vado diretto in INS_ODG c'è stata presa visione
			if(accreditamento.getStato() == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA && stato == AccreditamentoStatoEnum.INS_ODG) {
				presaVisione =  true;
			}
			//20180403 modificato per gestire la dataScadenza
			accreditamento.setStato(stato, workflowInCorso.getTipo());
		} else if(workflowInCorso.getTipo() == TipoWorkflowEnum.VARIAZIONE_DATI) {
			if(stato == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE_IN_PROTOCOLLAZIONE) {
				//Ricavo la seduta
//				Seduta seduta = null;
//				for (ValutazioneCommissione valCom : accreditamento.getValutazioniCommissione()) {
//					//Prendo la seduta con data maggiore della data di avvio del flusso
//					if(valCom.getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE) {
//						if(valCom.getSeduta().getData().isAfter(workflowInCorso.getDataAvvio()))
//							seduta = valCom.getSeduta();
//					}
//				}
				Set<FieldEditabileAccreditamento> fieldEditabiliAccreditamento = fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamento.getId());
				List<String> listaCriticita = new ArrayList<String>();
				fieldEditabiliAccreditamento.forEach(v -> {
					if(!v.getIdField().hasGruppo()) {
						//Richiesta
						//Riepilogo_Consegne_ECM_20.10.2016.docx - Modulo 7 - 40 - a [inserire singole note sui campi] (pag 4)
						if(v.getNota() == null || v.getNota().isEmpty())
							listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()));
						else
							listaCriticita.add(messageSource.getMessage("IdFieldEnum." + v.getIdField().name(), null, Locale.getDefault()) + "\n" + v.getNota());
					}
				});
				//Da aggiungere se viene richiesta la creazione di file nel procedimento di "Variazione Dati"
//				PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo integrazioneInfo = new PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(accreditamento, seduta, listaCriticita);
//				integrazioneInfo.setGiorniIntegrazionePreavvisoRigetto(workflowInCorso.getGiorniIntegrazione());
//				File file = null;
//				//creaPdfAccreditamentoVariazioneDatiIntegrazione va modificato e' una copi di creaPdfAccreditamentoIntegrazione
//				file = pdfService.creaPdfAccreditamentoVariazioneDatiIntegrazione(integrazioneInfo);
//				protocolloService.protocollaAllegatoFlussoDomandaInUscita(accreditamentoId, file.getId());
//				workflowInCorso.setRichiestaVariazioneDati(file);
				//protocollo il file
			} else if(stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA) {
				//mi sono spostato da INTEGRAZIONE a VALUTAZIONE_SEGRETERIA quindi rimuovo i fieldEditabili
				inviaIntegrazione(accreditamentoId);
				fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);
				workflowInCorso.setIntegrazioneEseguitaDaProvider(eseguitoDaUtente);
			} else if(stato == AccreditamentoStatoEnum.INS_ODG) {
				//Cancelliamo le Valutazioni non completate dei referee
				Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
				for(Valutazione v : valutazioni){
					if((v.getTipoValutazione() == ValutazioneTipoEnum.REFEREE) && v.getDataValutazione() == null){
						valutazioneService.delete(v);
					}
				}
			} else if(stato == AccreditamentoStatoEnum.CONCLUSO) {
				//Setto il flusso come concluso
				workflowInCorso.setStato(StatoWorkflowEnum.CONCLUSO);
			}
			accreditamento.setStatoVariazioneDati(stato);
			//non e' prevista la presa visione della segreteria
		} else if(workflowInCorso.getTipo() == TipoWorkflowEnum.DECADENZA) {
			if(stato == AccreditamentoStatoEnum.INS_ODG) {
				//messo in ODG per discussione commissione ECM
				gestioneChiusuraAccreditamento(accreditamento);
			}
			if(stato == AccreditamentoStatoEnum.CANCELLATO) {
				//Setto il flusso come concluso
				workflowInCorso.setStato(StatoWorkflowEnum.CONCLUSO);
				
				// ERM014776
				chiudiAccreditamentoEPulisciEventi(accreditamento);
			}
			//ATTENZIONE in setStato viene gestita anche la durata del procedimento
			//20180403 modificato per gestire la dataScadenza
			accreditamento.setStato(stato, workflowInCorso.getTipo());
		}

		//indipendentemente dal tipo di workflow se vado in uno stato di integrazione creo il relativo container
		if(stato == AccreditamentoStatoEnum.INTEGRAZIONE || stato == AccreditamentoStatoEnum.PREAVVISO_RIGETTO) {
			fieldIntegrazioneAccreditamentoService.createFieldIntegrazioneHistoryContainer(accreditamentoId, stato, workflowInCorso.getProcessInstanceId());
		}

		//salvo history
		if(workflowInCorso.getTipo() == TipoWorkflowEnum.VARIAZIONE_DATI)
			accreditamentoStatoHistoryService.createHistoryFine(accreditamento, workflowInCorso.getProcessInstanceId(), stato, accreditamento.getStatoVariazioneDati(), LocalDateTime.now(), presaVisione, missedTLValutazione);
		else
			accreditamentoStatoHistoryService.createHistoryFine(accreditamento, workflowInCorso.getProcessInstanceId(), stato, accreditamento.getStato(), LocalDateTime.now(), presaVisione, missedTLValutazione);


		//TODO se si chiama il servizio di protocollazione verrà settato uno stato intermedio di attesa protocollazione
		//TODO registrazione cronologia degli stati
		//accreditamentoRepository.save(accreditamento);
		saveAndAudit(accreditamento);

		//se lo stato è DINIEGO o ACCREDITATO storicizzo le valutazioni attive per la domanda
		if(stato == AccreditamentoStatoEnum.ACCREDITATO || stato == AccreditamentoStatoEnum.DINIEGO || stato == AccreditamentoStatoEnum.CONCLUSO) {
			Set<Valutazione> valutazioniAttive = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamentoId);
			for(Valutazione v : valutazioniAttive) {
				v.setStoricizzato(true);
				valutazioneService.save(v);
			}
		}

		alertEmailService.creaAlertForProvider(accreditamento, workflowInCorso);
	}

	private void sendEmailToResponsabili(Long accreditamentoId) throws Exception {
		Set<String> emailResponsabili = new HashSet<String>();
		for(Account a : accountService.getAllSegreteria()) {
			if(a.isResponsabileSegreteriaEcm())
				emailResponsabili.add(a.getEmail());
		}
		emailService.inviaNotificaFirmaResponsabileSegreteriaEcm(emailResponsabili, accreditamentoId);
	}

	//metodo che gestisce le procedure lasciate in sospeso da un accreditamento da concludere
	private void gestioneChiusuraAccreditamento(Accreditamento accreditamento) {
		//rimuove la valutazione commissione dell'accreditamento inserito in una seduta APERTA
		if(accreditamento.isInsOdg()) {
			valutazioneCommissioneRepository.deleteOneByAccreditamentoAndSedutaLockedFalse(accreditamento);
		}
		//cancellazione dei field integrazione ed editabili
		else if(accreditamento.isModificaDati() || accreditamento.isIntegrazione() || accreditamento.isPreavvisoRigetto()) {
			fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamento.getId());
//			fieldIntegrazioneAccreditamentoService.removeAllFieldIntegrazioneForAccreditamento(accreditamento.getId());
		}
		//in ogni caso chiudo le valutazioni esistenti come storicizzate e stato cancellato
		Set<Valutazione> valutazioniAttive = valutazioneService.getAllValutazioniForAccreditamentoIdAndNotStoricizzato(accreditamento.getId());
		for(Valutazione v : valutazioniAttive) {
			v.setStoricizzato(true);
			v.setDataValutazione(LocalDateTime.now());
			v.setAccreditamentoStatoValutazione(AccreditamentoStatoEnum.CANCELLATO);
			valutazioneService.save(v);
		}
	}

	@Override
	public void prendiInCarico(Long accreditamentoId, CurrentUser currentUser) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Accreditamento: " + accreditamentoId + " preso in carico dall'utente: " + currentUser.getUsername()));

		//semaforo Bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Account segretarioEcm = currentUser.getAccount();
		Valutazione valutazione = new Valutazione(segretarioEcm, accreditamento, ValutazioneTipoEnum.SEGRETERIA_ECM);

		//gestione diff
		if(accreditamento.isStandard()) {
			//prendo l'ultimo diff dell'accreditamento
			AccreditamentoDiff diffOld = diffService.findLastDiffByProviderId(accreditamento.getProvider().getId());
			if(diffOld == null)
				throw new Exception("Ultimo diff per il provider " + accreditamento.getProvider().getId() + " non trovato!");
			//creo il diff della domanda che sto prendendo in carico
			AccreditamentoDiff diffNew = diffService.creaAllDiffAccreditamento(accreditamento);
			//crea le valutazioni a seconda del confronto tra i due diff
			Set<FieldValutazioneAccreditamento> valutazioniDiff = diffService.confrontaDiffAccreditamento(diffOld, diffNew);
			//crea le valutazioni di default
			Set<FieldValutazioneAccreditamento> valutazioniDefault = fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento);
			//gestione intersezione default e diff
			valutazione.setValutazioni(handleValutazioniDefaultDiff(valutazioniDiff, valutazioniDefault));
		}
		//accreditamento provvisorio
		else {
			//setta i campi valutati positivamente di default
			valutazione.setValutazioni(fieldValutazioneAccreditamentoService.getValutazioniDefault(accreditamento));
		}

		valutazioneService.save(valutazione);

		//flusso Bonita
		workflowService.prendiTaskInCarica(currentUser, accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	private Set<FieldValutazioneAccreditamento> handleValutazioniDefaultDiff(Set<FieldValutazioneAccreditamento> valutazioniDiff, Set<FieldValutazioneAccreditamento> valutazioniDefault) {
		LOGGER.debug(Utils.getLogMessage("Gestione delle valutazioni di default e diff"));

		//N.B. tutti i field valutazione sono già presenti su db

		//rimuove dalle default quelle gestite in diff che andranno a sostituire (rimuove anche da db)
		for(FieldValutazioneAccreditamento fva : valutazioniDiff) {
			Iterator<FieldValutazioneAccreditamento> iterDefault = valutazioniDefault.iterator();
			while(iterDefault.hasNext()) {
				FieldValutazioneAccreditamento fvaDefault = iterDefault.next();
				if(fva.getIdField() == fvaDefault.getIdField()
						&& fva.getObjectReference() == fvaDefault.getObjectReference()) {
					iterDefault.remove();
					fieldValutazioneAccreditamentoService.delete(fvaDefault.getId());
				}
			}
		}

		valutazioniDefault.addAll(valutazioniDiff);
		return valutazioniDefault;
	}

	public boolean canUserInviaAValutazioneCommissione(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isInsOdg()){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
		}
		return false;
	};

	@Override
	@Transactional
	public void inserisciInValutazioneCommissioneForSystemUser(Long accreditamentoId) throws Exception{
		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		workflowService.eseguiTaskInsOdgForSystemUser(getAccreditamento(accreditamentoId));

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public boolean canUserInserisciValutazioneCommissione(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isValutazioneCommissione()){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
		}
		return false;
	}

	@Override
	public void inviaValutazioneCommissione(Seduta seduta, Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception{
		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		workflowService.eseguiTaskInserimentoEsitoOdgForCurrentUser(getAccreditamento(accreditamentoId), stato);
		if(!getAccreditamento(accreditamentoId).isVariazioneDati())
			settaStatusProviderAndDateAccreditamentoAndQuotaAnnuale(seduta.getData(), accreditamentoId, stato);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public void settaStatusProviderAndDateAccreditamentoAndQuotaAnnuale(LocalDate dataSeduta, Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception{
		Provider provider = providerService.getProvider(getProviderIdForAccreditamento(accreditamentoId));
		if(stato == AccreditamentoStatoEnum.ACCREDITATO){
			Accreditamento accreditamento = getAccreditamento(accreditamentoId);
			if(accreditamento.isProvvisorio()) {
				provider.setStatus(ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE);
				accreditamento.setDataFineAccreditamento(dataSeduta.plusYears(2));
				accreditamento.setDataInizioAccreditamento(LocalDate.now());
			} else {
				provider.setStatus(ProviderStatoEnum.ACCREDITATO_STANDARD);
				if(dataSeduta != null)
					accreditamento.setDataFineAccreditamento(dataSeduta.plusYears(4));
				else
					accreditamento.setDataFineAccreditamento(LocalDate.now().plusYears(4));
				accreditamento.setDataInizioAccreditamento(LocalDate.now());
			}
			save(accreditamento);
		}
		if(stato == AccreditamentoStatoEnum.DINIEGO) {
			provider.setStatus(ProviderStatoEnum.DINIEGO);
			//se la domanda diniegata è standard setta il necessario per ripresentare una nuova domanda provvisoria
			Set<Accreditamento> accreditamentiProvvisori = getAllAccreditamentiForProvider(provider.getId(), AccreditamentoTipoEnum.PROVVISORIO);
			//controllo se c'è un accreditamento provvisorio attivo e se la sua data di scadenza è > a 6 mesi da oggi
			for(Accreditamento a : accreditamentiProvvisori) {
				if(a.isDomandaAttiva() && a.getDataFineAccreditamento().isAfter(LocalDate.now().plusMonths(6))) {
					//in questo caso la data di inserimento nuovo provvisorio  = data fine accreditamento della domanda attiva
					provider.setCanInsertAccreditamentoProvvisorio(true);
					provider.setDataRinnovoInsertAccreditamentoProvvisorio(a.getDataFineAccreditamento());
				}
				else {
					//altrimenti la data per il nuovo accreditamento provvisorio è fra 6 mesi da oggi
					provider.setCanInsertAccreditamentoProvvisorio(true);
					provider.setDataRinnovoInsertAccreditamentoProvvisorio(LocalDate.now().plusMonths(6));
				}
			}
		}
		providerService.save(provider);
		if(stato == AccreditamentoStatoEnum.ACCREDITATO)
			quotaAnnualeService.createPagamentoProviderPerQuotaAnnuale(provider.getId(), LocalDate.now().getYear(), true);
	}

	@Override
	public void rivaluta(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Rivaluta Domanda : " + accreditamentoId));
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		valutazione.setDataValutazione(null);
		valutazioneService.save(valutazione);
	}

	@Override
	public void saveFileNoteOsservazioni(Long fileId, Long accreditamentoId) {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		File file = fileService.getFile(fileId);
		if (accreditamento.getStato().equals(AccreditamentoStatoEnum.INTEGRAZIONE))
			accreditamento.setNoteOsservazioniIntegrazione(file);
		else
			accreditamento.setNoteOsservazioniPreavvisoRigetto(file);
		accreditamentoRepository.save(accreditamento);
		//saveAndAudit(accreditamento);

	}

	@Override
	public Set<Accreditamento> getAllDomandeNonValutateByRefereeId(Long refereeId) {
		LOGGER.debug(Utils.getLogMessage("Ricerco tutte le utime domande non valutate consecutivamente dal referee id: " + refereeId));
		return accountRepository.getAllDomandeNonValutateByRefereeId(refereeId);
	}

	@Override
	@Transactional
	public void inviaValutazioneSulCampoStandard(Long accreditamentoId, String valutazioneComplessiva, File verbalePdf, AccreditamentoStatoEnum destinazioneStatoDomandaStandard, File allegato1, File allegato2, File allegato3) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Salvataggio verbale valutazione sul campo della domanda di Accreditamento " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());

		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamento.setVerbaleValutazioneSulCampoPdf(verbalePdf);

		//allegati opzionali
		if(allegato1 != null && !allegato1.isNew())
			accreditamento.setValutazioneSulCampoAllegato1(allegato1);
		if(allegato2 != null && !allegato2.isNew())
			accreditamento.setValutazioneSulCampoAllegato2(allegato2);
		if(allegato3 != null && !allegato3.isNew())
			accreditamento.setValutazioneSulCampoAllegato3(allegato3);

		saveAndAudit(accreditamento);

		workflowService.eseguiTaskValutazioneSulCampoForCurrentUser(accreditamento, accreditamento.getVerbaleValutazioneSulCampo().getTeamLeader().getUsernameWorkflow(), destinazioneStatoDomandaStandard);

		if(destinazioneStatoDomandaStandard == AccreditamentoStatoEnum.ACCREDITATO)
			settaStatusProviderAndDateAccreditamentoAndQuotaAnnuale(accreditamento.getVerbaleValutazioneSulCampo().getGiorno(), accreditamentoId, destinazioneStatoDomandaStandard);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	//inserisce il sottoscrivente del verbale sul campo
	//TODO se si deve mandare un email di aggiornamento del verbale sul campo questo sarebbe il punto :')
	@Override
	public void editScheduleVerbaleValutazioneSulCampo(Accreditamento accreditamento, VerbaleValutazioneSulCampo verbaleNew) {
		VerbaleValutazioneSulCampo verbaleToUpdate = accreditamento.getVerbaleValutazioneSulCampo();
		verbaleToUpdate.setGiorno(verbaleNew.getGiorno());
		verbaleToUpdate.setOra(verbaleNew.getOra());
		verbaleToUpdate.setTeamLeader(verbaleNew.getTeamLeader());
		verbaleToUpdate.setComponentiSegreteria(verbaleNew.getComponentiSegreteria());
		verbaleToUpdate.setOsservatoreRegionale(verbaleNew.getOsservatoreRegionale());
		verbaleToUpdate.setReferenteInformatico(verbaleNew.getReferenteInformatico());
		verbaleToUpdate.setSede(verbaleNew.getSede());
		verbaleToUpdate.setIsPresenteLegaleRappresentante(verbaleNew.getIsPresenteLegaleRappresentante());
		verbaleToUpdate.setDelegato(verbaleNew.getDelegato());
		verbaleToUpdate.setCartaIdentita(verbaleNew.getCartaIdentita());
		accreditamento.setVerbaleValutazioneSulCampo(verbaleToUpdate);
		save(accreditamento);
	}

	//modifica le info base del verbale sul campo
	@Override
	public void saveSottoscriventeVerbaleValutazioneSulCampo(Accreditamento accreditamento, VerbaleValutazioneSulCampo verbaleNew) {
		VerbaleValutazioneSulCampo verbaleToUpdate = accreditamento.getVerbaleValutazioneSulCampo();
		verbaleToUpdate.setCartaIdentita(verbaleNew.getCartaIdentita());
		verbaleToUpdate.setDelegato(verbaleNew.getDelegato());
		verbaleToUpdate.setIsPresenteLegaleRappresentante(verbaleNew.getIsPresenteLegaleRappresentante());
		accreditamento.setVerbaleValutazioneSulCampo(verbaleToUpdate);
		save(accreditamento);
	}

	@Override
	public void inviaEmailConvocazioneValutazioneSulCampo(Long accreditamentoId) throws Exception {
		LOGGER.info("Invio email per la Convocazione della Valutazione Sul Campo per accreditamento: " + accreditamentoId);
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		VerbaleValutazioneSulCampo verbale = accreditamento.getVerbaleValutazioneSulCampo();
		Set<String> dst = new HashSet<String>();

		dst.add(verbale.getTeamLeader().getEmail());
		dst.add(verbale.getOsservatoreRegionale().getEmail());
		for(Account a : verbale.getComponentiSegreteria())
			dst.add(a.getEmail());
		if(verbale.getReferenteInformatico() != null)
			dst.add(verbale.getReferenteInformatico().getEmail());

		emailService.inviaConvocazioneValutazioneSulCampo(dst, verbale.getDataoraVisita(), accreditamento.getProvider().getDenominazioneLegale());
	}

	@Override
	public boolean canUserAbilitaVariazioneDati(Long accreditamentoId, CurrentUser currentUser) {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria()
				&& accreditamento.isAccreditato()
				&& (accreditamento.getStatoVariazioneDati() == null
				|| accreditamento.getStatoVariazioneDati() == AccreditamentoStatoEnum.CONCLUSO
				|| accreditamento.getStatoVariazioneDati() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE)
				&& isNotVariazioneDatiPresaInCaricoDaAltri(accreditamento, currentUser.getAccount()))
			return true;
		else return false;
	}

	//funzione che controlla se la variazione dati può essere presa in carico
	private boolean isNotVariazioneDatiPresaInCaricoDaAltri(Accreditamento accreditamento, Account user) {
		Valutazione valutazioneSegreteriaVariazioneDati = valutazioneService.getValutazioneSegreteriaForAccreditamentoIdNotStoricizzato(accreditamento.getId());
		if(valutazioneSegreteriaVariazioneDati == null || valutazioneSegreteriaVariazioneDati.getAccount().equals(user))
			return true;
		else
			return false;
	}

	@Override
	public void avviaFlussoVariazioneDati(Accreditamento accreditamento) throws Exception {
		LOGGER.info("Avvio del flusso di Variazione dei Dati dell'Accreditamento: " + accreditamento.getId());
		Long accreditamentoId = accreditamento.getId();

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account segreteria = Utils.getAuthenticatedUser().getAccount();
		workflowService.createWorkflowAccreditamentoVariazioneDati(Utils.getAuthenticatedUser(), accreditamento);

		//si crea già anche la valutazione per la segreteria che sancisce la presa in carico
		Valutazione valutazioneSegreteria = new Valutazione(segreteria, accreditamento, ValutazioneTipoEnum.SEGRETERIA_ECM);
		valutazioneSegreteria.setAccreditamentoStatoValutazione(null);
		valutazioneSegreteria.setStoricizzato(false);
		//setta tutti gli esiti a true e bloccati
		valutazioneSegreteria.setValutazioni(fieldValutazioneAccreditamentoService.createAllFieldValutazioneAndSetEsitoAndEnabled(true, false, valutazioneSegreteria.getAccreditamento()));

		valutazioneService.save(valutazioneSegreteria);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public void inviaCampiSbloccatiVariazioneDati(Long accreditamentoId) throws Exception {
		LOGGER.info("Invio dei campi sbloccati in Variazione dei Dati dell'Accreditamento: " + accreditamentoId);

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		//i giorni per modificare la domanda sono 10
		int giorniPerModifica = ecmProperties.getGiorniVariazioneDatiAccreditamento();
		Long timerIntegrazioneRigetto = giorniPerModifica * millisecondiInGiorno;

		workflowService.eseguiTaskRichiestaIntegrazioneForCurrentUser(accreditamento, timerIntegrazioneRigetto);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public boolean canUserInviaCampiVariazioneDati(Long accreditamentoId, CurrentUser currentUser) {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.getStatoVariazioneDati() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE)
			return true;
		else return false;
	}

	/* la segreteria valuta l'integrazione e, se la destinazione della valutazione è INTEGRAZIONE, assegna la domanda ad un referee,
	 * creandogli una valutazione tutta valutata positivamente e sbloccandogli i campi di cui valutare l'integrazione del provider */
	@Override
	public void inviaValutazioneSegreteriaVariazioneDati(Long accreditamentoId, String valutazioneComplessiva, AccreditamentoStatoEnum destinazioneVariazioneDati, Account refereeVariazioneDati) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio valutazione segreteria della variazione dati per l'accreditamento: " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account user = Utils.getAuthenticatedUser().getAccount();
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, user.getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		approvaIntegrazione(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());

		if(destinazioneVariazioneDati == AccreditamentoStatoEnum.CONCLUSO) {
			//elimino le vecchie valutazioni
			Set<Valutazione> valutazioniVariazioneDati = valutazioneService.getAllValutazioniCompleteForAccreditamentoIdAndNotStoricizzato(accreditamento.getId());
			for(Valutazione v : valutazioniVariazioneDati) {
				valutazioneService.delete(v);
			}
			workflowService.eseguiTaskValutazioneVariazioneDatiForCurrentUser(accreditamento, null, null, destinazioneVariazioneDati);
		}
		else if(destinazioneVariazioneDati == AccreditamentoStatoEnum.INS_ODG) {
			//elimino le vecchie valutazioni
			Set<Valutazione> valutazioniVariazioneDati = valutazioneService.getAllValutazioniCompleteForAccreditamentoIdAndNotStoricizzato(accreditamento.getId());
			for(Valutazione v : valutazioniVariazioneDati) {
				valutazioneService.delete(v);
			}
			workflowService.eseguiTaskValutazioneVariazioneDatiForCurrentUser(accreditamento, null, null, destinazioneVariazioneDati);
		}
		else {
			//viene messo in una lista per sfruttare lo stesso metodo
			List<String> valutatore = new ArrayList<String>();
			valutatore.add(refereeVariazioneDati.getUsername());
			//creo la valutazione per il referee
			Valutazione valutazioneReferee = new Valutazione(refereeVariazioneDati, accreditamento, ValutazioneTipoEnum.REFEREE);
			//metodo per la gestione delle valutazioni
			valutazioneService.initializeFieldValutazioni(valutazioneReferee, accreditamento);
			valutazioneService.save(valutazioneReferee);
			//emailService.inviaNotificaAReferee(refereeVariazioneDati.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
			workflowService.eseguiTaskValutazioneVariazioneDatiForCurrentUser(accreditamento, valutatore, 1, destinazioneVariazioneDati);
		}

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public void inviaValutazioneCrecmVariazioneDati(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio valutazione CRECM della variazione dati per l'accreditamento: " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		Account user = Utils.getAuthenticatedUser().getAccount();
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, user.getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		bloccaAndStoricizzaValutazione(valutazione, valutazioneComplessiva, accreditamento.getStato());

		workflowService.eseguiTaskValutazioneCrecmForCurrentUser(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Deprecated
	@Override
	public void inviaValutazioneVariazioneDati(Long accreditamentoId, String valutazioneComplessiva, AccreditamentoStatoEnum destinazioneVariazioneDati, Account refereeVariazioneDati) throws Exception {
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Account user = Utils.getAuthenticatedUser().getAccount();
		List<FieldValutazioneAccreditamento> campiDaValutare = new ArrayList<FieldValutazioneAccreditamento>();

		//se l'utente è segreteria mi salvo tutti gli idField relativi ai fieldIntegrazione prima che questi
		//vengano cancellati e poi approvo l'integrazione
		if(user.isSegreteria()) {
			campiDaValutare = getFieldValutazioneDaValutare(accreditamentoId);
			//applica modifiche
			approvaIntegrazione(accreditamentoId);
		}

		//disabilito tutti i filedValutazioneAccreditamento
		for (FieldValutazioneAccreditamento fva : valutazione.getValutazioni()) {
			fva.setEnabled(false);
		}

		//setta la data
		valutazione.setDataValutazione(LocalDateTime.now());

		//inserisce il commento complessivo
		valutazione.setValutazioneComplessiva(valutazioneComplessiva);

		valutazione.setAccreditamentoStatoValutazione(null);

		valutazioneService.saveAndFlush(valutazione);

		//detacha e copia: da questo momento valutazione si riferisce alla copia storicizzata
		valutazioneService.copiaInStorico(valutazione);

		if(user.isSegreteria()) {

			if(destinazioneVariazioneDati == AccreditamentoStatoEnum.CONCLUSO) {
				workflowService.eseguiTaskValutazioneVariazioneDatiForCurrentUser(accreditamento, null, null, destinazioneVariazioneDati);
				//elimino le vecchie valutazioni
				Set<Valutazione> valutazioniVariazioneDati = valutazioneService.getAllValutazioniCompleteForAccreditamentoIdAndNotStoricizzato(accreditamento.getId());
				for(Valutazione v : valutazioniVariazioneDati) {
					valutazioneService.delete(v);
				}
			}
			else {
				List<String> valutatore = new ArrayList<String>();
				valutatore.add(refereeVariazioneDati.getUsername());
				workflowService.eseguiTaskValutazioneVariazioneDatiForCurrentUser(accreditamento, valutatore, 1, destinazioneVariazioneDati);

				//creo la valutazione per il referee
				Valutazione valutazioneReferee = new Valutazione(refereeVariazioneDati, accreditamento, ValutazioneTipoEnum.REFEREE);
				valutazioneReferee.setAccreditamentoStatoValutazione(null);
				valutazioneReferee.setStoricizzato(false);
				//setta tutti gli esiti a true e bloccati
				valutazioneReferee.setValutazioni(fieldValutazioneAccreditamentoService.createAllFieldValutazioneAndSetEsitoAndEnabled(true, false, accreditamento));
				//sblocca e setta a null l'esito dei campi che dovrà valutare
				valutazioneService.resetEsitoAndEnabledForSubset(valutazioneReferee, campiDaValutare);

				valutazioneService.save(valutazioneReferee);
				emailService.inviaNotificaAReferee(refereeVariazioneDati.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
			}
		}
		else {
			workflowService.eseguiTaskValutazioneCrecmForCurrentUser(accreditamento);
		}
	}

	private List<FieldValutazioneAccreditamento> getFieldValutazioneDaValutare(Long accreditamentoId) {
		List<FieldValutazioneAccreditamento> campiDaValutare = new ArrayList<FieldValutazioneAccreditamento>();
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();
		AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();

		Set<FieldIntegrazioneAccreditamento> fieldIntegrazione = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamentoByContainer(accreditamentoId, stato, workFlowProcessInstanceId);
		Map<IdFieldEnum, Boolean> idGruppi = new HashMap<IdFieldEnum, Boolean>();
		for(FieldIntegrazioneAccreditamento fia : fieldIntegrazione) {
			FieldValutazioneAccreditamento fieldValutazione = new FieldValutazioneAccreditamento();
			//gestione adhoc per idField con gruppo
			//verranno poi aggiunti alla fine
			if(fia.getIdField().getSubSetField() == SubSetFieldEnum.DATI_ACCREDITAMENTO) {
				Boolean result = null;
				switch(fia.getIdField()) {
				case DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO:
				case DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE:
				case DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE:
					result = idGruppi.get(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO);
					//non ancora aggiunto, lo aggiungo oppure se result è false lo aggiorno con true
					if(result == null || result == false) {
						idGruppi.put(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO, fia.isModificato());
					}
					break;
				case DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO:
				case DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE:
				case DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE:
					result = idGruppi.get(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE);
					//non ancora aggiunto, lo aggiungo oppure se result è false lo aggiorno con true
					if(result == null || result == false) {
						idGruppi.put(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE, fia.isModificato());
					}
					break;
				case DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO:
				case DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO:
					result = idGruppi.get(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI);
					//non ancora aggiunto, lo aggiungo oppure se result è false lo aggiorno con true
					if(result == null || result == false) {
						idGruppi.put(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI, fia.isModificato());
					}
					break;
				default:
					fieldValutazione.setIdField(fia.getIdField());
					fieldValutazione.setObjectReference(fia.getObjectReference());
					fieldValutazione.setModificatoInIntegrazione(fia.isModificato());
					campiDaValutare.add(fieldValutazione);
					break;
				}
			}
			else {
				fieldValutazione.setIdField(fia.getIdField());
				fieldValutazione.setObjectReference(fia.getObjectReference());
				fieldValutazione.setModificatoInIntegrazione(fia.isModificato());
				campiDaValutare.add(fieldValutazione);
			}
		}
		//aggiungo tutti i field valutazione adHoc dei gruppi
		idGruppi.forEach((key, value) -> {
			FieldValutazioneAccreditamento fieldValutazione = new FieldValutazioneAccreditamento();
			fieldValutazione.setIdField(key);
			//non hanno mai obj reference
			fieldValutazione.setObjectReference(-1);
			fieldValutazione.setModificatoInIntegrazione(value);
			campiDaValutare.add(fieldValutazione);
		});

		return campiDaValutare;
	}

	//interrompe il flusso di accreditamento e avvia la procedura di conclusione
	@Override
	public void conclusioneProcedimento(Accreditamento accreditamento, CurrentUser currentUser) throws Exception {
		Long accreditamentoId = accreditamento.getId();
		LOGGER.debug(Utils.getLogMessage("Avvio della procedura di conclusione dell'accreditamento: " + accreditamentoId));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		//chiamata a Bonita che annulla il vecchio flusso e apre il nuovo
		workflowService.createWorkflowAccreditamentoConclusioneProcedimento(currentUser, accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public Accreditamento getLastAccreditamentoForProviderId(Long providerId) {
		LOGGER.info("Cerco l'ultimo accreditamento del Provider: " + providerId);
		return accreditamentoRepository.findFirstByProviderIdOrderByDataFineAccreditamentoDesc(providerId);
	}

	/* Metodo che applica l'integrazione (ma non salva le modifiche) in modo in cui sia possibile fare un controllo sullo stato del DB
	 * a fine della valutazione della segreteria (ovvero subito prima di applicare le modifiche)
	 * Restituisce un array di String dove il primo elemento riguarda gli errori sul comitato e il secondo sulle sedi
	 */
	@Override
	public String[] controllaValidazioneIntegrazione(Long accreditamentoId) throws Exception {
		String erroreMsgComitato = null;
		String erroreMsgSedi = null;

		Provider provider = providerService.getProvider(getProviderIdForAccreditamento(accreditamentoId));
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Long workFlowProcessInstanceId = accreditamento.getWorkflowInCorso().getProcessInstanceId();
		//NB sono in valutazione
		AccreditamentoStatoEnum stato = accreditamento.getStatoUltimaIntegrazione();
		/* Verifichiamo che non vengano approvate modifiche al comitato scientifico che violino i vincoli della domanda */
		Set<Persona> componentiComitato = provider.getComponentiComitatoScientifico();
		/* e verifichiamo che non vengano approvate modifiche alle sedi che violino i vincoli della domanda */
		Set<Sede> sedi = provider.getSedi();
		Set<FieldIntegrazioneAccreditamento> fieldIntegrazione = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneApprovedBySegreteria(accreditamentoId, stato, workFlowProcessInstanceId);
		if(fieldIntegrazione == null || fieldIntegrazione.isEmpty()) {
			erroreMsgComitato = null;
			erroreMsgSedi = null;
		}
		else {
			Set<FieldIntegrazioneAccreditamento> fieldComponente = null;
			Iterator<Persona> personaIter = componentiComitato.iterator();
			while(personaIter.hasNext()) {
				Persona p = personaIter.next();
				fieldComponente = Utils.getSubset(fieldIntegrazione, p.getId(), SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO);
				if(fieldComponente != null && !fieldComponente.isEmpty()) {
					integrazioneService.applyIntegrazioneObject(p, fieldComponente);
					fieldIntegrazione.removeAll(fieldComponente);
				}
				fieldComponente = Utils.getSubset(fieldIntegrazione, p.getId(), SubSetFieldEnum.FULL);
				if(fieldComponente != null && !fieldComponente.isEmpty()) {
					if(fieldComponente.iterator().next().getTipoIntegrazioneEnum() == TipoIntegrazioneEnum.ELIMINAZIONE) {
						personaIter.remove();
						fieldIntegrazione.removeAll(fieldComponente);
					}
				}
			}
			IdFieldEnum comitatoFull = Utils.getFullFromRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
			for(FieldIntegrazioneAccreditamento fia : fieldIntegrazione) {
				if(fia.getIdField() == comitatoFull) {
					componentiComitato.add(personaService.getPersona(fia.getObjectReference()));
				}
			}
			erroreMsgComitato = providerService.controllaComitato(componentiComitato, true);

			Set<FieldIntegrazioneAccreditamento> fieldSede = null;
			Iterator<Sede> sedeIter = sedi.iterator();
			while(sedeIter.hasNext()) {
				Sede s = sedeIter.next();
				fieldSede = Utils.getSubset(fieldIntegrazione, s.getId(), SubSetFieldEnum.SEDE);
				if(fieldSede != null && !fieldSede.isEmpty()) {
					integrazioneService.applyIntegrazioneObject(s, fieldSede);
					fieldIntegrazione.removeAll(fieldSede);
				}
				fieldSede = Utils.getSubset(fieldIntegrazione, s.getId(), SubSetFieldEnum.FULL);
				if(fieldSede != null && !fieldSede.isEmpty()) {
					if(fieldSede.iterator().next().getTipoIntegrazioneEnum() == TipoIntegrazioneEnum.ELIMINAZIONE) {
						sedeIter.remove();
						fieldIntegrazione.removeAll(fieldSede);
					}
				}
			}
			IdFieldEnum sedeFull = IdFieldEnum.SEDE__FULL;
			for(FieldIntegrazioneAccreditamento fia : fieldIntegrazione) {
				if(fia.getIdField() == sedeFull) {
					sedi.add(sedeService.getSede(fia.getObjectReference()));
				}
			}
			erroreMsgSedi = providerService.controllaSedi(sedi);

		}
		return new String[] {erroreMsgComitato, erroreMsgSedi};
	}

	@Override
	public void generaDecretoDecadenza(ByteArrayOutputStream byteArrayOutputStreamAccreditata, Long providerId, ImpostazioniProviderWrapper wrapper) throws Exception {
		LOGGER.info("Genera PDF Decreto Decadenza per provider: " + providerId);
		Accreditamento accreditamento = null;
		accreditamento = getAccreditamentoAttivoForProvider(providerId);
		if(accreditamento == null){
			LOGGER.info("Nessun accreditamento attivo per provider: " + providerId);
			accreditamento = getLastAccreditamentoForProviderId(providerId);
			if(accreditamento == null){
				LOGGER.info("Nessun accreditamento trovato per provider: " + providerId);
				throw new java.lang.Exception("Impossibile generare PDF Decreto Decadenza per provider: " + providerId);
			}
		}

		PdfAccreditamentoProvvisorioDecretoDecadenzaInfo decadenzaInfo = new PdfAccreditamentoProvvisorioDecretoDecadenzaInfo(accreditamento, wrapper);
		pdfService.creaPdfAccreditamentoProvvisorioDecretoDecadenza(byteArrayOutputStreamAccreditata, decadenzaInfo);
	}

	//L'utente (segreteria) può riassegnare l'accreditamento ad un altro referee
	@Override
	public boolean canRiassegnaRefereeVariazioneDati(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.isSegreteria() && getAccreditamento(accreditamentoId).isAssegnamentoCrecmVariazioneDati())
			return true;
		return false;
	}

	@Override
	public boolean canUserAccreditatoInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isAccreditatoInAttesaDiFirma()){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void inviaAccreditamentoInAttesaDiFirma(Long accreditamentoId, Long fileIdLettera, Long fileIdDecreto, LocalDate dataDelibera, String numeroDelibera) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio Lettera Accompagnamento e Decreto Accreditamento della domanda " + accreditamentoId + " al Protocollo"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		File fileDecreto = fileService.getFile(fileIdDecreto);
		fileDecreto.setDataDelibera(dataDelibera);
		fileDecreto.setNumeroDelibera(numeroDelibera);
		fileService.save(fileDecreto);

		File fileLettera = fileService.getFile(fileIdLettera);

		//salvo nel file che viene protocollato l'operatore che effettua la richiesta
		fileLettera.setOperatoreProtocollo(Utils.getAuthenticatedUser().getAccount());
		fileService.save(fileLettera);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		accreditamento.setDecretoAccreditamento(fileDecreto);
		accreditamento.setLetteraAccompagnatoriaAccreditamento(fileLettera);
		saveAndAudit(accreditamento);
		workflowService.eseguiTaskFirmaAccreditamentoForCurrentUser(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public boolean canUserDiniegoInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && accreditamento.isDiniegoInAttesaDiFirma()){
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			if(!task.isAssigned())
				return true;
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void replaceValutazioneSulCampoFiles(Long accreditamentoId, Long pdfId, Long a1Id, Long a2Id, Long a3Id) {
		LOGGER.debug(Utils.getLogMessage("Cambio dei allegati della valutazione sul campo del accreditamento " + accreditamentoId));

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		boolean isSave = false;

		if(accreditamento.getVerbaleValutazioneSulCampoPdf() == null ||  //vecchio non ce
				!accreditamento.getVerbaleValutazioneSulCampoPdf().getId().equals(pdfId)) { // nuovo e diverso
			if(pdfId != null) { // nuovo non e vuoto
				LOGGER.debug(Utils.getLogMessage("Cambio pdf firmato della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setVerbaleValutazioneSulCampoPdf(fileService.getFile(pdfId));
				isSave = true;
			}
		}

		if(accreditamento.getValutazioneSulCampoAllegato1() == null || // vecchio non ce
			!accreditamento.getValutazioneSulCampoAllegato1().getId().equals(a1Id)) { // nuovo e diverso

			if(a1Id == null) {
				// riomozione
				LOGGER.debug(Utils.getLogMessage("Rimozione allegato1 della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setValutazioneSulCampoAllegato1(null);
			}else {
				// aggiornamento
				LOGGER.debug(Utils.getLogMessage("Cambio allegato1 della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setValutazioneSulCampoAllegato1(fileService.getFile(a1Id));
			}
			isSave = true;
		}

		if(accreditamento.getValutazioneSulCampoAllegato2() == null || // vecchio non ce
				!accreditamento.getValutazioneSulCampoAllegato2().getId().equals(a2Id)) { // nuovo e diverso

			if(a2Id == null) {
				// riomozione
				LOGGER.debug(Utils.getLogMessage("Rimozione allegato2 della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setValutazioneSulCampoAllegato2(null);
			}else {
				// aggiornamento
				LOGGER.debug(Utils.getLogMessage("Cambio allegato2 della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setValutazioneSulCampoAllegato2(fileService.getFile(a2Id));
			}
			isSave = true;
		}

		if(accreditamento.getValutazioneSulCampoAllegato3() == null || // vecchio non ce
				!accreditamento.getValutazioneSulCampoAllegato3().getId().equals(a3Id)) { // nuovo e diverso

			if(a3Id == null) {
				// riomozione
				LOGGER.debug(Utils.getLogMessage("Rimozione allegato3 della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setValutazioneSulCampoAllegato3(null);
			}else {
				// aggiornamento
				LOGGER.debug(Utils.getLogMessage("Cambio allegato3 della valutazione sul campo del accreditamento " + accreditamentoId));
				accreditamento.setValutazioneSulCampoAllegato3(fileService.getFile(a3Id));
			}
			isSave = true;
		}

		if(isSave) saveAndAudit(accreditamento);
	}

	@Override
	@Transactional
	public void inviaDiniegoInAttesaDiFirma(Long accreditamentoId, Long fileIdLettera, Long fileIdDecreto, LocalDate dataDelibera, String numeroDelibera) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio Lettera Accompagnamento e Decreto Diniego della domanda " + accreditamentoId + " al Protocollo"));

		//semaforo bonita
		tokenService.createBonitaSemaphore(accreditamentoId);

		File fileDecreto = fileService.getFile(fileIdDecreto);
		fileDecreto.setDataDelibera(dataDelibera);
		fileDecreto.setNumeroDelibera(numeroDelibera);
		fileService.save(fileDecreto);

		File fileLettera = fileService.getFile(fileIdLettera);

		//salvo nel file che viene protocollato l'operatore che effettua la richiesta
		fileLettera.setOperatoreProtocollo(Utils.getAuthenticatedUser().getAccount());
		fileService.save(fileLettera);

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		accreditamento.setDecretoDiniego(fileDecreto);
		accreditamento.setLetteraAccompagnatoriaDiniego(fileLettera);
		saveAndAudit(accreditamento);
		workflowService.eseguiTaskFirmaDiniegoForCurrentUser(accreditamento);

		//rilascio semaforo bonita
		tokenService.removeBonitaSemaphore(accreditamentoId);
	}

	@Override
	public void aggiungiDatiDelibera(Long idFileDelibera, String numeroDelibera, LocalDate dataDelibera) {
		LOGGER.debug(Utils.getLogMessage("Invio dati della delibera per il file protocollato: " + idFileDelibera));

		File fileProtocollato = fileService.getFile(idFileDelibera);
		fileProtocollato.setNumeroDelibera(numeroDelibera);
		fileProtocollato.setDataDelibera(dataDelibera);
		fileService.save(fileProtocollato);

	}

	@Override
	public Set<Accreditamento> getAllTipoStandart(CurrentUser currentUser) {
		return accreditamentoRepository.getAllDomandeTipoStandart(currentUser.getAccount().getId());
	}

	@Override
	public int countAllTipoStandart(CurrentUser currentUser) {
		return accreditamentoRepository.countAllDomandeTipoStandart(currentUser.getAccount().getId());
	}

	@Override
	public void chiudiAccreditamentoEPulisciEventi(Accreditamento acc) {
		// accreditamento deve essere salvato da chiamante
		acc.setDataChiusuraAcc(LocalDate.now());
		
		// ellimina tutti eventi in bozza
		eventoService.eliminaEventiPerChiusuraAccreditamento(acc, LocalDate.now());
	}


}
