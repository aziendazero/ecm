package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.repository.PianoFormativoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class PianoFormativoServiceImpl implements PianoFormativoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PianoFormativoServiceImpl.class);
	
	@Autowired private PianoFormativoRepository pianoFormativoRepository;
	@Autowired private ProviderService providerService;
	
	@Override
	public boolean exist(Long providerId, Integer annoPianoFormativo){
		LOGGER.debug(Utils.getLogMessage("Check exist piano formativo " + annoPianoFormativo + " del Provider: " + providerId));
		PianoFormativo pianoFormativo = getPianoFormativoAnnualeForProvider(providerId, annoPianoFormativo);
		if(pianoFormativo == null)
			return false;
		else
			return true;
	}

	@Override
	@Transactional
	public PianoFormativo create(Long providerId, Integer annoPianoFormativo) {
		LOGGER.debug(Utils.getLogMessage("Inserimento Piano Formativo Anno " + annoPianoFormativo + " del Provider: " + providerId));
		Provider provider = providerService.getProvider();
		PianoFormativo pianoFormativo = new PianoFormativo();
		pianoFormativo.setAnnoPianoFormativo(annoPianoFormativo);
		pianoFormativo.setProvider(provider);
		pianoFormativoRepository.save(pianoFormativo);
		//TODO calcolare la data entro la quale il piano è modificabile
		return pianoFormativo;
	}
	
	@Override
	@Transactional
	public void save(PianoFormativo pianoFormativo) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Piano Formativo Anno " + pianoFormativo.getAnnoPianoFormativo() + " del Provider: " + pianoFormativo.getProvider().getId()));
		pianoFormativoRepository.save(pianoFormativo);
	}
	
	@Override
	public PianoFormativo getPianoFormativo(Long pianoFormativoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Piano Formativo: " + pianoFormativoId));
		return pianoFormativoRepository.findOne(pianoFormativoId);
	}
	
	@Override
	public Set<PianoFormativo> getAllPianiFormativiForProvider(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero Piani Formativi del Provider: " + providerId));
		return pianoFormativoRepository.findAllByProviderId(providerId);
	}

	@Override
	public PianoFormativo getPianoFormativoAnnualeForProvider(Long providerId, Integer annoPianoFormativo) {
		LOGGER.debug(Utils.getLogMessage("Recupero Piano Formativo Anno " + annoPianoFormativo + " del Provider: " + providerId));
		return pianoFormativoRepository.findOneByProviderIdAndAnnoPianoFormativo(providerId, annoPianoFormativo);
	}
	
	@Override
	public boolean isEditabile(Long pianoFormativoId) {
		PianoFormativo pianoFormativo = pianoFormativoRepository.findOne(pianoFormativoId);
		if(pianoFormativo == null)
			return false;
		return pianoFormativo.isEditabile();
	}
}
