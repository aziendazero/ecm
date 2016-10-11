package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.repository.EventoPianoFormativoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class EventoPianoFormativoServiceImpl implements EventoPianoFormativoService {
	public static final Logger LOGGER = Logger.getLogger(EventoPianoFormativoServiceImpl.class);

	@Autowired
	private EventoPianoFormativoRepository eventoPianoFormativoRepository;

	@Override
	public EventoPianoFormativo getEvento(Long id) {
		LOGGER.debug("Recupero evento: " + id);
		return eventoPianoFormativoRepository.findOne(id);
	}

	@Override
	public Set<EventoPianoFormativo> getAllEventiFromProvider(Long providerId) {
		LOGGER.debug("Recupero eventi del provider: " + providerId);
		return eventoPianoFormativoRepository.findAllByProviderId(providerId);
	}

	@Override
	public Set<EventoPianoFormativo> getAllEventiFromProviderInPianoFormativo(Long providerId, Integer pianoFormativo) {
		LOGGER.debug("Recupero eventi del provider " + providerId + " relativi al piano formativo " + pianoFormativo);
		return eventoPianoFormativoRepository.findAllByProviderIdAndPianoFormativo(providerId, pianoFormativo);
	}

	@Override
	@Transactional
	public void save(EventoPianoFormativo evento) throws Exception {
		LOGGER.debug("Salvataggio evento");
		if(evento.isNew()) {
			eventoPianoFormativoRepository.saveAndFlush(evento);
			evento.buildPrefix();
		}
		eventoPianoFormativoRepository.save(evento);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento:" + id);
		eventoPianoFormativoRepository.delete(id);
	}

	@Override
	public void buildPrefix(EventoPianoFormativo evento) throws Exception{
		LOGGER.debug(Utils.getLogMessage("Salvataggio prefix per evento: " + evento.getId()));
		evento.buildPrefix();
		save(evento);
	}


	@Override
	public void validaRendiconto(File rendiconto) throws Exception {
		// TODO Auto-generated method stub
		// se File csv -> elaboro xml
		// se File xml, xml,p7m, xml.zip.p7m -> controllo se valido
		// ---
		// finito controllo/elaborazione
		// se File csv -> salvo File rendiconto in evento.setReportPartecipantiCSV così come è stato inviato
		// se File xml, xml.p7m, xml.zip.p7m e ha passato la validazione/è il risultato dell'elaborazione -> salvo File risultato in evento.setReportPartecimantiXML
	}

}
