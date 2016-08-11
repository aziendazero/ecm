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

	@Override
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
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser) {
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, currentUser.getAccount().getId());
		if(valutazione != null &&
				(valutazione.getDataValutazione() == null) &&
				(currentUser.hasProfile(ProfileEnum.SEGRETERIA) || currentUser.hasProfile(ProfileEnum.REFEREE)))
			return true;
		else return false;
	}
}
