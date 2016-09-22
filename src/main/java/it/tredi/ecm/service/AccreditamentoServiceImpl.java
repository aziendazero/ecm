package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.bonita.api.model.TaskInstanceDataModel;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;

@Service
public class AccreditamentoServiceImpl implements AccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoServiceImpl.class);

	@Autowired private AccreditamentoRepository accreditamentoRepository;

	@Autowired private ProviderService providerService;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private AccountRepository accountRepository;
	@Autowired private EmailService emailService;

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;

	@Autowired private IntegrazioneService integrazioneService;
	@Autowired private WorkflowService workflowService;

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

	private Accreditamento getNewAccreditamento(Provider provider, AccreditamentoTipoEnum tipoDomanda) throws Exception{
		if(provider == null){
			throw new Exception("Provider non può essere NULL");
		}

		if(provider.isNew()){
			throw new Exception("Provider non registrato");
		}else{

			Set<Accreditamento> accreditamentiAttivi = getAccreditamentiAvviatiForProvider(provider.getId(), tipoDomanda);

			if(accreditamentiAttivi.isEmpty()){
				Accreditamento accreditamento = new Accreditamento(tipoDomanda);
				accreditamento.setProvider(provider);
				accreditamento.enableAllIdField();
				save(accreditamento);
				return accreditamento;
			}else{
				throw new Exception("E' già presente una domanda di accreditamento " + tipoDomanda + " per il provider " + provider.getId());
			}
		}
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
		return accreditamentoRepository.findByProviderIdAndTipoDomandaAndDataScadenzaAfter(providerId, tipoDomanda, LocalDate.now());
	}

	/**
	 * Restituisce l'unica domanda di accreditamento che ha una data di fine accreditamento "attiva" e che è in stato "APPROVATO"
	 * */
	@Override
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) throws AccreditamentoNotFoundException{
		LOGGER.debug(Utils.getLogMessage("Recupero eventuale accreditamento attivo per il provider: " + providerId));
		Accreditamento accreditamento = accreditamentoRepository.findOneByProviderIdAndStatoAndDataFineAccreditamentoAfter(providerId, AccreditamentoStatoEnum.ACCREDITATO, LocalDate.now());
		if(accreditamento != null)
			LOGGER.debug("Trovato accreditamento attivo: " + accreditamento.getId() + "  per il provider: " + providerId);
		else
			throw new AccreditamentoNotFoundException("Nessun Accreditamento attivo trovato per il provider " + providerId);
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
		accreditamentoRepository.save(accreditamento);
	}

	@Override
	public boolean canProviderCreateAccreditamento(Long providerId,AccreditamentoTipoEnum tipoTomanda) {
		boolean canProvider = true;

		//per le domande standard è innanzitutto necessario che la segreteria abiliti il provider
		if(tipoTomanda == AccreditamentoTipoEnum.STANDARD){
			if(!providerService.canInsertAccreditamentoStandard(providerId)){
				return false;
			}
		}

		//controllo che non ci siano procedimenti gia' attivi
		Set<Accreditamento> accreditamentoList = getAllAccreditamentiForProvider(providerId,tipoTomanda);
		for(Accreditamento accreditamento : accreditamentoList){
			if(accreditamento.isBozza()){
				LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> Presente domanda " + accreditamento.getId() + " in stato di " + accreditamento.getStato().name()));
				return false;
			}

			if(accreditamento.isProcedimentoAttivo()){
				LOGGER.debug(Utils.getLogMessage("Provider(" + providerId + ") - canProviderCreateAccreditamento: False -> Presente domanda " + accreditamento.getId() + " in stato di Procedimento Attivo"));
				return false;
			}
			//TODO gestire la distinzione tra domanda inviata ma ancora non accreditata e domanda accreditata
			//				if(accreditamento.isInviato())
			//					return false;
		}
		return canProvider;
	}

	@Override
	//TODO capire perchè con @Transactional non ha effetto il salvataggio del wokflowService
	public void inviaDomandaAccreditamento(Long accreditamentoId) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Invio domanda di Accreditamento " + accreditamentoId + " alla segreteria"));

		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		if(accreditamento.getDataInvio() == null)
			accreditamento.setDataInvio(LocalDate.now());
		accreditamento.setDataScadenza(accreditamento.getDataInvio().plusDays(180));

		//accreditamento.setStato(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO);
		accreditamento.getProvider().setStatus(ProviderStatoEnum.VALIDATO);

		fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);

		try{
			if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.PROVVISORIO)
				workflowService.createWorkflowAccreditamentoProvvisorio(Utils.getAuthenticatedUser(), accreditamento);
		}catch (Exception ex){
			LOGGER.debug(Utils.getLogMessage("Errore avvio Workflow Accreditamento per la domanda " + accreditamentoId));
			throw new Exception("Errore avvio Workflow Accreditamento per la domanda " + accreditamentoId);
		}
		
		accreditamentoRepository.save(accreditamento);
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
		accreditamentoRepository.save(accreditamento);

		fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);
		fieldEditabileService.insertFieldEditabileForAccreditamento(accreditamentoId, null, SubSetFieldEnum.FULL, new HashSet<IdFieldEnum>(Arrays.asList(IdFieldEnum.EVENTO_PIANO_FORMATIVO__FULL)));
	}

	@Override
	@Transactional
	public void inviaValutazioneDomanda(Long accreditamentoId, String valutazioneComplessiva, Set<Account> refereeGroup) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Assegnamento domanda di Accreditamento " + accreditamentoId + " ad un gruppo CRECM"));
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		Account user = Utils.getAuthenticatedUser().getAccount();

		//setta la data
		valutazione.setDataValutazione(LocalDate.now());

		//inserisce il commento complessivo
		valutazione.setValutazioneComplessiva(valutazioneComplessiva);

		valutazioneService.save(valutazione);

		//il referee azzera il suo contatore di valutazioni non date consecutivamente
		if (user.isReferee()) {
			user.setValutazioniNonDate(0);
			accountRepository.save(user);
			workflowService.eseguiTaskValutazioneCrecmForCurrentUser(accreditamento);
		}

		//la segreteria crea le valutazioni per i referee
		if (user.isSegreteria()){
			List<String> usernameWorkflowValutatoriCrecm = new ArrayList<String>();
			for (Account a : refereeGroup) {
				Valutazione valutazioneReferee = new Valutazione();
				valutazioneReferee.setAccount(a);
				valutazioneReferee.setAccreditamento(accreditamento);
				valutazioneReferee.setTipoValutazione(ValutazioneTipoEnum.REFEREE);
				valutazioneService.save(valutazioneReferee);
				emailService.inviaNotificaAReferee(a.getEmail(), accreditamento.getProvider().getDenominazioneLegale());
				usernameWorkflowValutatoriCrecm.add(a.getUsernameWorkflow());
			}

			accreditamento.setDataValutazioneCrecm(LocalDate.now());
			accreditamentoRepository.save(accreditamento);
			
			workflowService.eseguiTaskValutazioneAssegnazioneCrecmForCurrentUser(accreditamento, usernameWorkflowValutatoriCrecm);
		}
	}

	@Override
	@Transactional
	public void approvaIntegrazione(Long accreditamentoId) throws Exception{
		Set<FieldValutazioneAccreditamento> fieldValutazioni = fieldValutazioneAccreditamentoService.getAllFieldValutazioneForAccreditamento(accreditamentoId);
		Set<FieldIntegrazioneAccreditamento> fieldIntegrazione = fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamentoId);

		Set<FieldIntegrazioneAccreditamento> approved = new HashSet<FieldIntegrazioneAccreditamento>();

		fieldValutazioni.forEach(v -> {
			if(v.getEsito().booleanValue()){
				approved.add(Utils.getField(fieldIntegrazione, v.getIdField()));
			}
		});

		integrazioneService.applyIntegrazioneAccreditamentoAndSave(accreditamentoId, approved);
		fieldIntegrazioneAccreditamentoService.delete(fieldIntegrazione);
	}


	@Override
	@Transactional
	//ritorna il numero di referee che non hanno valutato
	public void riassegnaGruppoCrecm(Long accreditamentoId, Set<Account> refereeGroup) {
		LOGGER.debug(Utils.getLogMessage("RIassegnamento domanda di Accreditamento " + accreditamentoId + " ad un ALTRO gruppo CRECM"));
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		//elimino le valutazioni dei referee che non hanno confermato la valutazione e aumento il loro contatore
		Set<Account> valutatori = valutazioneService.getAllValutatoriForAccreditamentoId(accreditamentoId);
		for(Account a : valutatori) {
			if(a.isReferee()) {
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, a.getId());
				if(valutazione.getDataValutazione() == null) {
					//TODO send notifica/messaggio
					a.setValutazioniNonDate(a.getValutazioniNonDate() + 1);
					accountRepository.save(a);
					valutazioneService.delete(valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, a.getId()));
				}
			}
		}

		// crea le valutazioni per i nuovi referee
		for (Account a : refereeGroup) {
			Valutazione valutazioneReferee = new Valutazione();
			valutazioneReferee.setAccount(a);
			valutazioneReferee.setAccreditamento(accreditamento);
			valutazioneReferee.setTipoValutazione(ValutazioneTipoEnum.REFEREE);
			valutazioneService.save(valutazioneReferee);
		}

		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamento.setStato(AccreditamentoStatoEnum.VALUTAZIONE_CRECM);
		accreditamentoRepository.save(accreditamento);
	}

	@Override
	@Transactional
	public void assegnaStessoGruppoCrecm(Long accreditamentoId, String valutazioneComplessiva) throws Exception {
		LOGGER.debug(Utils.getLogMessage("RIassegnamento domanda di Accreditamento " + accreditamentoId + " allo STESSO gruppo CRECM"));
		Valutazione valutazioneSegreteria = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		//setta la data
		valutazioneSegreteria.setDataValutazione(LocalDate.now());

		//inserisce il commento complessivo
		valutazioneSegreteria.setValutazioneComplessiva(valutazioneComplessiva);

		//elimino le date delle vecchie valutazioni
		Set<Account> valutatori = valutazioneService.getAllValutatoriForAccreditamentoId(accreditamentoId);
		for(Account a : valutatori) {
			if(a.isReferee()) {
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, a.getId());
				valutazione.setDataValutazione(null);
				valutazioneService.save(valutazione);
			}
		}

		valutazioneService.save(valutazioneSegreteria);
		accreditamento.setDataValutazioneCrecm(LocalDate.now());
		accreditamentoRepository.save(accreditamento);
		
		workflowService.eseguiTaskValutazioneSegreteriaForCurrentUser(accreditamento, false);
	}

	@Override
	public void presaVisione(Long accreditamentoId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Presa visione della conferma dei dati da parte del provider e cambiamento stato della domanda " + accreditamentoId + " in INS_ODG"));
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		accreditamento.setDataInserimentoOdg(LocalDate.now());
		accreditamentoRepository.save(accreditamento);
		
		workflowService.eseguiTaskValutazioneSegreteriaForCurrentUser(accreditamento, true);
	}

	@Override
	@Transactional
	public void inviaRichiestaIntegrazione(Long accreditamentoId, Long giorniTimer) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Invio RIchiesta Integrazione della domanda " + accreditamentoId + " al Provider"));
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		
		Long timerIntegrazioneRigetto = giorniTimer * 86400;
		workflowService.eseguiTaskRichiestaIntegrazioneForCurrentUser(accreditamento, timerIntegrazioneRigetto);
	}

	@Override
	public void inviaIntegrazione(Long accreditamentoId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Integrazione della domanda " + accreditamentoId + " inviata alla segreteria per essere valutata"));

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		//controllo quali campi sono stati modificati e quali confermati
		integrazioneService.checkIfFieldIntegraizoniConfirmedForAccreditamento(accreditamentoId, fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(accreditamentoId));

		//per i campi modificati...elimino i field valutazione su tutte le valutazioni presenti
		Set<FieldIntegrazioneAccreditamento> fieldModificati = fieldIntegrazioneAccreditamentoService.getModifiedFieldIntegrazioneForAccreditamento(accreditamentoId);
		
		//se ci sono state delle modifiche ri-abilito la valutazione cancellando la data
		if(fieldModificati == null || fieldModificati.isEmpty()){
			//elimina data valutazione
			Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
			if(valutazione.getTipoValutazione() == ValutazioneTipoEnum.SEGRETERIA_ECM){
				valutazione.setDataValutazione(null);
				valutazioneService.save(valutazione);
			}
		}
		
		Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniForAccreditamentoId(accreditamentoId);
		FieldValutazioneAccreditamento field = null;
		for(Valutazione valutazione : valutazioni){
			Set<FieldValutazioneAccreditamento> fieldValutazioni = valutazione.getValutazioni();
			for(FieldIntegrazioneAccreditamento fieldIntegrazione : fieldModificati){
				if(fieldIntegrazione.isModificato()){
					LOGGER.debug(Utils.getLogMessage("Eliminazione valutazione per " + fieldIntegrazione.getIdField()));
					if(fieldIntegrazione.getObjectReference() != -1){
						//multi-istanza
						field = Utils.getField(fieldValutazioni, fieldIntegrazione.getObjectReference(), fieldIntegrazione.getIdField());
					}else{
						//non multi-istanza
						field = Utils.getField(fieldValutazioni, fieldIntegrazione.getIdField());
					}
					if(field != null){
						fieldValutazioni.remove(field);
						fieldValutazioneAccreditamentoService.delete(field.getId());
					}
				}
			}
			valutazioneService.save(valutazione);
		}
		
		workflowService.eseguiTaskIntegrazioneForCurrentUser(accreditamento);
	}

	@Override
	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(Long accreditamentoId) throws Exception{
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

	//recupera tutti gli accreditamenti a seconda dello stato e del tipo che sono state assegnate ad un certo id utente
	@Override
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomandaForAccountId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long id) {
		if (tipo != null) {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " assegnate all'id: " + id));
			return accreditamentoRepository.findAllByStatoAndTipoDomandaForAccountId(stato, tipo, id);
		}
		else {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " assegnate all'id: " + id));
			return accreditamentoRepository.findAllByStatoForAccountId(stato, id);
		}
	}

	//conta tutti gli accreditamenti a seconda dello stato e del tipo che sono state assegnate ad un certo id utente
	@Override
	public int countAllAccreditamentiByStatoAndTipoDomandaForAccountId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long id) {
		if (tipo != null) {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " di tipo " + tipo + " assegnate all'id: " + id));
			return accreditamentoRepository.countAllByStatoAndTipoDomandaForAccountId(stato, tipo, id);
		}
		else {
			LOGGER.debug(Utils.getLogMessage("Conteggio delle domande di accreditamento " + stato + " assegnate all'id: " + id));
			return accreditamentoRepository.countAllByStatoForAccountId(stato, id);
		}
	}

	//recupera tutte le domande di accreditamento in scadenza
	//controlla se la data è compresa tra la data di scadenza e 30 giorni alla data di scadenza
	@Override
	public Set<Accreditamento> getAllAccreditamentiInScadenza() {
		LocalDate oggi = LocalDate.now();
		LocalDate dateScadenza = LocalDate.now().plusDays(30);
		return accreditamentoRepository.findAllByDataScadenzaProssima(oggi, dateScadenza);
	}

	//conta tutte le domande di accreditamento in scadenza
	//controlla se oggi + 30 giorni supera la data di scadenza
	@Override
	public int countAllAccreditamentiInScadenza() {
		LocalDate oggi = LocalDate.now();
		LocalDate dateScadenza = LocalDate.now().plusDays(30);
		return accreditamentoRepository.countAllByDataScadenzaProssima(oggi, dateScadenza);
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
			Set<Valutazione> valutazioniAccreditamento = valutazioneService.getAllValutazioniForAccreditamentoId(accreditamentoId);
			for (Valutazione v : valutazioniAccreditamento) {
				if(v.getTipoValutazione() == ValutazioneTipoEnum.SEGRETERIA_ECM)
					return false;
			}
			
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(getAccreditamento(accreditamentoId));
			if(task == null){
				return false;
			}
			
			if(task.isAssigned())
				return false;
			
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
		
		if( ((accreditamento.isValutazioneSegreteriaAssegnamento() || accreditamento.isValutazioneSegreteria()) && currentUser.isSegreteria()) ||
			(accreditamento.isValutazioneCrecm() && currentUser.isReferee())){
			Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, currentUser.getAccount().getId());
			if(valutazione != null && valutazione.getDataValutazione() == null){
				TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
				if(task == null){
					return false;
				}
				if(accreditamento.isValutazioneSegreteria()){
					if(!task.isAssigned())
						return true;
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
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, currentUser.getAccount().getId());
		if(valutazione != null &&
				(valutazione.getDataValutazione() != null) &&
				(currentUser.isSegreteria() || currentUser.isReferee()))
			return true;
		else return false;
	}

	@Override
	/*
	 * L'utente (segreteria | commissioneECM) può visualizzare tutte le valutazioni inviate
	 */
	public boolean canUserValutaDomandaShowRiepilogo(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.isSegreteria() || currentUser.isCommissioneEcm()){
			Set<Valutazione> valutazioni = valutazioneService.getAllValutazioniCompleteForAccreditamentoId(accreditamentoId);
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
			Set<FieldIntegrazioneAccreditamento> fields = fieldIntegrazioneAccreditamentoService.getModifiedFieldIntegrazioneForAccreditamento(accreditamentoId);
			TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
			if(task == null){
				return false;
			}
			
			if(fields.isEmpty())
				return true;
		}
		return false;
	}

	@Override
	/*
	 * L'utente (segreteria) può abilitare i campi per eventuale modifica
	 */
	public boolean canUserEnableField(CurrentUser currentUser, Long accreditamentoId) throws Exception {
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		if(currentUser.isSegreteria() && (accreditamento.isRichiestaIntegrazione() || accreditamento.isRichiestaPreavvisoRigetto())){
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
	public boolean canUserInviaRichiestaIntegrazione(Long accreditamentoId, CurrentUser currentUser) {
		return canUserEnableField(currentUser);
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
				Long accountIdForProvider = providerService.getAccountIdForProvider(providerId);
				if(currentUser.getAccount().getId().equals(accountIdForProvider)){
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
	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato) {
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.setStato(stato);
		accreditamentoRepository.save(accreditamento);
	}
	
	@Override
	public void prendiInCarica(Long accreditamentoId, CurrentUser currentUser) throws Exception{
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
		workflowService.prendiTaskInCarica(currentUser, accreditamento);
		
		Valutazione valutazione = new Valutazione();

		//utente corrente che prende in carico
		Account segretarioEcm = currentUser.getAccount();
		valutazione.setAccount(segretarioEcm);

		//accreditamento
		valutazione.setAccreditamento(accreditamento);

		//tipo di valutatore
		valutazione.setTipoValutazione(ValutazioneTipoEnum.SEGRETERIA_ECM);

		valutazioneService.save(valutazione);
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
	public void inserisciInValutazioneCommissione(Long accreditamentoId, CurrentUser curentUser) throws Exception{
		workflowService.eseguiTaskInsOdgForCurrentUser(getAccreditamento(accreditamentoId));
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
	public void inviaValutazioneCommissione(Long accreditamentoId, CurrentUser curentUser, AccreditamentoStatoEnum stato) throws Exception{
		workflowService.eseguiTaskTaskInserimentoEsitoOdgForUser(curentUser, getAccreditamento(accreditamentoId), stato);
	}
	
	@Override
	public boolean canUserEnableField(CurrentUser currentUser) {
		return currentUser.isSegreteria();
	}
}
