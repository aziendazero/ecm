package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AccreditamentoServiceImpl implements AccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoServiceImpl.class);
	
	@Autowired private AccreditamentoRepository accreditamentoRepository;
	@Autowired private ProviderService providerService;
	@Autowired private EventoService eventoService;
	@Autowired private PianoFormativoService pianoFormativoService;
	
	@Override
	public Accreditamento getNewAccreditamentoForCurrentProvider() throws Exception{
		LOGGER.debug(Utils.getLogMessage("Creazione domanda di accreditamento per il provider corrente"));
		Provider currentProvider = providerService.getProvider();
		return getNewAccreditamento(currentProvider);
	}
	
	@Override
	public Accreditamento getNewAccreditamentoForProvider(Long providerId) throws Exception {
		LOGGER.debug(Utils.getLogMessage("Creazione domanda di accreditamento per il provider: " + providerId));
		Provider provider = providerService.getProvider(providerId);
		return getNewAccreditamento(provider);
	}
	
	private Accreditamento getNewAccreditamento(Provider provider) throws Exception{
		if(provider == null){
			throw new Exception("Provider non può essere NULL");
		}
		
		if(provider.isNew()){
			throw new Exception("Provider non registrato");
		}else{
			
			Set<Accreditamento> accreditamentiAttivi = getAccreditamentiAvviatiForProvider(provider.getId(), AccreditamentoTipoEnum.PROVVISORIO);
			
			if(accreditamentiAttivi.isEmpty()){
				Accreditamento accreditamento = new Accreditamento(AccreditamentoTipoEnum.PROVVISORIO);
				accreditamento.setProvider(provider);
				save(accreditamento);
				return accreditamento;
			}else{
				throw new Exception("E' già presente una domanda");
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
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero eventuale accreditamento attivo per il provider: " + providerId));
		Accreditamento accreditamento = accreditamentoRepository.findOneByProviderIdAndStatoAndDataFineAccreditamentoAfter(providerId, AccreditamentoStatoEnum.ACCREDITATO, LocalDate.now());
		if(accreditamento != null)
			LOGGER.debug("Trovato accreditamento attivo: " + accreditamento.getId() + "  per il provider: " + providerId);
		return accreditamento;
	}
	
	@Override
	@Transactional
	public void save(Accreditamento accreditamento) {
		LOGGER.debug("Salvataggio domanda di accreditamento " + accreditamento.getTipoDomanda() + " per il provider " + accreditamento.getProvider().getId());
		accreditamentoRepository.save(accreditamento);
	}
	
	@Override
	public boolean canProviderCreateAccreditamento(Long providerId) {
		boolean canProvider = true;
		
		Set<Accreditamento> accreditamentoList = getAllAccreditamentiForProvider(providerId);
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
//			if(accreditamento.isInviato())
//				return false;
		}
		
		return canProvider;
	}
	
	@Override
	public List<Integer> getIdEditabili(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero idEditabili per domanda " + accreditamentoId));
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		if(accreditamento != null)
			return accreditamento.getIdEditabili();
		return new ArrayList<Integer>();
	}
	
	@Override
	@Transactional
	public void removeIdEditabili(Long accreditamentoId, List<Integer> idEditabiliToRemove) {
		LOGGER.debug(Utils.getLogMessage("Rimozione idEditabili " +  idEditabiliToRemove + "dalla domanda : " + accreditamentoId));

		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.getIdEditabili().removeAll(idEditabiliToRemove);
		accreditamentoRepository.save(accreditamento);
	}
	
	@Override
	@Transactional
	public void addIdEditabili(Long accreditamentoId, List<Integer> idEditabiliToAdd) {
		LOGGER.debug(Utils.getLogMessage("Aggiunta idEditabili " +  idEditabiliToAdd + "alla domanda : " + accreditamentoId));
		
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.getIdEditabili().addAll(idEditabiliToAdd);
		accreditamentoRepository.save(accreditamento);
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
		accreditamento.getIdEditabili().clear();
		accreditamento.setEditabile(false);
		
		accreditamentoRepository.save(accreditamento);
		
		Set<Evento> eventiNelPianoFormativo = accreditamento.getPianoFormativo().getEventi();
		for(Evento e : eventiNelPianoFormativo)
			e.getIdEditabili().clear();
		
		accreditamento.getPianoFormativo().setEditabile(false);
		
		accreditamento.getProvider().setStatus(ProviderStatoEnum.VALIDATO);
	}
	
	@Override
	@Transactional
	public void inserisciPianoFormativo(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Inserimento piano formativo per la domanda di Accreditamento " + accreditamentoId));
		
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		PianoFormativo pianoFormativo = new PianoFormativo();
		pianoFormativo.setEditabile(true);
		pianoFormativo.setAnnoPianoFormativo(LocalDate.now().getYear());
		pianoFormativo.setProvider(accreditamento.getProvider());
		pianoFormativoService.save(pianoFormativo);
		accreditamento.setPianoFormativo(pianoFormativo);
		accreditamento.getIdEditabili().clear();
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
}
