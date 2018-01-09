package it.tredi.ecm.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import it.tredi.ecm.service.bean.EcmProperties;

@Component
public class EventoServiceController {
	@Autowired private EcmProperties ecmProperties;

	public EventoVersioneEnum versioneEvento(Evento evento) {
		EventoVersioneEnum versione = ecmProperties.getEventoVersioneDefault();
		//se l'evento ha gia' una versione impostata e salvata questa e' la versione
		if(evento.getVersione() != null) {
			versione = evento.getVersione();
		} else {
			//se la data inizio dell'evento e' maggiore uguale al 2018 utilizzo il nuovo metodo di calcolo
			if(evento.getDataInizio() != null) {
				if(evento.getDataInizio().isAfter(ecmProperties.getEventoDataPassaggioVersioneDue()) || evento.getDataInizio().isEqual(ecmProperties.getEventoDataPassaggioVersioneDue())) {
					versione = EventoVersioneEnum.DUE_DAL_2018;
				} else {
					versione = EventoVersioneEnum.UNO_PRIMA_2018;
				}
			}
		}
		return versione;
	}
	
	public boolean fadDisableSupportoSvoltoDaEsperto(EventoFAD evento) {
		if(versioneEvento(evento) == EventoVersioneEnum.DUE_DAL_2018 && evento.getTipologiaEventoFAD() == TipologiaEventoFADEnum.EVENTI_SEMINARIALI_IN_RETE) {
			return true;
		}
		return false;
	}

}
