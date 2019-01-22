package it.tredi.ecm.service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.repository.ObiettivoRepository;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.service.controller.EventoServiceController;

@Service
public class ObiettivoServiceImpl implements ObiettivoService {

	private final static Logger LOGGER = Logger.getLogger(ObiettivoServiceImpl.class);

	@Autowired private ObiettivoRepository obiettivoRepository;
	@Autowired private EcmProperties ecmProperties;
	@Autowired private EventoServiceController eventoServiceController;

	@Override
	public Set<Obiettivo> getAllObiettivi() {
		LOGGER.debug("Recupero tutti gli Obiettivi");
		return obiettivoRepository.findAll();
	}

	// EVENTO_VERSIONE
	@Override
	public Set<Obiettivo> getObiettiviNazionali(EventoVersioneEnum versione) {
		LOGGER.debug("Recupero tutti gli Obiettivi Nazionali per versione " + versione);

		if(versione == null) {
			versione = ecmProperties.getEventoVersioneDefault();
		}

		return obiettivoRepository.findAllByNazionaleAndVersioneOrderByCodiceCogeapsAsc(true, versione.getNumeroVersione()).stream().sorted(Comparator.comparing(Obiettivo::getIntCogeaps)).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	// EVENTO_VERSIONE
	@Override
	public Set<Obiettivo> getObiettiviNazionali() {
		return getObiettiviNazionali(null);
	}

	@Override
	public Set<Obiettivo> getObiettiviRegionali() {
		LOGGER.debug("Recupero tutti gli Obiettivi Regionali");
		return getObiettiviRegionali(null);
	}

	@Override
	public Set<Obiettivo> getObiettiviRegionali(EventoVersioneEnum versione) {
		LOGGER.debug("Recupero tutti gli Obiettivi Regionali per versione " + versione);

		if(versione == null) {
			versione = ecmProperties.getEventoVersioneDefault();
		}else if(eventoServiceController.isVersionDue(versione)) {
			versione = EventoVersioneEnum.UNO_PRIMA_2018; //nella versione 2 gli obiettivi regionali non erano cambiati
		}

		return obiettivoRepository.findAllByNazionaleAndVersioneOrderByCodiceCogeapsAsc(false, versione.getNumeroVersione()).stream().sorted(Comparator.comparing(Obiettivo::getIntCogeaps)).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	@Transactional
	public void save(Obiettivo obiettivo) {
		LOGGER.debug("Salvataggio obiettivo " + ((obiettivo.isNazionale()) ? "nazionale" : "regionale"));
		obiettivoRepository.save(obiettivo);
	}

	@Override
	@Transactional
	public void save(Set<Obiettivo> obiettivi) {
		LOGGER.debug("Salvataggio obiettivi");
		obiettivoRepository.save(obiettivi);
	}

	@Override
	public Obiettivo getObiettivo(Long obiettivoId) {
		LOGGER.info("Retrieving Obiettivo (" + obiettivoId +")");
		return obiettivoRepository.findOne(obiettivoId);
	}

	// EVENTO_VERSIONE
	@Override
	public Obiettivo findOneByCodiceCogeaps(String codiceCogeaps, boolean nazionale) {
		LOGGER.info("Retrieving Obiettivo by CodiceCogeaps (" + codiceCogeaps +") and Nazionale (" + nazionale + ")");
		Set<Obiettivo> obiettivi = obiettivoRepository.findAllByCodiceCogeapsAndNazionale(codiceCogeaps, nazionale);
		if (obiettivi == null)
			return null;
		return obiettivi.iterator().next();
	}

	@Override
	public Set<Obiettivo> getObiettiviByCodiceCogeapsAndVersioneEventi(boolean nazionale, List<String> codiceCogeaps, EventoVersioneEnum versione) {
		LOGGER.info("Retrieving Obiettivo by CodiceCogeaps IN (" + codiceCogeaps.toString() +") and versioneEvento (" + versione.name() + ") + and Nazionale (" + nazionale + ")");
		return obiettivoRepository.findAllByNazionaleAndCodiceCogeapsInAndVersione(nazionale, codiceCogeaps, versione.getNumeroVersione());
	}

}
