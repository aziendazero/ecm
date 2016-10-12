package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.web.bean.EventoWrapper;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(Evento.class);

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
	@Transactional
	public void save(Evento evento) {
		LOGGER.debug("Salvataggio evento");
		if(evento.isNew()) {
			eventoRepository.saveAndFlush(evento);
			evento.buildPrefix();
		}
		eventoRepository.save(evento);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento:" + id);
		eventoRepository.delete(id);
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

	@Override
	public List<Evento> getAllEventi() {
		LOGGER.debug("Recupero tutti gli eventi");
		return eventoRepository.findAll();
	}

	@Override
	public Set<Evento> getAllEventiForProviderId(Long providerId) {
		LOGGER.debug("Recupero tutti gli eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderId(providerId);
	}

	@Override
	public boolean canCreateEvento(Account account) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Evento handleRipetibili(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();

		//date intermedie
		if (eventoWrapper.getDateIntermedieTemp() != null && !eventoWrapper.getDateIntermedieTemp().isEmpty()) {
			Set<LocalDate> dateIntermedie = ((EventoRES) evento).getDateIntermedie();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			for (String s : eventoWrapper.getDateIntermedieTemp()) {
				if(s != null && !s.isEmpty()) {
					LocalDate data = LocalDate.parse(s, dtf);
					dateIntermedie.add(data);
				}
			}
			((EventoRES) evento).setDateIntermedie(dateIntermedie);
		}

		return evento;
	}

}
