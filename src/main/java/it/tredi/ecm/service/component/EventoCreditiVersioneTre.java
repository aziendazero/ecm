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
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.RiepilogoRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.enumlist.NumeroPartecipantiPerCorsoEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TematicheInteresseEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoCreditiVersioneTre {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoCreditiVersioneTre.class);

	public float calcoloCreditiEvento(EventoWrapper eventoWrapper) throws Exception {
		float crediti = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			EventoRES evento = ((EventoRES)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoRES(evento.getTipologiaEventoRES(), evento.getDurata(), eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values(), evento.getNumeroPartecipanti(), evento.getRiepilogoRES(), evento.getNumeroPartecipantiPerCorso(), evento.getTematicaInteresse());
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

	private float calcoloCreditiFormativiEventoRES(TipologiaEventoRESEnum tipologiaEvento, float durata, Collection<EventoRESProgrammaGiornalieroWrapper> programma, Integer numeroPartecipanti, RiepilogoRES riepilogoRES, NumeroPartecipantiPerCorsoEnum numeroPartecipantiPerCorso, TematicheInteresseEnum tematicaInteresse){
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

		/*
		 * 0.3 crediti ogni ora non frazionabili
		 * MAX CREDITI 6
		 * */
		if(tipologiaEvento == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO){
			crediti = (0.30f * (int) durata);
			if(crediti > 6.0f)
				crediti = 6.0f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO ||
				tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO) {

			float extraCrediti = 0.0f;
			numeroPartecipanti = numeroPartecipanti != null ? numeroPartecipanti.intValue() : 0;

			if(numeroPartecipanti <= 25)
				extraCrediti += 0.3f;

			/* nel caso in cui i partecipanti siano 26 - 50 deve essere rispettata la proporzione docenti:discenti 1:25
			 * per usufruire di questi extra crediti
			 * questo controllo viene comunque fatto dal validatore,
			 * quindi do per scontato che sia rispettato */
			if(numeroPartecipanti <= 50 && oreInterattiva > 0.0f) {
				extraCrediti += 0.3f;
			}

			/*
			 * WORKSHOP_SEMINARIO
			 *
			 * 0.7 crediti ogni ora non frazionabili
			 * + extraCrediti cumulabili
			 * MAX CREDITI 50
			 * */
			if(tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO){
				crediti = (0.70f + extraCrediti) * (int) durata;
				if(crediti > 50f)
					crediti = 50f;
			}

			/*
			 * CORSO_AGGIORNAMENTO
			 *
			 * CORSO_AGGIORNAMENTO_FINO_100_PARTECIPANTI 		-> 1.0 crediti ogni ora non frazionabili
			 * CORSO_AGGIORNAMENTO_DA_101_A_200_PARTECIPANTI 	-> 0.7 crediti ogni ora non frazionabili
			 * + extraCrediti cumulabili
			 * MAX CREDITI 50
			 * */
			if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO){

				//CORSO_AGGIORNAMENTO_FINO_100_PARTECIPANTI 		-> 1.0 crediti ogni ora non frazionabili
				//CORSO_AGGIORNAMENTO_DA_101_A_200_PARTECIPANTI 	-> 0.7 crediti ogni ora non frazionabili
				float creditiOra = 0.0f;
				if(numeroPartecipantiPerCorso == NumeroPartecipantiPerCorsoEnum.CORSO_AGGIORNAMENTO_FINO_100_PARTECIPANTI)
					creditiOra = 1.0f;
				else if(numeroPartecipantiPerCorso == NumeroPartecipantiPerCorsoEnum.CORSO_AGGIORNAMENTO_DA_101_A_200_PARTECIPANTI)
					creditiOra = 0.7f;

				crediti = (creditiOra + extraCrediti) * (int) durata;
				if(crediti > 50f)
					crediti = 50f;
			}
		}

		/* +0.3 crediti ogni ora non frazionabili se è stato selezionato una tematica di interesse nazionale o regionale */
		if(tematicaInteresse != null && tematicaInteresse != TematicheInteresseEnum.NON_RIGUARDA_UNA_TEMATICA_SPECIALE)
			crediti += (0.30f * (int) durata);

		crediti = Utils.getRoundedFloatValue(crediti, 1);

		return crediti;
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
		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(pairs.getKey() != null && pairs.getKey().getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE)
					pairs.getValue().calcolaCreditiVersioneTre(evento,0f);
			 }
		}
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> calcolo i crediti degli altri RUOLI
	 * */
	private void calcolaCreditiAltriRuoliFSC(EventoFSC evento, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC, float maxValue){
		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() != RuoloFSCBaseEnum.PARTECIPANTE)
					pairs.getValue().calcolaCreditiVersioneTre(evento,maxValue);
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

		/* +0.3 crediti ogni ora non frazionabili se è stato selezionato una tematica di interesse nazionale o regionale */
		if(evento.getTematicaInteresse() != null && evento.getTematicaInteresse() != TematicheInteresseEnum.NON_RIGUARDA_UNA_TEMATICA_SPECIALE)
			crediti += (0.30f * (int) durata);

		if(crediti > 50f)
			crediti = 50f;
		crediti = Utils.getRoundedFloatValue(crediti, 1);
		return crediti;
	}

}
