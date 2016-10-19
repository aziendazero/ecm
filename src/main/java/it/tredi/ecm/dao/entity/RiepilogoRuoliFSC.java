package it.tredi.ecm.dao.entity;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiepilogoRuoliFSC {
	private RuoloFSCEnum ruolo;
	private float tempoDedicato;
	private float crediti;

	public RiepilogoRuoliFSC(){}
	public RiepilogoRuoliFSC(RuoloFSCEnum ruolo){
		this.ruolo = ruolo;
		this.tempoDedicato = 0.0f;
		this.crediti = 0.0f;
	}

	public RiepilogoRuoliFSC(RuoloFSCEnum ruolo, float tempoDedicato, float crediti){
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
		this.crediti = crediti;
	}

	public void clear(){
		this.tempoDedicato = 0.0f;
		this.crediti = 0.0f;
	}

	public void addTempo(float tempo){
		this.tempoDedicato += tempo;
	}

	public void addCrediti(float credit){
		this.crediti += credit;
	}

	public void calcolaCrediti(TipologiaEventoFSCEnum tipologiaEvento){
		if(tipologiaEvento != null){
			switch(tipologiaEvento){
			case TRAINING_INDIVIDUALIZZATO: 
			{
				/*
				 * PARTECIPANTI:	1 credito ogni ora (max 30)
				 * TUTOR:			1 credito ogni 6 ore
				 * ESPERTO:			1 credito ongi ora (max 'crediti evento')
				 * COORDINATORE		1 credito ongi ora (max 'crediti evento')
				 * 
				 * */	
				switch(ruolo)
				{					
					case PARTECIPANTE: crediti = 1*tempoDedicato;
										if(crediti > 30)
											crediti = 30;
						break;
					
					case TUTOR: crediti = 1*(tempoDedicato/6);
						break;
						
					case ESPERTO: crediti = 1*tempoDedicato;
						break;
						
					case COORDINATORE: crediti = 1*tempoDedicato;
						break;
						
					default:	crediti = 0.0f;
						break;
				}
			}
			break;

			default: break;
			}
		}else{
			crediti = 0.0f;
		}

	}
}
