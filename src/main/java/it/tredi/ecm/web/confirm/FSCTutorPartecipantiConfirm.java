package it.tredi.ecm.web.confirm;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;

public class FSCTutorPartecipantiConfirm implements IConfirm {
	private boolean confirmRequired = false;

	public FSCTutorPartecipantiConfirm(Evento evento) {
		if(evento instanceof EventoFSC) {
			EventoFSC eventoFsc = (EventoFSC) evento;
			if(eventoFsc.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO
					&& eventoFsc.getNumeroPartecipanti() > (eventoFsc.getNumeroTutor() * 5))
				confirmRequired = true;
		}
	}
	
	@Override
	public boolean isConfirmRequired() {
		return confirmRequired;
	}

}
