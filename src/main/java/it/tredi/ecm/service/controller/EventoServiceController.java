package it.tredi.ecm.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;

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
			//determino la versione in funzione della data di inizio verificando gli intervalli stabiliti da property
			if(evento.getDataInizio() != null) {
				if(Utils.isDateAfterIncluded(evento.getDataInizio(), ecmProperties.getEventoDataPassaggioVersioneTre())) {
					versione = EventoVersioneEnum.TRE_DAL_2019;
				}else if(Utils.isDateAfterIncluded(evento.getDataInizio(), ecmProperties.getEventoDataPassaggioVersioneDue())) {
					versione = EventoVersioneEnum.DUE_DAL_2018;
				} else {
					versione = EventoVersioneEnum.UNO_PRIMA_2018;
				}
			}
		}
		return versione;
	}

	/* i metodi che prendono l'ENUM come parametro sono chiamati lato server (.java) */
	public boolean isVersionUno(EventoVersioneEnum versione) {
		return (versione != null && versione == EventoVersioneEnum.UNO_PRIMA_2018);
	}

	public boolean isVersionDue(EventoVersioneEnum versione) {
		return (versione != null && versione == EventoVersioneEnum.DUE_DAL_2018);
	}

	public boolean isVersionDueOrHigh(EventoVersioneEnum versione) {
		return (versione != null && (versione == EventoVersioneEnum.DUE_DAL_2018 || versione == EventoVersioneEnum.TRE_DAL_2019));
	}

	public boolean isVersionTre(EventoVersioneEnum versione) {
		return (versione != null && versione == EventoVersioneEnum.TRE_DAL_2019);
	}

	/* i metodi che prendono l'evento come parametro sono chiamati lato client (.html)*/
	public boolean isVersionUno(Evento evento) {
		return versioneEvento(evento) == EventoVersioneEnum.UNO_PRIMA_2018;
	}

	public boolean isVersionDue(Evento evento) {
		return versioneEvento(evento) == EventoVersioneEnum.DUE_DAL_2018;
	}

	public boolean isVersionDueOrHigh(Evento evento) {
		return (versioneEvento(evento) == EventoVersioneEnum.DUE_DAL_2018 || versioneEvento(evento) == EventoVersioneEnum.TRE_DAL_2019);
	}

	public boolean isVersionTre(Evento evento) {
		return versioneEvento(evento) == EventoVersioneEnum.TRE_DAL_2019;
	}

	public boolean fadDisableSupportoSvoltoDaEsperto(EventoFAD evento) {
		if(isVersionDueOrHigh(evento) && evento.getTipologiaEventoFAD() == TipologiaEventoFADEnum.EVENTI_SEMINARIALI_IN_RETE) {
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

	/*
	 * Il tasto appare solo a evento VALIDATO
	 *
	 * */
	public boolean canDoMarcaNoEcm(Evento e){
		return accreditamentoService.canProviderWorkWithEvent(e.getProvider().getId(), e) &&  e.canDoMarcaNoECM();
	}

}
