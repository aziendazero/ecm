package it.tredi.ecm.service.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.controller.EventoServiceController;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoCrediti {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoCrediti.class);

	@Autowired private EventoServiceController eventoServiceController;
	@Autowired private EventoCreditiVersioneUno eventoCreditiVersioneUno;
	@Autowired private EventoCreditiVersioneDue eventoCreditiVersioneDue;
	@Autowired private EventoCreditiVersioneDue eventoCreditiVersioneTre;

	// EVENTO_VERSIONE
	public float calcoloCreditiEvento(EventoWrapper eventoWrapper) throws Exception {
		EventoVersioneEnum versioneEvento = eventoServiceController.versioneEvento(eventoWrapper.getEvento());
		switch (versioneEvento) {
		case UNO_PRIMA_2018:
			return eventoCreditiVersioneUno.calcoloCreditiEvento(eventoWrapper);
		case DUE_DAL_2018:
			return eventoCreditiVersioneDue.calcoloCreditiEvento(eventoWrapper);
		case TRE_DAL_2019:
			return eventoCreditiVersioneTre.calcoloCreditiEvento(eventoWrapper);
		default:
			LOGGER.error("Evento versione: " + versioneEvento + " non gestita");
			throw new Exception("Evento versione: " + versioneEvento + " non gestita");
		}
	}

}
