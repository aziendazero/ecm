package it.tredi.ecm.service.component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.RiepilogoFAD;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.enumlist.ProgettiDiMiglioramentoFasiDaInserireFSCEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;

@Component
public class EventoDurataVersioneTre {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventoDurataVersioneTre.class);

	public float calcoloDurataEvento(EventoWrapper eventoWrapper) {
		float durata = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			//durata = calcoloDurataEventoRES(eventoWrapper.getProgrammaEventoRES());
			durata = calcoloDurataEventoRES(eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values(), ((EventoRES)eventoWrapper.getEvento()).getTipologiaEventoRES());
			((EventoRES)eventoWrapper.getEvento()).setDurata(durata);
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			durata = calcoloDurataEventoFSC(eventoWrapper);
			((EventoFSC)eventoWrapper.getEvento()).setDurata(durata);
			//calcolo partecipanti
			int numPartecipanti = calcolaNumeroRuoloFSC(RuoloFSCBaseEnum.PARTECIPANTE, eventoWrapper.getRiepilogoRuoliFSC());
			eventoWrapper.getEvento().setNumeroPartecipanti(numPartecipanti);
			//calcolo tutor
			int numTutor = calcolaNumeroRuoloFSC(RuoloFSCBaseEnum.TUTOR, eventoWrapper.getRiepilogoRuoliFSC());
			((EventoFSC) eventoWrapper.getEvento()).setNumeroTutor(numTutor);
		}else if(eventoWrapper.getEvento() instanceof EventoFAD){
			durata = calcoloDurataEventoFAD(eventoWrapper.getProgrammaEventoFAD(), ((EventoFAD)eventoWrapper.getEvento()).getRiepilogoFAD());
			((EventoFAD)eventoWrapper.getEvento()).setDurata(durata);
		}

		durata = Utils.getRoundedFLOORFloatValue(durata, 2);
		return durata;
	}

	private float calcoloDurataEventoRES(Collection<EventoRESProgrammaGiornalieroWrapper> programma, TipologiaEventoRESEnum tipologiaEventoRES){
		float durata = 0;
		long durataMinuti = 0;

		long durataMinutiCondivisioneEsitiValutazione = 0;

		if(programma != null){
			for(EventoRESProgrammaGiornalieroWrapper progrGior : programma){
				for(DettaglioAttivitaRES dett : progrGior.getProgramma().getProgramma()){
					if(!dett.isExtraType()) {
//						durata += dett.getOreAttivita();
						durataMinuti += dett.getMinutiAttivita();
					}else {
						if(dett.isCondivisioneEsitiValutazione())
							durataMinutiCondivisioneEsitiValutazione += dett.getMinutiAttivita();
					}
				}
			}
		}

		//sotto le 5 ore di docenza non consideriamo nulla per la condivisioneEsitiValutazione
		//se ci sono le ore di docenza verifichiamo l'incremento per la condivisioneEsitiValutazione, in linea con la tabella fornita
		if((tipologiaEventoRES == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO || tipologiaEventoRES == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO) && durataMinutiCondivisioneEsitiValutazione > 0 && durataMinuti >= 300) {
			if(durataMinuti < 600) { // <10 ore -> max 30 minuti di condivisione
				if(durataMinutiCondivisioneEsitiValutazione > 30)
					durataMinutiCondivisioneEsitiValutazione = 30;
			}else if(durataMinuti < 1200) {// < 20ore -> max 60 minuti di condivisione
				if(durataMinutiCondivisioneEsitiValutazione > 60)
					durataMinutiCondivisioneEsitiValutazione = 60;
			}else if(durataMinuti < 1800) {// < 30ore -> max 120 minuti di condivisione
				if(durataMinutiCondivisioneEsitiValutazione > 120)
					durataMinutiCondivisioneEsitiValutazione = 120;
			}else if(durataMinuti >= 1800) {// >= 30ore -> max 180 minuti di condivisione
				if(durataMinutiCondivisioneEsitiValutazione > 180)
					durataMinutiCondivisioneEsitiValutazione = 180;
			}

			durataMinuti += durataMinutiCondivisioneEsitiValutazione;
		}

		durata = (float) durataMinuti / 60;
		durata = Utils.getRoundedFLOORFloatValue(durata, 2);

		return durata;
	}

	private float calcoloDurataEventoFSC(EventoWrapper eventoWrapper){
		float durata = 0;

		prepareRiepilogoRuoli(eventoWrapper);
		durata = getMaxDurataPatecipanti(eventoWrapper.getRiepilogoRuoliFSC());

		durata = Utils.getRoundedFLOORFloatValue(durata, 2);
		return durata;
	}

	/*
	 * Ragruppo i Ruoli coinvolti in una mappa <Ruolo,RiepilogoRuoloOreFSC>
	 * dove il RiepilogoRuoloOreFSC avra la somma delle ore dei ruoli
	 * */
	private void prepareRiepilogoRuoli(EventoWrapper eventoWrapper){
		if(eventoWrapper.getRiepilogoRuoliFSC() != null)
		{
			Set<RuoloFSCEnum> ruoliUsati = new HashSet<RuoloFSCEnum>();

			Iterator<Entry<RuoloFSCEnum, RiepilogoRuoliFSC>> iterator = eventoWrapper.getRiepilogoRuoliFSC().entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				pairs.getValue().setTempoDedicato(0f);
				pairs.getValue().setCrediti(0f);
				if(pairs.getValue().getRuolo() == null)
					iterator.remove();
			}

			//dpranteda - 17/01/2018: bugfix risolto, nel caso di modifica alle fasi attive non si aggiornavano tutti i calcoli
			EventoFSC eventoFSC = ((EventoFSC)eventoWrapper.getEvento());
			TipologiaEventoFSCEnum tipologiaEventoFSC = eventoFSC.getTipologiaEventoFSC();
			ProgettiDiMiglioramentoFasiDaInserireFSCEnum fasiDaInserire = eventoFSC.getFasiDaInserire();

			for(FaseAzioniRuoliEventoFSCTypeA fase : eventoWrapper.getProgrammaEventoFSC()){
				if(tipologiaEventoFSC != null && (tipologiaEventoFSC != TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO || ProgettiDiMiglioramentoFasiDaInserireFSCEnum.faseAbilitata(fasiDaInserire, fase.getFaseDiLavoro()))) {
					for(AzioneRuoliEventoFSC azione : fase.getAzioniRuoli()){
						for(RuoloOreFSC ruolo : azione.getRuoli())
						{
							ruoliUsati.add(ruolo.getRuolo());

							if(eventoWrapper.getRiepilogoRuoliFSC().containsKey(ruolo.getRuolo())){
								RiepilogoRuoliFSC r = eventoWrapper.getRiepilogoRuoliFSC().get(ruolo.getRuolo());
								float tempoDedicato = ruolo.getTempoDedicato() != null ? ruolo.getTempoDedicato() : 0.0f;
								r.addTempo(tempoDedicato);
							}else{
								float tempoDedicato = ruolo.getTempoDedicato() != null ? ruolo.getTempoDedicato() : 0.0f;
								RiepilogoRuoliFSC r = new RiepilogoRuoliFSC(ruolo.getRuolo(), tempoDedicato, 0.0f);
								eventoWrapper.getRiepilogoRuoliFSC().put(ruolo.getRuolo(), r);
							}
						}
					}
				}
			}

			iterator = eventoWrapper.getRiepilogoRuoliFSC().entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(!ruoliUsati.contains(pairs.getValue().getRuolo()))
					iterator.remove();
			}
		}
	}

	private float getMaxDurataPatecipanti(Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float max = 0.0f;

		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE && pairs.getValue().getTempoDedicato() > max)
					max = pairs.getValue().getTempoDedicato();
			 }
		}

		return max;
	}

	private int calcolaNumeroRuoloFSC(RuoloFSCBaseEnum ruolo, Map<RuoloFSCEnum, RiepilogoRuoliFSC> riepilogoRuoliMap) {
		int counter = 0;
		if(riepilogoRuoliMap != null){
			for(RiepilogoRuoliFSC rrf : riepilogoRuoliMap.values()) {
				if(rrf.getRuolo() != null && rrf.getRuolo().getRuoloBase() == ruolo) {
					counter = counter + rrf.getNumeroPartecipanti();
				}
			}
		}
		return counter;
	}

	private float calcoloDurataEventoFAD(List<DettaglioAttivitaFAD> programma, RiepilogoFAD riepilogoFAD){
		float durata = 0;
		riepilogoFAD.clear();

		if(programma != null){
			for(DettaglioAttivitaFAD dett : programma){
				durata += dett.getOreAttivita();

				//popolo la lista di obiettivi
				if(dett.getObiettivoFormativo() != null)
					riepilogoFAD.getObiettivi().add(dett.getObiettivoFormativo());

				//popolo la lista di metodologie con annesso calcolo di ore
				if(dett.getMetodologiaDidattica() != null){
					if(riepilogoFAD.getMetodologie().containsKey(dett.getMetodologiaDidattica())){
						float ore = riepilogoFAD.getMetodologie().get(dett.getMetodologiaDidattica());
						riepilogoFAD.getMetodologie().put(dett.getMetodologiaDidattica(), ore + dett.getOreAttivita());
					}else{
						riepilogoFAD.getMetodologie().put(dett.getMetodologiaDidattica(), dett.getOreAttivita());
					}
				}
			}
		}

		durata = Utils.getRoundedFLOORFloatValue(durata, 2);
		return durata;
	}

}
