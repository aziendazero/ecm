package it.tredi.ecm.service.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoCrediti {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoCrediti.class);

	@Autowired private EventoService eventoService;
	@Autowired private EventoCreditiVersioneUno eventoCreditiVersioneUno;
	@Autowired private EventoCreditiVersioneDue eventoCreditiVersioneDue;
	
	public float calcoloCreditiEvento(EventoWrapper eventoWrapper) throws Exception {
		EventoVersioneEnum eventoVersione = eventoService.versioneEvento(eventoWrapper.getEvento());
		switch (eventoVersione) {
		case UNO_PRIMA_2018:
			return eventoCreditiVersioneUno.calcoloCreditiEvento(eventoWrapper);
		case DUE_DAL_2018:
			return eventoCreditiVersioneDue.calcoloCreditiEvento(eventoWrapper);
		default:
			LOGGER.error("Evento versione: " + eventoVersione + " non gestita");
			throw new Exception("Evento versione: " + eventoVersione + " non gestita");
		}
	}
	
}
