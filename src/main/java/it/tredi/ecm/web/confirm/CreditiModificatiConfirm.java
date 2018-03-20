package it.tredi.ecm.web.confirm;

import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;

public class CreditiModificatiConfirm implements IConfirm {
	private boolean confirmRequired = false;
	
	private Boolean eventoConfermatiCrediti;
	private Float oldValueCrediti;
	private Float newValueCrediti;
	private Float creditiProposti;
	
	public CreditiModificatiConfirm(EventoWrapper eventoWrapper) {
		if(Utils.getAuthenticatedUser().isSegreteria() &&
				eventoWrapper.getEvento().isValidatorCheck() &&
				(eventoWrapper.getCreditiOld() !=  eventoWrapper.getEvento().getCrediti() || eventoWrapper.getEvento().getConfermatiCrediti() == false)) {
			//la segreteria potrebbe aver modificato i crediti dell'evento (già accreditato) e va notificato
			confirmRequired = true;
			eventoConfermatiCrediti = eventoWrapper.getEvento().getConfermatiCrediti();
			oldValueCrediti = eventoWrapper.getCreditiOld();
			newValueCrediti = eventoWrapper.getEvento().getCrediti();
			creditiProposti = eventoWrapper.getCreditiProposti();
		}
	}
	
	@Override
	public boolean isConfirmRequired() {
		return confirmRequired;
	}

	public void setConfirmRequired(boolean confirmRequired) {
		this.confirmRequired = confirmRequired;
	}

	public Boolean getEventoConfermatiCrediti() {
		return eventoConfermatiCrediti;
	}

	public void setEventoConfermatiCrediti(Boolean eventoConfermatiCrediti) {
		this.eventoConfermatiCrediti = eventoConfermatiCrediti;
	}

	public Float getOldValueCrediti() {
		return oldValueCrediti;
	}

	public void setOldValueCrediti(Float oldValueCrediti) {
		this.oldValueCrediti = oldValueCrediti;
	}

	public Float getNewValueCrediti() {
		return newValueCrediti;
	}

	public void setNewValueCrediti(Float newValueCrediti) {
		this.newValueCrediti = newValueCrediti;
	}

	public Float getCreditiProposti() {
		return creditiProposti;
	}

	public void setCreditiProposti(Float creditiProposti) {
		this.creditiProposti = creditiProposti;
	}
	
}
