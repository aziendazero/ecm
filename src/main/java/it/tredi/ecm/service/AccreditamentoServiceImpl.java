package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
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
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private AccountRepository accountRepository;

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
	@Transactional
	public void inviaDomandaAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Invio domanda di Accreditamento " + accreditamentoId + " alla segreteria"));

		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		if(accreditamento.getDataInvio() == null)
			accreditamento.setDataInvio(LocalDate.now());
		accreditamento.setDataScadenza(accreditamento.getDataInvio().plusDays(180));

		accreditamento.setStato(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO);
		accreditamento.getProvider().setStatus(ProviderStatoEnum.VALIDATO);
		accreditamentoRepository.save(accreditamento);

		fieldEditabileService.removeAllFieldEditabileForAccreditamento(accreditamentoId);

		//TODO AVVIO WORKFLOW DOMANDA
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
	public void inviaValutazioneDomanda(Long accreditamentoId, String valutazioneComplessiva, Set<Account> refereeGroup) {
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
		}

		//la segreteria crea le valutazioni per i referee
		if (user.isSegreteria()){
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
	public void assegnaStessoGruppoCrecm(Long accreditamentoId, String valutazioneComplessiva) {
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
		accreditamento.setStato(AccreditamentoStatoEnum.VALUTAZIONE_CRECM);
		accreditamentoRepository.save(accreditamento);
	}

	@Override
	@Transactional
	public void presaVisione(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Presa visione della conferma dei dati da parte del provider e cambiamento stato della domanda " + accreditamentoId + " in INS_ODG"));
		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		accreditamento.setDataInserimentoOdg(LocalDate.now());
		accreditamento.setStato(AccreditamentoStatoEnum.INS_ODG);
		accreditamentoRepository.save(accreditamento);
	}

	@Override
	@Transactional
	public void inviaIntegrazione(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Integrazione della domanda " + accreditamentoId + " inviata alla segreteria per essere valutata"));

		Accreditamento accreditamento = getAccreditamento(accreditamentoId);

		accreditamento.setStato(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA);
		accreditamentoRepository.save(accreditamento);

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
	public int countAllAccreditamentiByStato(AccreditamentoStatoEnum stato) {
		LOGGER.debug(Utils.getLogMessage("Conto delle domande di accreditamento " + stato));
		return accreditamentoRepository.countAllByStato(stato);
	}

	@Override
	public Set<Accreditamento> getAllAccreditamentiByStato(AccreditamentoStatoEnum stato) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle domande di accreditamento " + stato));
		return accreditamentoRepository.findAllByStato(stato);
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

	//metodo generico per filtrare gli accreditamenti in base allo stato e all'account id
	@Override
	public Set<Accreditamento> getAllAccreditamentiByStatoForAccountId(AccreditamentoStatoEnum stato, Long id) {
		LOGGER.debug(Utils.getLogMessage("Recuper le domande di accreditamento in stato " + stato + " assegnate all'account " + id));
		return accreditamentoRepository.findAllAccreditamentoInValutazioneAssignedToAccountId(id);
	}

	//metodo generico per contare gli accreditamenti in base allo stato e all'account id
	@Override
	public int countAllAccreditamentiByStatoForAccountId(AccreditamentoStatoEnum stato, Long id) {
		LOGGER.debug(Utils.getLogMessage("Numero delle domande di accreditamento " + stato + " assegnate all'account " + id));
		return accreditamentoRepository.countAllAccreditamentoInValutazioneAssignedToAccountId(id);
	}

	@Override
	/*
	 * L'utente segreteria può prendere in carica una domanda se:
	 * 	+ La domanda è in stato VALUTAZIONE_SEGRTERIA_ASSEGNAMENTO
	 *  + E NON esiste già una valutazione di tipo SEGRETERIA_ECM (significa che nessun altro l'ha già presa in carico)
	 */
	public boolean canUserPrendiInCarica(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.hasProfile(ProfileEnum.SEGRETERIA) && getAccreditamento(accreditamentoId).isValutazioneSegreteriaAssegnamento()) {
			Set<Valutazione> valutazioniAccreditamento = valutazioneService.getAllValutazioniForAccreditamentoId(accreditamentoId);
			for (Valutazione v : valutazioniAccreditamento) {
				if(v.getTipoValutazione() == ValutazioneTipoEnum.SEGRETERIA_ECM)
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
	 */
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser) {
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, currentUser.getAccount().getId());
		if(valutazione != null &&
				(valutazione.getDataValutazione() == null) &&
				(currentUser.hasProfile(ProfileEnum.SEGRETERIA) || currentUser.hasProfile(ProfileEnum.REFEREE)))
			return true;
		else return false;
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
	public boolean canPresaVisione(Long accreditamentoId, CurrentUser currentUser) {
		if(currentUser.isSegreteria() && getAccreditamento(accreditamentoId).isValutazioneSegreteria())
			return true;
		return false;
	}
	
	@Override
	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato) {
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.setStato(stato);
		accreditamentoRepository.save(accreditamento);
	}
}
