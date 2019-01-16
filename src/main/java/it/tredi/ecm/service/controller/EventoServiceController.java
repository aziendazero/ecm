package it.tredi.ecm.service.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.bean.EcmProperties;

// EVENTO_VERSIONE
@Component
public class EventoServiceController {
	@Autowired private EcmProperties ecmProperties;
	@Autowired private AccreditamentoService accreditamentoService;

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

	public boolean isVersionDue(Evento evento) {
		return versioneEvento(evento) == EventoVersioneEnum.DUE_DAL_2018;
	}

	public boolean fadDisableSupportoSvoltoDaEsperto(EventoFAD evento) {
		if(versioneEvento(evento) == EventoVersioneEnum.DUE_DAL_2018 && evento.getTipologiaEventoFAD() == TipologiaEventoFADEnum.EVENTI_SEMINARIALI_IN_RETE) {
			return true;
		}
		return false;
	}


	// ERM014776
	/*
	 * 	1)  se accreditamento e chiuso si puo modificare
	 * 		solo se data corrente non e maggiore di data chiusura
	 * 		del acc + un intervallo dei giorni
	 */
	public boolean canEdit(Evento e){
		return accreditamentoService.canProviderWorkWithEvent(e.getProvider().getId(), e) &&  e.canEdit(true); // mando true allora non controlla blockato
	}

	/*
	*	1) evento terminato
	*	2) sponsor non ancora caricati
	*	3) siamo ancora entro i 90 gg dalla fine dell'evento
	*	4) passati i 90 gg -> non è più possibile caricare gli sponsor
	*   5) la segreteria può sempre
	*/
	public boolean canDoUploadSponsor(Evento e){
		return accreditamentoService.canProviderWorkWithEvent(e.getProvider().getId(), e) &&  e.canDoUploadSponsor();
	}


	/*
	*	1) evento terminato
	*	2) evento non è stato già pagato
	*	3) siamo ancora entro i 90 gg dalla fine dell'evento
	*	4) passati i 90 gg -> non è più possibile pagare
	*/
	public boolean canDoPagamento(Evento e){
		return accreditamentoService.canProviderWorkWithEvent(e.getProvider().getId(), e) &&  e.canDoPagamento();
	}

	/*
	 * Il tasto appare solo a evento terminato
	 *
	 */
	public boolean canDoRendicontazione(Evento e){
		return accreditamentoService.canProviderWorkWithEvent(e.getProvider().getId(), e) &&  e.canDoRendicontazione();
	}

}
