package it.tredi.ecm.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEventoBase;
import it.tredi.ecm.dao.repository.AnagraficaFullEventoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AnagraficaFullEventoServiceImpl implements AnagraficaFullEventoService {

	private static Logger LOGGER = LoggerFactory.getLogger(AnagraficaFullEventoServiceImpl.class);

	@Autowired private AnagraficaFullEventoRepository anagraficaFullEventoRepository;

	@Override
	public AnagraficaFullEvento getAnagraficaFullEvento(Long anagraficaFullEventoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento : " + anagraficaFullEventoId));
		return anagraficaFullEventoRepository.findOne(anagraficaFullEventoId);
	}

	@Override
	public AnagraficaFullEvento getAnagraficaFullEventoByCodiceFiscaleForProvider(String codiceFiscale, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento : " + codiceFiscale + " per provider " + providerId));
		return anagraficaFullEventoRepository.findOneByAnagraficaCodiceFiscaleAndProviderId(codiceFiscale, providerId);
	}

	@Override
	public Set<AnagraficaFullEvento> getAllAnagraficheFullEventoByProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento per provider: " + providerId));
		return anagraficaFullEventoRepository.findAllByProviderId(providerId);
	}

	@Override
	public Set<AnagraficaFullEvento> getAllAnagraficheFullEventoByProviderJSONVersion(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero AnagraficheFullEvento (JSONVersion) per provider: " + providerId));

		Set<AnagraficaFullEvento> lista = new HashSet<AnagraficaFullEvento>();
		List<Object[]> listOfObjs = anagraficaFullEventoRepository.findAllByProviderIdJSONVersion(providerId);
		for(Object[] obj : listOfObjs){
			AnagraficaFullEvento a = new AnagraficaFullEvento();
			a.setId((Long) obj[0]);
			AnagraficaFullEventoBase aB = new AnagraficaFullEventoBase();
			aB.setCognome((String) obj[1]);
			aB.setNome((String) obj[2]);
			aB.setCodiceFiscale((String) obj[3]);
			a.setAnagrafica(aB);
			lista.add(a);
		}
		return lista;
	}

	@Override
	@Transactional
	public void save(AnagraficaFullEvento anagraficaFullEvento) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio AnagraficheFullEvento per provider: " + anagraficaFullEvento.getProvider().getId()));
		anagraficaFullEventoRepository.save(anagraficaFullEvento);
	}

}
