package it.tredi.ecm.service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.repository.ObiettivoRepository;

@Service
public class ObiettivoServiceImpl implements ObiettivoService {

	private final static Logger LOGGER = Logger.getLogger(ObiettivoServiceImpl.class);
	
	@Autowired
	private ObiettivoRepository obiettivoRepository;
	
	@Override
	public Set<Obiettivo> getAllObiettivi() {
		LOGGER.debug("Recupero tutti gli Obiettivi");
		return obiettivoRepository.findAll(); 
	}
	
	@Override
	public Set<Obiettivo> getObiettiviNazionali(EventoVersioneEnum versione) {
		LOGGER.debug("Recupero tutti gli Obiettivi Nazionali");
		if(versione == null || versione == EventoVersioneEnum.DUE_DAL_2018) {
			return obiettivoRepository.findAllByNazionaleAndVersioneNotOrderByCodiceCogeapsAsc(true, 1).stream().sorted(Comparator.comparing(Obiettivo::getIntCogeaps)).collect(Collectors.toCollection(LinkedHashSet::new)); 
		}else {
			return obiettivoRepository.findAllByNazionaleAndVersioneOrderByCodiceCogeapsAsc(true, 1).stream().sorted(Comparator.comparing(Obiettivo::getIntCogeaps)).collect(Collectors.toCollection(LinkedHashSet::new)); 
		}
		
	}
		
	@Override
	public Set<Obiettivo> getObiettiviNazionali() {
		return getObiettiviNazionali(EventoVersioneEnum.DUE_DAL_2018);
	}
	
	@Override
	public Set<Obiettivo> getObiettiviRegionali() {
		LOGGER.debug("Recupero tutti gli Obiettivi Regionali");
		return obiettivoRepository.findAllByNazionale(false);
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
	
	@Override
	public Obiettivo findOneByCodiceCogeaps(String codiceCogeaps, boolean nazionale) {
		LOGGER.info("Retrieving Obiettivo by CodiceCogeaps (" + codiceCogeaps +") and Nazionale (" + nazionale + ")");
		Set<Obiettivo> obiettivi = obiettivoRepository.findAllByCodiceCogeapsAndNazionale(codiceCogeaps, nazionale);
		if (obiettivi == null)
			return null;
		return obiettivi.iterator().next();
	}

	
}
