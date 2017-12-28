package it.tredi.ecm.service.component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.RiepilogoRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoCreditiVersioneDue {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoCreditiVersioneDue.class);

	public float calcoloCreditiEvento(EventoWrapper eventoWrapper) throws Exception {
		float crediti = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			EventoRES evento = ((EventoRES)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoRES(evento.getTipologiaEventoRES(), evento.getDurata(), eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values(), evento.getNumeroPartecipanti(), evento.getRiepilogoRES());
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento RES"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			EventoFSC evento = ((EventoFSC)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoFSC(evento, eventoWrapper);
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento FSC"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFAD){
			EventoFAD evento = ((EventoFAD)eventoWrapper.getEvento());
			//crediti = calcoloCreditiFormativiEventoFAD(evento.getDurata(), evento.getSupportoSvoltoDaEsperto());
			crediti = calcoloCreditiFormativiEventoFAD(evento);
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento FAD"));
			return crediti;
		}

		return crediti;
	}
	
	private float calcoloCreditiFormativiEventoRES(TipologiaEventoRESEnum tipologiaEvento, float durata, Collection<EventoRESProgrammaGiornalieroWrapper> programma, Integer numeroPartecipanti, RiepilogoRES riepilogoRES){
		float crediti = 0.0f;
		float oreFrontale = 0f;
		long minutiFrontale = 0;
		float oreInterattiva = 0f;
		long minutiInterattiva = 0;

		riepilogoRES.clear();

		for(EventoRESProgrammaGiornalieroWrapper progrGio : programma) {
			for(DettaglioAttivitaRES a : progrGio.getProgramma().getProgramma()){
				if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.FRONTALE){
//					oreFrontale += a.getOreAttivita();
					minutiFrontale += a.getMinutiAttivita();
				}else if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.INTERATTIVA){
//					oreInterattiva += a.getOreAttivita();
					minutiInterattiva += a.getMinutiAttivita();
				}

				//popolo la lista di obiettivi formativi utilizzati
				if(a.getObiettivoFormativo() != null)
					riepilogoRES.getObiettivi().add(a.getObiettivoFormativo());

				//popolo la lista di metodologie con annesso calcolo di ore
				if(a.getMetodologiaDidattica() != null){
					if(riepilogoRES.getMetodologie().containsKey(a.getMetodologiaDidattica())){
						float ore = riepilogoRES.getMetodologie().get(a.getMetodologiaDidattica());
						riepilogoRES.getMetodologie().put(a.getMetodologiaDidattica(), ore + a.getOreAttivita());
					}else{
						riepilogoRES.getMetodologie().put(a.getMetodologiaDidattica(), a.getOreAttivita());
					}
				}
			}
		}

		oreFrontale = (float) minutiFrontale / 60;
		oreFrontale = Utils.getRoundedFloatValue(oreFrontale, 2);
		oreInterattiva = (float) minutiInterattiva / 60;
		oreInterattiva = Utils.getRoundedFloatValue(oreInterattiva, 2);

		riepilogoRES.setTotaleOreFrontali(oreFrontale);
		riepilogoRES.setTotaleOreInterattive(oreInterattiva);

		//approssimazione per calcolo con HALF_DOWN (2.5 -> 2 || 2.6 -> 3)
		durata = Utils.getRoundedHALFDOWNFloatValue(durata);
		oreFrontale = Utils.getRoundedHALFDOWNFloatValue(oreFrontale);
		oreInterattiva = Utils.getRoundedHALFDOWNFloatValue(oreInterattiva);

		if(tipologiaEvento == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO){
			crediti = (0.20f * (int) durata);
			if(crediti > 5.0f)
				crediti = 5.0f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO){
			crediti = 1 * (int) durata;
			if(crediti > 50f)
				crediti = 50f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO){
			float creditiFrontale = 0f;
			float creditiInterattiva = 0f;

			//metodologia frontale
			numeroPartecipanti = numeroPartecipanti!= null ? numeroPartecipanti.intValue() : 0;

			if(numeroPartecipanti >=1 && numeroPartecipanti <=20){
				creditiFrontale = (int) oreFrontale * 1.25f;
			}else if(numeroPartecipanti >=21 && numeroPartecipanti <= 50){
				float creditiDecrescenti = getQuotaFasciaDecrescenteForRES(numeroPartecipanti);
				creditiFrontale = (int) oreFrontale * creditiDecrescenti;
			}else if(numeroPartecipanti >=51 && numeroPartecipanti <=100){
				creditiFrontale = (int) oreFrontale * 1.0f;
			}else if(numeroPartecipanti >= 101 && numeroPartecipanti <= 150){
				creditiFrontale = (int) oreFrontale* 0.75f;
			}else if(numeroPartecipanti >= 151 && numeroPartecipanti <= 200){
				creditiFrontale = (int) oreFrontale * 0.5f;
			}

			//metodologia interattiva
			creditiInterattiva = (int) oreInterattiva * 1.5f;

			crediti = creditiFrontale + creditiInterattiva;

			if(crediti > 50f)
				crediti = 50f;
		}

		crediti = Utils.getRoundedFloatValue(crediti, 1);

		return crediti;
	}

	private float getQuotaFasciaDecrescenteForRES(int numeroPartecipanti){
		switch (numeroPartecipanti){
			case 21: return 1.24f;
			case 22: return 1.23f;
			case 23: return 1.23f;
			case 24: return 1.22f;
			case 25: return 1.21f;
			case 26: return 1.20f;
			case 27: return 1.19f;
			case 28: return 1.19f;
			case 29: return 1.18f;
			case 30: return 1.17f;
			case 31: return 1.16f;
			case 32: return 1.15f;
			case 33: return 1.15f;
			case 34: return 1.14f;
			case 35: return 1.13f;
			case 36: return 1.12f;
			case 37: return 1.11f;
			case 38: return 1.10f;
			case 39: return 1.10f;
			case 40: return 1.08f;
			case 41: return 1.08f;
			case 42: return 1.07f;
			case 43: return 1.06f;
			case 44: return 1.06f;
			case 45: return 1.05f;
			case 46: return 1.04f;
			case 47: return 1.03f;
			case 48: return 1.02f;
			case 49: return 1.02f;
			case 50: return 1.01f;

			default: return 0.0f;
		}

	}
	
	private float calcoloCreditiFormativiEventoFSC(EventoFSC evento, EventoWrapper wrapper){
		float crediti = 0.0f;
		
		calcolaCreditiPartecipantiFSC(evento, wrapper.getRiepilogoRuoliFSC());
		crediti = getMaxCreditiPartecipantiFSC(wrapper.getRiepilogoRuoliFSC());
		calcolaCreditiAltriRuoliFSC(evento, wrapper.getRiepilogoRuoliFSC(),crediti);

		return crediti;
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> calcolo i crediti dei PARTECIPANTI
	 * */
	private void calcolaCreditiPartecipantiFSC(EventoFSC evento, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		//TipologiaEventoFSCEnum tipologia = evento.getTipologiaEventoFSC();
		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(pairs.getKey() != null && pairs.getKey().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE)
					pairs.getValue().calcolaCreditiVersioneDue(evento,0f);
			 }
		}
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> calcolo i crediti degli altri RUOLI
	 * */
	private void calcolaCreditiAltriRuoliFSC(EventoFSC evento, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC, float maxValue){
		//TipologiaEventoFSCEnum tipologia = evento.getTipologiaEventoFSC();
		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() != RuoloFSCBaseEnum.PARTECIPANTE)
					pairs.getValue().calcolaCreditiVersioneDue(evento,maxValue);
			 }
		}
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> individuo il valore MAX numero crediti attribuito a un PARTECIPANTE
	 * */
	private float getMaxCreditiPartecipantiFSC(Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float max = 0.0f;

		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE && pairs.getValue().getCrediti() > max)
					max = pairs.getValue().getCrediti();
			 }
		}

		return max;
	}
	
	private float calcoloCreditiFormativiEventoFAD(EventoFAD evento){
		//crediti = calcoloCreditiFormativiEventoFAD(evento.getDurata(), evento.getSupportoSvoltoDaEsperto());
		float crediti = 0.0f;
		float durata = Utils.getRoundedHALFDOWNFloatValue(evento.getDurata());
		
		if(evento.getTipologiaEventoFAD() != null) {
			switch (evento.getTipologiaEventoFAD()) {
			case APPRENDIMENTO_INDIVIDUALE_NO_ONLINE:
				crediti = (int) durata * 1.0f;
				break;
			case APPRENDIMENTO_INDIVIDUALE_SI_ONLINE:
			case APPRENDIMENTO_CONTESTO_SOCIALE:
				if(evento.getSupportoSvoltoDaEsperto() != null && evento.getSupportoSvoltoDaEsperto())
					crediti = (int) durata * 1.5f;
				else
					crediti = (int) durata * 1.0f;
				break;
			case EVENTI_SEMINARIALI_IN_RETE:
				crediti = (int) durata * 1.5f;
				break;
			}
		}

		if(crediti > 50f)
			crediti = 50f;
		crediti = Utils.getRoundedFloatValue(crediti, 1);
		return crediti;
	}

}
