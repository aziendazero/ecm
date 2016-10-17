package it.tredi.ecm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.repository.PianoFormativoRepository;
import it.tredi.ecm.dao.repository.RendicontazioneInviataRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class RendicontazioneInviataServiceImpl implements RendicontazioneInviataService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RendicontazioneInviataServiceImpl.class);
	@Autowired private RendicontazioneInviataRepository rendicontazioneInviataRepository;

	@Override
	public void save(RendicontazioneInviata rendicontazioneInviata) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio rendicontazione inviata"));
//TODO - migliorare logging		
		rendicontazioneInviataRepository.save(rendicontazioneInviata);
		
	}

}
