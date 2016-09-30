package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.repository.EventoRepository;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(EventoServiceImpl.class);

	@Autowired
	private EventoRepository eventoRepository;

	@Override
	public Evento getEvento(Long id) {
		LOGGER.debug("Recupero evento: " + id);
		return eventoRepository.findOne(id);
	}

	@Override
	public Set<Evento> getAllEventiFromProvider(Long providerId) {
		LOGGER.debug("Recupero eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderId(providerId);
	}

	@Override
	public Set<Evento> getAllEventiFromProviderInPianoFormativo(Long providerId, Integer pianoFormativo) {
		LOGGER.debug("Recupero eventi del provider " + providerId + " relativi al piano formativo " + pianoFormativo);
		return eventoRepository.findAllByProviderIdAndPianoFormativo(providerId, pianoFormativo);
	}

	@Override
	@Transactional
	public void save(Evento evento) {
		LOGGER.debug("Salvataggio evento");
		eventoRepository.save(evento);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento:" + id);
		eventoRepository.delete(id);
	}
	
	@Override
	public void copyEvento(Evento src, Evento dst) throws CloneNotSupportedException {
		LOGGER.debug("Copia evento: " + src.getId() + " (" + src.getCodiceIdentificativo() + ")" );
		dst = (Evento) src.clone();
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
