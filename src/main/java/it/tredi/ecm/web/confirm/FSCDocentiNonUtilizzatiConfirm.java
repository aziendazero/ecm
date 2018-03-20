package it.tredi.ecm.web.confirm;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.web.bean.EventoWrapper;

public class FSCDocentiNonUtilizzatiConfirm implements IConfirm {
	private boolean confirmRequired = false;
	private Set<RuoloFSCEnum> ruoliDocentiNonUtilizzati = null;

	public FSCDocentiNonUtilizzatiConfirm(EventoWrapper eventoWrapper) {
		if(eventoWrapper.getEvento() instanceof EventoFSC) {
			ruoliDocentiNonUtilizzati = new HashSet<RuoloFSCEnum>();
			ruoliDocentiNonUtilizzati.addAll(eventoWrapper.getListRuoloFSCEnumPerEspertiDocenti());
			ruoliDocentiNonUtilizzati.addAll(eventoWrapper.getListRuoloFSCEnumPerResponsabiliScientificiDocenti());
			ruoliDocentiNonUtilizzati.addAll(eventoWrapper.getListRuoloFSCEnumPerCoordinatoriDocenti());
			
			Set<RuoloFSCEnum> ruoliInEventi = eventoWrapper.getRiepilogoRuoliFSC().keySet();
			ruoliDocentiNonUtilizzati.removeAll(ruoliInEventi);
			
			if(!ruoliDocentiNonUtilizzati.isEmpty()) {
				confirmRequired = true;
			}
		}
	}
	
	@Override
	public boolean isConfirmRequired() {
		return confirmRequired;
	}
	
	public String getRuoliDocenzaNonUtilizzati() {
		String toRet = "";
		boolean write = false;
		for(RuoloFSCEnum ruolo : ruoliDocentiNonUtilizzati) {
			if(write)
				toRet += ", ";
			toRet += "\"" + ruolo.getNome() + "\"";
			write = true;
		}
		return toRet;
	}

	public boolean isMultiDocenti() {
		return ruoliDocentiNonUtilizzati != null && ruoliDocentiNonUtilizzati.size() > 1;
	}
}
