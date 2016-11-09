package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;
import it.tredi.ecm.dao.repository.QuotaAnnualeRepository;
import it.tredi.ecm.dao.repository.RelazioneAnnualeRepository;
import it.tredi.ecm.utils.Utils;
import scala.noinline;

@Service
public class RelazioneAnnualeServiceImpl implements RelazioneAnnualeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelazioneAnnualeServiceImpl.class);
	
	@Autowired private ProviderService providerService;
	@Autowired private RelazioneAnnualeRepository relazioneAnnualeRepository;
	@Autowired private EventoPianoFormativoService eventoPianoFormativoService;
	@Autowired private EventoService eventoService;
	
	@Override
	public RelazioneAnnuale getRelazioneAnnuale(Long relazioneAnnualeId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Relazione Annuale: " + relazioneAnnualeId));
		return relazioneAnnualeRepository.findOne(relazioneAnnualeId);
	}
	
	@Override
	public Set<RelazioneAnnuale> getAllRelazioneAnnuale() {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le Relazioni Annuali"));
		return relazioneAnnualeRepository.findAll();
	}
	
	@Override
	public Set<RelazioneAnnuale> getAllRelazioneAnnualeByProviderId(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le Relazioni Annuali per il provider: " + providerId));
		return relazioneAnnualeRepository.findAllByProviderId(providerId);
	}
	
	@Override
	public RelazioneAnnuale getRelazioneAnnualeForProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero la Relazione Annuale dell'anno " + annoRiferimento + " per il provider: " + providerId));
		return relazioneAnnualeRepository.findOneByProviderIdAndAnnoRiferimento(providerId, annoRiferimento);
	}
	
	@Override
	@Transactional
	public void save(RelazioneAnnuale relazioneAnnuale) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Relazione Annuale"));
		relazioneAnnualeRepository.save(relazioneAnnuale);
	}
	
	@Override
	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrata(Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti i provider che non hanno ancora inserito la relazione Annuale per l'anno " + annoRiferimento));
		return relazioneAnnualeRepository.findAllProviderNotRelazioneAnnualeRegistrata(annoRiferimento);
	}
	
	@Override
	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrataAllaScadenza() {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti i provider che non hanno inserito la relazione Annuale alla scadenza"));
		
		Set<Provider> listaProvider = new HashSet<Provider>();
		
		LocalDate dataScadenza = LocalDate.of(LocalDate.now().getYear(), 4, 30);
		if(LocalDate.now().isAfter(dataScadenza)){
			listaProvider = getAllProviderNotRelazioneAnnualeRegistrata(LocalDate.now().getYear() - 1);
		}
		
		return listaProvider;
	}
	
	@Override
	public int countProviderNotRelazioneAnnualeRegistrataAllaScadenza() {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti i provider che non hanno inserito la relazione Annuale alla scadenza"));
		Set<Provider> listaProvider = getAllProviderNotRelazioneAnnualeRegistrataAllaScadenza();
		if(listaProvider != null)
			return listaProvider.size();
		return 0;
	}
	
	@Override
	public RelazioneAnnuale createRelazioneAnnuale(Long providerId, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti i provider che non hanno inserito la relazione Annuale alla scadenza"));
		RelazioneAnnuale relazioneAnnuale = new RelazioneAnnuale();
		
		relazioneAnnuale.setAnnoRiferimento(annoRiferimento);
		relazioneAnnuale.setProvider(providerService.getProvider(providerId));
		
		relazioneAnnuale.setEventiPFA(eventoPianoFormativoService.getAllEventiFromProviderInPianoFormativo(providerId, annoRiferimento));
		relazioneAnnuale.setEventiAttuati(eventoService.getEventiByProviderIdAndAnnoRiferimento(providerId, annoRiferimento));
		
		return relazioneAnnuale;
	}
}
