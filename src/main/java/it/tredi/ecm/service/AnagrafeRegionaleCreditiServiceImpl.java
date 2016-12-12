package it.tredi.ecm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.dao.repository.AnagrafeRegionaleCreditiRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AnagrafeRegionaleCreditiServiceImpl implements AnagrafeRegionaleCreditiService {

	private static Logger LOGGER = LoggerFactory.getLogger(AnagrafeRegionaleCreditiServiceImpl.class);

	@Autowired private AnagrafeRegionaleCreditiRepository anagrafeRegionaleCreditiRepository;

	@Override
	public Set<Integer> getAnnoListForAnagrafeRegionaleCrediti() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista anni disponibili"));
		Set<Integer> annoList = new HashSet<Integer>();

		//data minima presente
		LocalDate minDate = anagrafeRegionaleCreditiRepository.getMinData();

		//creo un elenco dall'anno minimo fino all'anno corrente
		if(minDate != null)
			for(int a = minDate.getYear(); a <= LocalDate.now().getYear(); a++)
				annoList.add(a);

		return annoList;
	}

	@Override
	public Set<AnagrafeRegionaleCrediti> getAll(Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le anagrafe regionali crediti ECM per l'anno: " + annoRiferimento));
		Set<AnagrafeRegionaleCrediti> result = new HashSet<AnagrafeRegionaleCrediti>();

		List<Object[]> items = anagrafeRegionaleCreditiRepository.findDistinctAll(LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
		for(Object[] obj : items)
			result.add(new AnagrafeRegionaleCrediti((String) obj[0], (String) obj[1], (String) obj[2], null, null, null));

		return result;
	}

	@Override
	public Set<AnagrafeRegionaleCrediti> getAllByCodiceFiscale(String codiceFiscale, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero anagrafe regionali crediti ECM: " + codiceFiscale + " per l'anno " + annoRiferimento));
		return anagrafeRegionaleCreditiRepository.findDistinctAllByCodiceFiscale(codiceFiscale, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
	}

	@Override
	public BigDecimal getSumCreditiByCodiceFiscale(String codiceFiscale, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero somma crediti anagrafe regionali: " + codiceFiscale + " per l'anno " + annoRiferimento));
		BigDecimal somma = new BigDecimal(0);
		somma = anagrafeRegionaleCreditiRepository.getSumCreditiByCodiceFiscale(codiceFiscale, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
		return somma;
	}

	/*
	 *Faccio una query per individuare tutti i ruoli che hanno ricevuto crediti contanto il numero di persone distinte.
	 *Ottengo per ciascun ruolo quante persone hanno avuto crediti
	 *
	 **/
	@Override
	public Map<String,Integer> getRuoliAventeCreditiPerAnno(Long providerId, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero ruoli con conteggio di persone distinte che hanno avuto crediti nell'anno: " + annoRiferimento + " per il provide " + providerId));
		Map<String,Integer> ruoli = new HashMap<String, Integer>();

		List<Object[]> result = anagrafeRegionaleCreditiRepository.getRuoliAventeCreditiPerAnno(providerId, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
		for(Object[] obj : result)
			ruoli.put((String) obj[0], Integer.valueOf((int)(long)obj[1]));

		return ruoli;
	};

	@Override
	public int getProfessioniAnagrafeAventeCrediti(Long providerId, Integer annoRiferimento) {
		return anagrafeRegionaleCreditiRepository.getProfessioniAnagrafeAventeCrediti(providerId, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
	}

//	@Override
//	public Set<Evento> getEventiAnagrafeAventeCrediti(Long providerId, Integer annoRiferimento){
//		LOGGER.debug(Utils.getLogMessage("Recupero eventi che hanno avuto crediti nell'anno: " + annoRiferimento + " per il provide " + providerId));
//		return anagrafeRegionaleCreditiRepository.getEventiAnagrafeAventeCrediti(providerId, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
//	}
}
