package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RiepilogoRuoliFSC {
	private RuoloFSCEnum ruolo;
	private float tempoDedicato;
	private float crediti;
	private int numeroPartecipanti;

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
	
	public RiepilogoRuoliFSC(RuoloFSCEnum ruolo, float tempoDedicato, float crediti, int numeroPartecipanti){
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
		this.crediti = crediti;
		this.numeroPartecipanti = numeroPartecipanti;
	}

	public void clear(){
		this.tempoDedicato = 0.0f;
		this.crediti = 0.0f;
		this.numeroPartecipanti = 0;
	}

	public void addTempo(float tempo){
		this.tempoDedicato += tempo;
	}

	public void addCrediti(float crediti){
		this.crediti += crediti;
	}

	public void calcolaCrediti(TipologiaEventoFSCEnum tipologiaEvento, int maxValue){
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
				switch(ruolo.getRuoloBase())
				{					
					case PARTECIPANTE: crediti = 1*tempoDedicato;
										if(crediti > maxValue)
											crediti = maxValue;
						break;
					
					case TUTOR: crediti = 1*(tempoDedicato/6);
						break;
						
					case ESPERTO: crediti = 1*tempoDedicato;
									if(crediti > maxValue)
										crediti = maxValue;
						break;
						
					case COORDINATORE: crediti = 1*tempoDedicato;
										if(crediti > maxValue)
											crediti = maxValue;
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
	
	@Override
	public boolean equals(Object obj) {
		RiepilogoRuoliFSC second = (RiepilogoRuoliFSC)obj;
		
		if(second == null)
			return false;
		
		if(second.getRuolo() == this.ruolo){
			return true;
		}else{
			return false;
		}
	}
}
