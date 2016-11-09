package it.tredi.ecm.scheduledtask;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.RendicontazioneInviataService;

@Component
public class InvioCogeapsTask {
	private static Logger LOGGER = LoggerFactory.getLogger(InvioCogeapsTask.class);
	
	@Autowired EventoService eventoService;
	@Autowired RendicontazioneInviataService rendicontazioneInviataService;
	
	@Async
	@Transactional
	public void checkStatoElaborazioneCogeaps() throws Exception{
		LOGGER.debug(Thread.currentThread().getName());
		LOGGER.debug("checkStatoElaborazioneCogeaps - entering");
		Set<RendicontazioneInviata> rendicontazioniPendenti = rendicontazioneInviataService.getAllInviiRendicontazionePendenti();
		for (RendicontazioneInviata rendicontazioneInviata:rendicontazioniPendenti) {
			Long eventoId = rendicontazioneInviata.getEvento().getId();
			eventoService.statoElaborazioneCogeaps(eventoId);
		}
		LOGGER.debug("checkStatoElaborazioneCogeaps - exiting");
	}
}
