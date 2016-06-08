package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.repository.AccreditamentoRepository;

@Service
public class AccreditamentoServiceImpl implements AccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoServiceImpl.class);
	
	@Autowired
	private AccreditamentoRepository accreditamentoRepository;
	
	@Autowired
	private ProviderService providerService;
	
	@Override
	public Accreditamento getNewAccreditamentoForCurrentProvider() throws Exception{
		Provider currentProvider = providerService.getProvider();
		if(currentProvider.isNew()){
			throw new Exception("Provider non registrato");
		}else{
			
			Set<Accreditamento> accreditamentiAttivi = getAccreditamentiAttviForProvider(currentProvider.getId(), Costanti.ACCREDITAMENTO_PROVVISORIO);
			
			if(accreditamentiAttivi.isEmpty()){
				Accreditamento accreditamento = new Accreditamento(Costanti.ACCREDITAMENTO_PROVVISORIO);
				accreditamento.setProvider(currentProvider);
				save(accreditamento);
				return accreditamento;
			}else{
				throw new Exception("E' già presente una domanda");
			}
		}
	}
	
	@Override
	public Accreditamento getAccreditamento(Long id) {
		LOGGER.debug("Caricamento domanda di accreditamento: " + id);
		return accreditamentoRepository.findOne(id);
	};
	
	@Override
	public Accreditamento getAccreditamento() {
		LOGGER.debug("Caricamento domanda di accreditamento corrente");
		return null;
	};
	
	@Override
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId) {
		LOGGER.debug("Recupero domande di accreditamento per il provider " + providerId);
		Set<Accreditamento> l = accreditamentoRepository.findByProviderId(providerId);
		return l;
	}
	
	@Override
	public Set<Accreditamento> getAccreditamentiAttviForProvider(Long providerId, String tipoDomanda) {
		LOGGER.debug("Recupero domande di accreditamento attive per il provider " + providerId);
		LOGGER.debug("Ricerca domande di accreditamento di tipo: " + tipoDomanda + "con data di scadenza posteriore a: " + LocalDate.now());
		return accreditamentoRepository.findByProviderIdAndTipoDomandaAndDataScadenzaAfter(providerId, tipoDomanda, LocalDate.now());
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
		}
		
		return canProvider;
	}
	
	@Override
	public List<Integer> getIdEditabili(Long accreditamentoId) {
		return accreditamentoRepository.findOne(accreditamentoId).getIdEditabili();
	}
	
	@Override
	@Transactional
	public void removeIdEditabili(Long accrediatementoId, List<Integer> idEditabiliToRemove) {
		LOGGER.debug("Rimozione Ideditabili " +  idEditabiliToRemove + "dalla domanda : " + accrediatementoId);

		Accreditamento accreditamento = accreditamentoRepository.findOne(accrediatementoId);
		accreditamento.getIdEditabili().removeAll(idEditabiliToRemove);
		accreditamentoRepository.save(accreditamento);
	}

}
