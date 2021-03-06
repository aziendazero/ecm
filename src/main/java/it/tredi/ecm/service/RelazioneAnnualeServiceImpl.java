package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;
import it.tredi.ecm.dao.repository.RelazioneAnnualeRepository;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;

@Service
public class RelazioneAnnualeServiceImpl implements RelazioneAnnualeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelazioneAnnualeServiceImpl.class);

	@Autowired
	private ProviderService providerService;
	@Autowired
	private RelazioneAnnualeRepository relazioneAnnualeRepository;
	@Autowired
	private EventoPianoFormativoService eventoPianoFormativoService;
	@Autowired
	private EventoService eventoService;
	@Autowired
	private AnagrafeRegionaleCreditiService anagrafeRegionaleCreditiService;
	@Autowired
	private FileService fileService;
	@Autowired
	private EcmProperties ecmProperties;

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
	public RelazioneAnnuale getRelazioneAnnualeForProviderIdAndAnnoRiferimento(Long providerId,
			Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage(
				"Recupero la Relazione Annuale dell'anno " + annoRiferimento + " per il provider: " + providerId));
		return relazioneAnnualeRepository.findOneByProviderIdAndAnnoRiferimento(providerId, annoRiferimento);
	}

	@Override
	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrata(Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage(
				"Recupero tutti i provider che non hanno ancora inserito la relazione Annuale per l'anno "
						+ annoRiferimento));
		return relazioneAnnualeRepository.findAllProviderNotRelazioneAnnualeRegistrata(annoRiferimento);
	}

	@Override
	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrataAllaScadenza() {
		LOGGER.debug(Utils
				.getLogMessage("Recupero tutti i provider che non hanno inserito la relazione Annuale alla scadenza"));
		Set<Provider> listaProvider = new HashSet<Provider>();
		LocalDate dataScadenza = LocalDate.of(LocalDate.now().getYear(),
				ecmProperties.getRelazioneAnnualeMeseFineModifica(),
				ecmProperties.getRelazioneAnnualeGiornoFineModifica());
		if (LocalDate.now().isAfter(dataScadenza)) {
			listaProvider = getAllProviderNotRelazioneAnnualeRegistrata(LocalDate.now().getYear() - 1);
		}
		return listaProvider;
	}

	@Override
	public int countProviderNotRelazioneAnnualeRegistrataAllaScadenza() {
		LOGGER.debug(Utils
				.getLogMessage("Recupero tutti i provider che non hanno inserito la relazione Annuale alla scadenza"));
		Set<Provider> listaProvider = getAllProviderNotRelazioneAnnualeRegistrataAllaScadenza();
		if (listaProvider != null)
			return listaProvider.size();
		return 0;
	}

	// ERM012514 - RA va creata dal 01/01-30/06 per lanno scorso, dal 01/07- 31/12
	// per anno corrente
	@Override
	public RelazioneAnnuale createRelazioneAnnuale(Long providerId) {
		int annoRiferimento = ecmProperties.getAnnoDiRiferimentoRA_rispettoDataCorrente(); //
		LOGGER.debug(Utils.getLogMessage("Check se esiste gia RA per annoRif : " + annoRiferimento));
		RelazioneAnnuale r = getRelazioneAnnualeForProviderIdAndAnnoRiferimento(providerId, annoRiferimento);
		if (r != null) {
			return null;
		}

		LOGGER.debug(Utils.getLogMessage("Sto creando RA per annoRif : " + annoRiferimento
				+ " modificabile entro 30/03 del anno " + (annoRiferimento + 1)));
		RelazioneAnnuale relazioneAnnuale = new RelazioneAnnuale();
		relazioneAnnuale.setAnnoRiferimento(annoRiferimento);
		relazioneAnnuale.setProvider(providerService.getProvider(providerId));
		// modificabile entro il 30 Aprile dell'annosuccesivo al anno di riferimento
		relazioneAnnuale.setDataFineModifca(
				LocalDate.of(annoRiferimento + 1, ecmProperties.getRelazioneAnnualeMeseFineModifica(),
						ecmProperties.getRelazioneAnnualeGiornoFineModifica()));
		return relazioneAnnuale;
	}

	@Override
	public void elaboraRelazioneAnnualeAndSave(RelazioneAnnuale relazioneAnnuale, File relazioneFinale,
			boolean asBozza) {
		relazioneAnnuale.setEventiPFA(eventoPianoFormativoService.getAllEventiFromProviderInPianoFormativo(
				relazioneAnnuale.getProvider().getId(), relazioneAnnuale.getAnnoRiferimento()));
		relazioneAnnuale.setEventiAttuati(eventoService.getEventiForRelazioneAnnualeByProviderIdAndAnnoRiferimento(
				relazioneAnnuale.getProvider().getId(), relazioneAnnuale.getAnnoRiferimento()));
		relazioneAnnuale.setRiepilogoAnagrafeAventeCrediti(anagrafeRegionaleCreditiService.getRuoliAventeCreditiPerAnno(
				relazioneAnnuale.getProvider().getId(), relazioneAnnuale.getAnnoRiferimento()));
		relazioneAnnuale
				.setProfessioniAventeCrediti(anagrafeRegionaleCreditiService.getProfessioniAnagrafeAventeCrediti(
						relazioneAnnuale.getProvider().getId(), relazioneAnnuale.getAnnoRiferimento()));
		relazioneAnnuale.elabora();
		save(relazioneAnnuale, relazioneFinale, asBozza);
	}

	@Transactional
	private void save(RelazioneAnnuale relazioneAnnuale, File relazioneFinale, boolean asBozza) {
		relazioneAnnuale.setBozza(asBozza);
		// evita la TransientPropertyValueException
		if (relazioneFinale == null || relazioneFinale.isNew())
			relazioneAnnuale.setRelazioneFinale(null);
		else {
			relazioneAnnuale.setRelazioneFinale(fileService.getFile(relazioneFinale.getId()));
		}
		relazioneAnnualeRepository.save(relazioneAnnuale);
	}

	@Override
	public boolean isLastRelazioneAnnualeInserita(Long providerId) {
		// ERM012514 - get anno di riferimento che dipende dal periodo del anno primo /
		// secondo semestre
		return relazioneAnnualeRepository.findAllByProviderIdAndAnnoRiferimento(providerId,
				ecmProperties.getAnnoDiRiferimentoRA_rispettoDataCorrente()).size() > 0;
	}

	@Transactional
	@Override
	public void aggiornaDataDiFineModificaPerRelazioneAnnualeForProviderIdAndAnnoRiferimento(Long providerId,
			Integer annoRiferimento, LocalDate dataFineModifca) {
		RelazioneAnnuale ra = getRelazioneAnnualeForProviderIdAndAnnoRiferimento(providerId, annoRiferimento);
		if (ra != null) {
			ra.setDataFineModifca(dataFineModifca);
			relazioneAnnualeRepository.save(ra);
		}
	}
}
