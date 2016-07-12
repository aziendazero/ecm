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
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoEnum;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;

@Service
public class AccreditamentoServiceImpl implements AccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoServiceImpl.class);
	
	@Autowired private AccreditamentoRepository accreditamentoRepository;
	@Autowired private ProviderService providerService;
	@Autowired private EventoService eventoService;
	
	@Override
	public Accreditamento getNewAccreditamentoForCurrentProvider() throws Exception{
		Provider currentProvider = providerService.getProvider();
		return getNewAccreditamento(currentProvider);
	}
	
	@Override
	public Accreditamento getNewAccreditamentoForProvider(Long providerId) throws Exception {
		Provider provider = providerService.getProvider(providerId);
		return getNewAccreditamento(provider);
	}
	
	private Accreditamento getNewAccreditamento(Provider provider) throws Exception{
		if(provider == null){
			throw new Exception("Provider non puoò essere NULL");
		}
		
		if(provider.isNew()){
			throw new Exception("Provider non registrato");
		}else{
			
			Set<Accreditamento> accreditamentiAttivi = getAccreditamentiAvviatiForProvider(provider.getId(), AccreditamentoEnum.ACCREDITAMENTO_TIPO_PROVVISORIO);
			
			if(accreditamentiAttivi.isEmpty()){
				Accreditamento accreditamento = new Accreditamento(AccreditamentoEnum.ACCREDITAMENTO_TIPO_PROVVISORIO);
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
		LOGGER.debug("Caricamento domanda di accreditamento: " + accreditamentoId);
		return accreditamentoRepository.findOne(accreditamentoId);
	};
	
	@Override
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId) {
		LOGGER.debug("Recupero domande di accreditamento per il provider " + providerId);
		Set<Accreditamento> accreditamenti = accreditamentoRepository.findByProviderId(providerId);
		if(accreditamenti != null) 
			LOGGER.debug("Trovati " + accreditamenti.size() + " accreditamenti");
		return accreditamenti;
	}
	
	/**
	 * Restituisce tutte le domande di accreditamento che hanno una data di scadenza "attiva"
	 * */
	@Override
	public Set<Accreditamento> getAccreditamentiAvviatiForProvider(Long providerId, AccreditamentoEnum tipoDomanda) {
		LOGGER.debug("Recupero domande di accreditamento avviate per il provider " + providerId);
		LOGGER.debug("Ricerca domande di accreditamento di tipo: " + tipoDomanda.name() + "con data di scadenza posteriore a: " + LocalDate.now());
		return accreditamentoRepository.findByProviderIdAndTipoDomandaAndDataScadenzaAfter(providerId, tipoDomanda, LocalDate.now());
	}
	
	/**
	 * Restituisce l'unica domanda di accreditamento che ha una data di scadenza "attiva" e che è in stato "APPROVATO"
	 * */	
	@Override
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) {
		LOGGER.debug("Recupero eventuale accreditamento attivo per il provider: " + providerId);
		Accreditamento accreditamento = accreditamentoRepository.findOneByProviderIdAndStatoAndDataScadenzaAfter(providerId, AccreditamentoEnum.ACCREDITAMENTO_STATO_APPROVATO, LocalDate.now());
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
			if(accreditamento.isBozza())
				return false;
			
			if(accreditamento.isAttivo())
				return false;
			
			if(accreditamento.isInviato())
				return false;
		}
		
		return canProvider;
	}
	
	@Override
	public List<Integer> getIdEditabili(Long accreditamentoId) {
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		if(accreditamento != null)
			return accreditamento.getIdEditabili();
		return new ArrayList<Integer>();
	}
	
	@Override
	@Transactional
	public void removeIdEditabili(Long accreditamentoId, List<Integer> idEditabiliToRemove) {
		LOGGER.debug("Rimozione idEditabili " +  idEditabiliToRemove + "dalla domanda : " + accreditamentoId);

		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.getIdEditabili().removeAll(idEditabiliToRemove);
		accreditamentoRepository.save(accreditamento);
	}
	
	@Override
	@Transactional
	public void addIdEditabili(Long accreditamentoId, List<Integer> idEditabiliToAdd) {
		LOGGER.debug("Aggiunta idEditabili " +  idEditabiliToAdd + "alla domanda : " + accreditamentoId);
		
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.getIdEditabili().addAll(idEditabiliToAdd);
		accreditamentoRepository.save(accreditamento);
	}

	@Override
	@Transactional
	public void inviaDomandaAccreditamento(Long accreditamentoId) {
		LOGGER.debug("Invio domanda di Accreditamento " + accreditamentoId + " alla segreteria");
		
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		if(accreditamento.getDataInvio() == null)
			accreditamento.setDataInvio(LocalDate.now());
		accreditamento.setStato(AccreditamentoEnum.ACCREDITAMENTO_STATO_INVIATO);
		accreditamento.getIdEditabili().clear();
		accreditamento.setEditabile(false);
		
		accreditamentoRepository.save(accreditamento);
		
		Set<Evento> eventiNelPianoFormativo = eventoService.getAllEventiFromProviderInPianoFormativo(accreditamento.getProvider().getId(), accreditamento.getPianoFormativo());
		for(Evento e : eventiNelPianoFormativo)
			e.getIdEditabili().clear();
	}
	
	@Override
	@Transactional
	public void inserisciPianoFormativo(Long accreditamentoId) {
		LOGGER.debug("Inserimento piano formativo per la domanda di Accreditamento " + accreditamentoId);
		
		Accreditamento accreditamento = accreditamentoRepository.findOne(accreditamentoId);
		accreditamento.getIdEditabili().clear();
		accreditamento.setPianoFormativo(LocalDate.now().getYear());
		accreditamentoRepository.save(accreditamento);
	}
	
	@Override
	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(Long accreditamentoId) throws Exception{
		LOGGER.debug("Recupero datiAccreditamento per la domanda " + accreditamentoId);
		DatiAccreditamento datiAccreditamento = accreditamentoRepository.getDatiAccreditamentoForAccreditamento(accreditamentoId);
		if(datiAccreditamento == null)
				throw new Exception("Dati non presenti");
				
		return datiAccreditamento;
	}
	
	@Override
	public Long getProviderIdForAccreditamento(Long accreditamentoId) {
		return accreditamentoRepository.getProviderIdById(accreditamentoId);
	}
}
